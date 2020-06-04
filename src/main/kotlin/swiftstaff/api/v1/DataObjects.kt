package swiftstaff.api.v1

enum class UserType {
    Worker,
    Restaurant
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
    val dob: Int
) : Credentials

data class SignupRestaurant(
    override val email: String,
    override val password: String,
    val name: String,
    val address: String,
    val phone: Long,
    val restaurantEmailAddress: String
) : Credentials