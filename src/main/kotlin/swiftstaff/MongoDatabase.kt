package swiftstaff
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.UpdateResult
import org.bson.conversions.Bson
import org.litote.kmongo.*
import kotlin.reflect.KClass

object MongoDatabase :  Database {
    private val mongoClient: MongoClient
    private val connectionString: ConnectionString
    val db: MongoDatabase

    init {
        val username: String = System.getenv("DB_USERNAME") ?: "mongoadmin"
        val password: String = System.getenv("DB_PASSWORD") ?: "mongoadmin"
        val server: String = System.getenv("SERVER_ADDRESS") ?: "127.0.0.1"
//        val username: String = System.getenv("DB_USERNAME") ?: "superuser"
//        val password: String = System.getenv("DB_PASSWORD") ?: "changeToAStrongPassword"
//        val server: String = System.getenv("SERVER_ADDRESS") ?: "localhost:27017"
        val databaseName: String = System.getenv("DATABASE") ?: "test"
        this.connectionString = ConnectionString("mongodb://$username:$password@$server")
        this.mongoClient = KMongo.createClient(connectionString)
        this.db = this.mongoClient.getDatabase(databaseName)
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
