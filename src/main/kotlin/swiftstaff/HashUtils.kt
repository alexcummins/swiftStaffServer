package swiftstaff

import at.favre.lib.crypto.bcrypt.BCrypt
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import kotlin.random.Random
import java.security.MessageDigest;

// Documentation https://github.com/patrickfav/bcrypt

fun generateSalt(): String {
    return String(Random.nextBytes(16), StandardCharsets.ISO_8859_1)
}

fun hashPassword(salt: String, password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(salt.toByteArray( StandardCharsets.ISO_8859_1))
    digest.update(password.toByteArray(StandardCharsets.ISO_8859_1))
    return String(digest.digest(), StandardCharsets.ISO_8859_1)
}
