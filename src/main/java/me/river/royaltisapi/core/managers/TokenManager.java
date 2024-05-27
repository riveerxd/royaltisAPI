package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.game.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * The Token manager.
 */
public class TokenManager {
    /**
     * The Algorithm.
     */
    private static final String ALGORITHM = "AES";
    /**
     * The Key.
     */
    private static final String KEY = "Jd7w92uz1g5Djd82";

    /**
     * Gets user token.
     *
     * @param user the user
     * @return the user token
     */
    public static String getUserToken(User user){
        return encrypt(user.getUsername()+";"+user.getPassword());
    }

    /**
     * Gets user from token.
     *
     * @param token the token
     * @return the user from token
     */
    public static User getUserFromToken(String token){
        String[] decrypted = decrypt(token).split(";");
        return new User(decrypted[0], decrypted[1]);
    }

    public static String encrypt(String plainText) {
        try {
            byte[] plainTextBytes = plainText.getBytes();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting string: " + e.getMessage(), e);
        }
    }



    private static String decrypt(String encryptedText) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting string: " + e.getMessage(), e);
        }
    }


}
