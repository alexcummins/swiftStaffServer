package swiftstaff

data class Worker(val _id: String? = null,
                  val fName: String = "John",
                  val lName: String = "Doe",
                  val phone: Long = 79999999,
                  val dob: Int = 1591206710,
                  val qualificationIds: MutableList<String> = mutableListOf(),
                  val expertiseIds: MutableList<Int> = mutableListOf(),
                  val rating: Double = 5.0,
                  val imageIds: MutableList<String> = mutableListOf()
)


data class Restaurant(
        val _id: String? = null,
        val name: String = "",
        val address: String = "",
        val phone: Long = 79999999,
        val staffUserIds: MutableList<String> = mutableListOf(),
        val restaurantEmailAddress: String = "",
        val rating: Double = 5.0,
        val longitude: Double = 0.0,
        val latitude: Double = 0.0,
        val imageIds: MutableList<String> = mutableListOf(),
        val facebookLink: String = "",
        val twitterLink: String = "",
        val instagramLink: String = ""
)

data class User(
        val _id: String? = null,
        val email: String = "",
        val password: String = "",
        val userType: Int = 1,
        val foreignTableId: String = "",
        val fcmTokens: MutableList<String> = mutableListOf(),
        val signUpFinished: Boolean = false
)


data class Job(
        val _id: String? = null,
        val restaurantId: String = "",
        val workerId: String = "",
        val sendStrategyId: String = "1",
        val hourlyRate: Int = 1075,
        val status: Int = 0,
        val expertiseId: Int = 0,
        val date: Int = 79999999,
        val startTime: Int = 79999999,
        val endTime: Int = 79999999,
        val extraInfo: String = ""
)

data class WorkerRestaurantRelation(
        val _id: String? = null,
        val restaurantId: String = "",
        val workerId: String = "",
        val relationType: Int = 0
        )

data class Jobs(val count: Int, val jobsList: MutableList<Job>)
