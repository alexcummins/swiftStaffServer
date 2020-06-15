package swiftstaff.api.v1

import swiftstaff.Job
import swiftstaff.Restaurant
import swiftstaff.Worker

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
        val fname: String,
        val lname: String,
        val credentials: MutableList<String>,
        val phone: Long,
        val dob: String,
        val fcmToken: String
) : Credentials

data class SignupRestaurant(
        override val email: String,
        override val password: String,
        val name: String,
        val address: String,
        val phone: Long,
        val restaurantEmailAddress: String,
        val fcmToken: String
) : Credentials

data class LoginAttempt(
        override val email: String,
        override val password: String,
        val fcmToken: String
) : Credentials

data class UpdatedRestaurant(
        val restaurantId: String,
        val email: String,
        val name: String,
        val address: String,
        val phone: Long,
        val facebookLink: String,
        val twitterLink: String,
        val instagramLink: String,
        val description: String
)

data class LatLong(
        val latitude: Double,
        val longitude: Double
)

data class LoginWorkerResponse(
        val userId: String,
        val workerId: String,
        val userType: Int,
        val email: String,
        val fname: String,
        val lname: String,
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
        val fname: String,
        val lname: String,
        val signUpFinished: Boolean,
        val restaurantId: String
)

data class NewJobRequest(
        val restaurantId: String,
        val hourlyRate: String = "1075",
        val credentials: MutableList<String>,
        val date: String = "01/01/2000",
        val startTime: String = "07:00",
        val endTime: String = "15:00",
        val extraInfo: String = ""
)

data class JobResponse(
        val job: Job,
        val restaurant: Restaurant
)

data class JobResponseForRestaurant(
        val job: Job,
        val workers: MutableList<Worker>
)

data class UserIdentity(
        val userId: String,
        val userType: Int
)

data class WorkerProfile(
        val userId: String,
        val fname: String,
        val lname: String,
        val profileImageId: String,
        val phone: Long,
        val address: String,
        val skillsAndQualities: MutableList<String>,
        val qualifications: MutableList<String>,
        val experience: MutableList<String>,
        val personalStatement: String,
        val ratingTotal: Double,
        val ratingCount: Int
)

data class WorkerProfileEditRequest(
        val workerId: String,
        val firstName: String,
        val lastName: String,
        val address: String,
        val phoneNumber: String,
        val skillsAndQualities: MutableList<String>,
        val qualifications: MutableList<String>,
        val experience: MutableList<String>,
        val personalStatement: String
)

data class UploadInfo(
        val userId: String,
        val resourceName: String
)

data class WorkerId(
        val workerId: String
)

data class RestaurantId(
        val restaurantId: String
)

data class JobId(
        val _id: String
)

data class RestaurantProfile(
        val restaurantId: String,
        val name: String,
        val address: String,
        val phone: Long,
        val email: String,
        val longitude: Double,
        val latitude: Double,
        val facebookLink: String,
        val twitterLink: String,
        val instagramLink: String
)


data class NewWorkerRating(
        val userId: String,
        val newRating: Double
)
data class WorkerPatch(
        val jobId: String,
        val workerId: String,
        val commandId: Int
)
