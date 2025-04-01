/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


/**
 * JUnit 5 test for the PasswordList class.
 */
public class PasswordListTest {
    
    private static final String TEST_PASSWORDS_FILE = "test_passwords.txt";
    
    @BeforeEach
    public void setup() throws IOException {
        // Create a temporary password file for testing
        String[] passwords = {"password1", "securepass", "mypassword"};
        Files.write(Paths.get(TEST_PASSWORDS_FILE), String.join("\n", passwords).getBytes());
    }
    
    @AfterEach
    public void cleanup() throws IOException {
        // Delete the temporary password file
        Files.deleteIfExists(Paths.get(TEST_PASSWORDS_FILE));
    }
    
    @Test
    public void testPasswordListCreation() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        assertEquals(3, passwordList.size(), () -> "Password list should contain 3 passwords");
    }
    
    @Test
    public void testValidateAndConsumeValid() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertTrue(passwordList.validateAndConsume("password1"), () -> "Valid password should be accepted");
        assertEquals(2, passwordList.size(), () -> "Password list should have one less password after consumption");
        
        assertTrue(passwordList.validateAndConsume("securepass"), () -> "Valid password should be accepted");
        assertEquals(1, passwordList.size(), () -> "Password list should have one less password after consumption");
    }
    
    @Test
    public void testValidateAndConsumeInvalid() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertFalse(passwordList.validateAndConsume("wrongpassword"), () -> "Invalid password should be rejected");
        assertEquals(3, passwordList.size(), () -> "Password list size should remain unchanged");
    }
    
    @Test
    public void testValidateAndConsumeReuse() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertTrue(passwordList.validateAndConsume("password1"), () -> "Valid password should be accepted");
        assertFalse(passwordList.validateAndConsume("password1"), () -> "Used password should be rejected");
    }
    
    @Test
    public void testPasswordWithHash() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertFalse(passwordList.validateAndConsume("invalid#password"), () -> "Password with # should be rejected");
        assertEquals(3, passwordList.size(), () -> "Password list size should remain unchanged");
    }
    
    @Test
    public void testEmptyPassword() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertFalse(passwordList.validateAndConsume(""), () -> "Empty password should be rejected");
        assertEquals(3, passwordList.size(), () -> "Password list size should remain unchanged");
    }
    
    @Test
    public void testNullPassword() throws IOException {
        PasswordList passwordList = new PasswordList(TEST_PASSWORDS_FILE);
        
        assertFalse(passwordList.validateAndConsume(null), () -> "Null password should be rejected");
        assertEquals(3, passwordList.size(), () -> "Password list size should remain unchanged");
    }
}

