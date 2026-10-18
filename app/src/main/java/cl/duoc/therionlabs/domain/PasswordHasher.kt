package cl.duoc.therionlabs.domain

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    fun salt(): String =
        Base64.getEncoder().encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 120000, 256)
        return try {
            Base64.getEncoder()
                .encodeToString(
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(spec)
                        .encoded
                )
        } finally {
            spec.clearPassword()
        }
    }

    fun matches(password: String, salt: String, expected: String) =
        MessageDigest.isEqual(
            Base64.getDecoder().decode(hash(password, salt)),
            Base64.getDecoder().decode(expected),
        )
}
