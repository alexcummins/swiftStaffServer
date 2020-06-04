package swiftstaff

interface Collection {
    val _id: String?
}

data class Worker(
    override val _id: String? = null,
    val fName: String = "John",
    val lName: String = "Doe",
    val phone: Long = 79999999,
    val dob: String = "01/01/2020",
    val personalStatement: String = " ",
    val qualificationIds: MutableList<String> = mutableListOf(),
    val expertiseIds: MutableList<Int> = mutableListOf(),
    val rating: Double = 5.0,
    val imageIds: MutableList<String> = mutableListOf()
) : Collection


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
    val imageIds: MutableList<String> = mutableListOf(),
    val facebookLink: String = "",
    val twitterLink: String = "",
    val instagramLink: String = ""
) : Collection

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
    val workerId: String = " ",
    val sendStrategyId: String = "1",
    val hourlyRate: String = "1075",
    val status: Int = 0,
    val expertiseId: Int = 0,
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

data class Jobs(val count: Int, val jobsList: MutableList<Job>)
