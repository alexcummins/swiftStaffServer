package swiftstaff

import swiftstaff.api.v1.JobResponse
import swiftstaff.api.v1.JobResponseForRestaurant

interface Collection {
    val _id: String?
}

interface ImageCollector {
    val _id: String?
    val imageIds: MutableList<String>
}

data class Worker(
        override val _id: String? = null,
        val fname: String = "",
        val lname: String = "",
        val phone: Long = 79999999,
        val dob: String = "01/01/2020",
        val personalStatement: String = " ",
        val qualificationIds: MutableList<String> = mutableListOf(),
        val credentials: MutableList<String> = mutableListOf(),
        val expertiseIds: MutableList<Int> = mutableListOf(),
        var ratingTotal: Double = 0.0,
        var ratingCount: Int = 0,
        override val imageIds: MutableList<String> = mutableListOf("0")
) : Collection, ImageCollector


data class Restaurant(
    override val _id: String? = null,
    val name: String = " ",
    val address: String = " ",
    val phone: Long = 79999999,
    val staffUserIds: MutableList<String> = mutableListOf(),
    val restaurantEmailAddress: String = " ",
    val rating: Double = 5.0,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    override val imageIds: MutableList<String> = mutableListOf("0"),
    val facebookLink: String = "",
    val twitterLink: String = "",
    val instagramLink: String = ""
) : Collection, ImageCollector

data class User(
    override val _id: String? = null,
    val email: String = " ",
    val passwordHash: String = " ",
    val salt: String = " ",
    val userType: Int = 1,
    val foreignTableId: String = " ",
    var fcmTokens: MutableList<String> = mutableListOf(),
    val signUpFinished: Boolean = false
): Collection


data class Job(
        override val _id: String? = null,
        val restaurantId: String = " ",
        var workerId: String = " ",
        val hourlyRate: String = "1075",
        var status: Int = 0,
        val credentials: MutableList<String> = mutableListOf(),
        var sentList: MutableList<String> = mutableListOf(),
        var reviewList: MutableList<String> = mutableListOf(),
        val date: String = "01/01/2000",
        val startTime: String = "07:00",
        val endTime: String = "15:00",
        val extraInfo: String = ""
) : Collection

data class WorkerRestaurantRelation(
    override val _id: String? = null,
    val restaurantId: String = "",
    val workerId: String = "",
    val relationType: Int = 0
) : Collection

data class Jobs(val count: Int, val jobsList: MutableList<JobResponse>)
data class JobsForRestaurant(val count: Int, val jobsList: MutableList<JobResponseForRestaurant>)
