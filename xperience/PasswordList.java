/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import java.io.*;
import java.util.*;

/**
 * Class responsible for managing the list of one-time passwords.
 * This class loads passwords from a file and provides methods to validate and
 * consume passwords as part of the "SeaCure" protocol.
 */
public class PasswordList {
    private final Set<String> passwords = new HashSet<>();
    
    /**
     * Constructor that loads passwords from the specified file.
     *
     * @param passwordFile Path to the file containing passwords
     * @throws IOException If the file cannot be read
     */
    public PasswordList(String passwordFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && !line.contains("#")) {
                    passwords.add(line);
                }
            }
        }
    }
    
    /**
     * Checks if the provided password is valid and removes it if it is.
     * This implements the one-time password mechanism.
     *
     * @param password The password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean validateAndConsume(String password) {
        if (password == null || password.isEmpty() || password.contains("#")) {
            return false;
        }
        
        if (passwords.contains(password)) {
            passwords.remove(password);
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns the number of available passwords.
     *
     * @return The number of passwords in the list
     */
    public int size() {
        return passwords.size();
    }
}
