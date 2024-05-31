package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.game.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Manages the creation and decryption of user tokens for authentication and authorization.
 */
public class TokenManager {
    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private static final String ALGORITHM = "AES";
    private static final String KEY = "Jd7w92uz1g5Djd82";

    /**
     * Generates a token for the given user by encrypting their username and password.
     *
     * @param user The user for whom to generate the token.
     * @return The generated token.
     */
    public static String getUserToken(User user) {
        String token = encrypt(user.getUsername() + ";" + user.getPassword());
        logger.info("Generated token for user {}", user.getUsername());
        return token;
    }

    /**
     * Retrieves a User object from a given token by decrypting it.
     *
     * @param token The token to decrypt.
     * @return The User object extracted from the token.
     */
    public static User getUserFromToken(String token) {
        logger.info("Attempting to decrypt token...");
        String[] decrypted = decrypt(token).split(";");
        logger.info("Token decrypted successfully.");
        return new User(decrypted[0], decrypted[1]);
    }

    /**
     * Encrypts a plain text string using AES encryption.
     *
     * @param plainText The plain text to encrypt.
     * @return The encrypted text.
     */
    public static String encrypt(String plainText) {
        logger.debug("Encrypting string.");
        try {
            byte[] plainTextBytes = plainText.getBytes();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("Error encrypting string: {}", e.getMessage());
            throw new RuntimeException("Error encrypting string: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts an encrypted text string using AES encryption.
     *
     * @param encryptedText The encrypted text to decrypt.
     * @return The decrypted text.
     */
    private static String decrypt(String encryptedText) {
        logger.debug("Decrypting string.");
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            logger.error("Error decrypting string: {}", e.getMessage());
            throw new RuntimeException("Error decrypting string: " + e.getMessage(), e);
        }
    }
}
