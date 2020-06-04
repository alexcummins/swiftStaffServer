package swiftstaff

import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

// TODO: Refactor so tests pass in randomness as a seed

class HashUtilsTest {

    @Test
    fun saltValueReturnedIs16BytesLong() {
        assert(generateSalt().toByteArray(StandardCharsets.ISO_8859_1).size == 16)
    }

    @Test
    fun passwordHashedWithSameSaltGivesSameHash(){
        val salt = generateSalt()
        val pass1 = hashPassword(salt, "MyPassword")
        val pass2 = hashPassword(salt, "MyPassword")
        assert(pass1 == pass2)
    }

}