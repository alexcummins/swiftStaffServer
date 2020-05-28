package swiftstaff
import com.mongodb.BasicDBObject
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import kotlin.properties.Delegates

data class Jedi(val name: String, val age: Int)


object DatabaseAdaptor {
    val mongoClient : MongoClient
    val connectionString:  ConnectionString
    init {
        this.connectionString = ConnectionString("mongodb://myUserAdmin:SwiftStaff@10.106.0.2")
        this.mongoClient = MongoClients.create(connectionString)
    }
}
