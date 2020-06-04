package swiftstaff.api.v1

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
