package swiftstaff
import com.mongodb.BasicDBObject
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import org.bson.Document

data class Jedi(val name: String, val age: Int)


class HelloWorld {


     fun test() : String  {

         val mongoClient = MongoClients.create(ConnectionString("mongodb://167.99.193.160"))
         val database = mongoClient.getDatabase("test")
         val tbl = database.getCollection("bookings");
         val document = Document()
         document.append("restaurant","Eastside")
         document.append("Date","28052020")
         tbl.insertOne(document)

         tbl.findOneAndReplace(Document("Date", "holmes"), Document("Date", "29052020"))
         val s =  tbl.find(eq("restaurant", "Eastside")).forEach {
             print(it.toString()) }.toString()
        return s
    }
}
