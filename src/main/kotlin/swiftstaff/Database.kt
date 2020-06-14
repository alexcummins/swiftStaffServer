package swiftstaff

import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import kotlin.reflect.KClass

interface Database {
    fun < T : Any> find(filter: Bson? = null, classParam: KClass<T>, collection: MongoCollection<T>) : MutableList<T>

    fun <T : Any> deleteById(_id: String, classParam: KClass<T>, collection: MongoCollection<T>): Boolean

    fun < T : Any> insert(data: T, classParam: KClass<T>, collection: MongoCollection<T>) : Boolean

    fun <T : Any> update(data: T, filter: Bson,  classParam: KClass<T>, collection: MongoCollection<T>): Boolean
}
