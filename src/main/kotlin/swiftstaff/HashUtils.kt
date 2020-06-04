package swiftstaff

import at.favre.lib.crypto.bcrypt.BCrypt
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

// Documentation https://github.com/patrickfav/bcrypt

fun generateSalt(): String {
    return String(SecureRandom().generateSeed(16), StandardCharsets.UTF_8)
}

fun hashPassword(salt: String, password: String): String {
    val bcryptHash: ByteArray =
        BCrypt.withDefaults().hash(6, salt.toByteArray(StandardCharsets.UTF_8), password.toByteArray())
    return String(bcryptHash, StandardCharsets.UTF_8)
}