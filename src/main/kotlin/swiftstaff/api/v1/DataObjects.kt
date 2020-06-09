package swiftstaff.api.v1

import swiftstaff.Job
import swiftstaff.Restaurant

enum class UserType (val num: Int) {
    Restaurant(1),
    Worker(2)
}

interface Credentials {
    val email: String
    val password: String
}

data class SignupWorker(
    override val email: String,
    override val password: String,
    val fName: String,
    val lName: String,
    val phone: Long,
    val dob: String
) : Credentials

data class SignupRestaurant(
    override val email: String,
    override val password: String,
    val name: String,
    val address: String,
    val phone: Long,
    val restaurantEmailAddress: String
) : Credentials

data class LoginAttempt(
    override val email: String,
    override val password: String,
    val fcmToken: String
) : Credentials


data class LoginWorkerResponse(
    val userId: String,
    val userType: Int,
    val email: String,
    val fName: String,
    val lName: String,
    val phone: Long,
    val signUpFinished: Boolean
)


data class LoginRestaurantResponse(
    val userId: String,
    val userType: Int,
    val email: String,
    val restaurantEmail: String,
    val restaurantName: String,
    val restaurantPhone: Long,
    val fName: String,
    val lName: String,
    val signUpFinished: Boolean,
    val restaurantId: String
)

data class NewJobRequest(
        val restaurantId: String,
        val sendStrategyId: Int = 1,
        val hourlyRate: String = "1075",
        val expertiseId: Int = 1,
        val date: String = "01/01/2000",
        val startTime: String = "07:00",
        val endTime: String = "15:00",
        val extraInfo: String = ""
)

data class JobResponse(
        val job: Job,
        val restaurant: Restaurant
)

data class UserIdentity(
        val userId: String,
        val userType: Int
)

data class WorkerProfile(
        val fName: String,
        val lName: String,
        val phone: Long,
        val profileImage: String,
        val address: String,
        val skillsAndQualities: MutableList<String>,
        val qualifications: MutableList<String>,
        val experience: MutableList<String>,
        val personalStatement: String,
        val rating: Double
)