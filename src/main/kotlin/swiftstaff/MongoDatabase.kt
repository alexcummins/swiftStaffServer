package swiftstaff

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.mongodb.client.gridfs.GridFSUploadStream
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import kotlin.reflect.KClass


object MongoDatabase :  Database {
    private val mongoClient: MongoClient
    private val connectionString: ConnectionString
    val db: MongoDatabase
    val imageBucket: GridFSBucket

    init {
        val username: String = System.getenv("DB_USERNAME") ?: "mongoadmin"
        val password: String = System.getenv("DB_PASSWORD") ?: "mongoadmin"
        val server: String = System.getenv("SERVER_ADDRESS") ?: "127.0.0.1"
//        val username: String = System.getenv("DB_USERNAME") ?: "superuser"
//        val password: String = System.getenv("DB_PASSWORD") ?: "changeMeToAStrongPassword"
//        val server: String = System.getenv("SERVER_ADDRESS") ?: "localhost:27017"
        val databaseName: String = System.getenv("DATABASE") ?: "test"
        this.connectionString = ConnectionString("mongodb://$username:$password@$server")
        this.mongoClient = KMongo.createClient(connectionString)
        this.db = this.mongoClient.getDatabase(databaseName)
        this.imageBucket = GridFSBuckets.create(this.db, "images")
    }

    // Implementation follows Kotlin generics for inline virtual methods and interfaces

    override fun < T : Any> find(filter: Bson?, classParam: KClass<T>, collection: MongoCollection<T>): MutableList<T> {
        val returnList = mutableListOf<T>()
        if (filter != null) {
            returnList.addAll(collection.find(filter))
        } else {
            returnList.addAll(collection.find())
        }
        return returnList;
    }

    override fun <T : Any> insert(data: T, classParam: KClass<T>, collection: MongoCollection<T>): Boolean{
        val result = collection.insertOne(data)
        // On successful insert insert ID is added to data class _id field
        return result.wasAcknowledged()
    }

    override fun <T : Any> update(data: T, filter: Bson,  classParam: KClass<T>, collection: MongoCollection<T>): Boolean{
        return collection.updateOne(filter, data).wasAcknowledged()
    }

    fun upload(userId: String, resourceName: String) :String {
        try {

            // Delete previous version(s) of the resource
            val imageCursor = imageBucket.find(eq("filename" , "$userId/$resourceName.jpg")).iterator()
            while(imageCursor.hasNext()) {
                val imageFile = imageCursor.next()
                imageBucket.delete(imageFile.objectId)
            }

            // Create upload buffer for the first time
            val file = File("ubuffer/$resourceName.jpg")
            file.parentFile.mkdirs()
            file.createNewFile()

            // Upload file from ubuffer/$resourceName to $userId/$resourceName in GridFS
            val uploadStream: GridFSUploadStream = imageBucket.openUploadStream("$userId/$resourceName.jpg")
            val data = Files.readAllBytes(File("ubuffer/$resourceName.jpg").toPath())
            uploadStream.write(data)
            uploadStream.close()

            return uploadStream.objectId.toHexString()
        } catch (e: IOException) {
            println("Upload Exception Caught")
            println(e.message)
        }
        return ""
    }

    fun download(fileId: String, resourceName: String) {
        try {

            // Create download buffer for the first time
            val file = File("dbuffer/$resourceName.jpg")
            file.parentFile.mkdirs()
            file.createNewFile()

            // Download fileId from GridFS to the buffer dbuffer/$resourceName.
            val streamToDownloadTo = FileOutputStream("dbuffer/$resourceName.jpg")
            imageBucket.downloadToStream(ObjectId(fileId), streamToDownloadTo)
            streamToDownloadTo.close()
        } catch (e: IOException) {
            println("Download Exception Caught")
            println(e.message)
        }
    }

    inline fun <reified T : Any> insert(data: T): Boolean = insert(data, T::class, this.db.getCollection<T>())

    inline fun <reified T : Any> find(filter: Bson? = null): MutableList<T> = find(filter, T::class, this.db.getCollection<T>())

    inline fun <reified T : Any> update(data: T, filter: Bson? = null): Boolean {
        return if (filter != null) {
            update(data, filter, T::class, this.db.getCollection<T>())
        } else {
             this.db.getCollection<T>().updateOne(data).wasAcknowledged()
        }
    }

}
