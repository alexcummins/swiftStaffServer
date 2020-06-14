package swiftstaff.api.v1

import org.litote.kmongo.eq
import swiftstaff.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun updateFileId(fileIndex: Int, fileId: String, userId: String, entity: ImageCollector) {

    // Configured to only use Profile as imageId[0]
    if (entity.imageIds.size == 0) {
        entity.imageIds.add(fileId)
    } else {
        entity.imageIds[fileIndex] = fileId
    }

    logMessage("imageID: " + entity.imageIds[fileIndex])

    if (entity is Worker) {
        MongoDatabase.update(entity, entity::_id eq userId)
    } else if (entity is Restaurant) {
        MongoDatabase.update(entity, entity::_id eq userId)
    }
    logMessage("Updated entitity")
}