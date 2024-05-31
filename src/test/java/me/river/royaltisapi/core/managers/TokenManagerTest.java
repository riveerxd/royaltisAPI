package me.river.royaltisapi.core.managers;

import me.river.royaltisapi.core.game.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TokenManager class.
 * Verifies the correct generation and decryption of user tokens, as well as error handling for invalid tokens.
 */
class TokenManagerTest {

    /**
     * Tests the entire lifecycle of token generation and decryption.
     * Creates a sample user, generates a token, and then decrypts it,
     * verifying that the original user's data is correctly recovered.
     */
    @Test
    void testTokenGenerationAndDecryption() {
        // Create a sample user
        User testUser = new User("testuser", "password123");

        // Generate a token
        String token = TokenManager.getUserToken(testUser);
        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");

        // Decrypt the token
        User decryptedUser = TokenManager.getUserFromToken(token);

        // Verify that the decrypted user matches the original
        assertNotNull(decryptedUser, "Decrypted user should not be null");
        assertEquals(testUser.getUsername(), decryptedUser.getUsername(), "Username should match");
        assertEquals(testUser.getPassword(), decryptedUser.getPassword(), "Password should match");
    }

    /**
     * Tests the behavior when attempting to decrypt an invalid token.
     * Verifies that a RuntimeException is thrown in this scenario.
     */
    @Test
    void testInvalidTokenDecryption() {
        // Provide an invalid token
        String invalidToken = "this_is_an_invalid_token";

        // Expect an exception to be thrown due to invalid decryption
        assertThrows(RuntimeException.class, () -> TokenManager.getUserFromToken(invalidToken),
                "Decrypting an invalid token should throw RuntimeException");
    }
}
