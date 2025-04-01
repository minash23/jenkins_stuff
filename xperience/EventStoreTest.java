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
import java.net.*;

/**
 * JUnit 5 test for the XPerienceServer communication protocol.
 * Tests the server's responses to various event registration requests.
 */
public class EventStoreTest {
    
    private static final int PORT = 8085;
    private static final String PASSWORD_FILE = "test_passwords.txt";
    private static XPerienceServer server;
    private static Thread serverThread;
    
    @BeforeAll
    public static void setupServer() throws IOException {
        // Create test password file
        try (PrintWriter writer = new PrintWriter(PASSWORD_FILE)) {
            writer.println("password1");
            writer.println("password2");
            writer.println("password3");
            writer.println("password4");
            writer.println("password5");
            writer.println("password6");
            writer.println("password7");
        }
        
        // Start server in a separate thread
        serverThread = new Thread(() -> {
            try {
                server = new XPerienceServer(PORT, PASSWORD_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Give server time to start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testAddValidEvent() throws IOException {
        String response = sendEventToServer(
            "Conference", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference",
            "password1"
        );
        
        assertEquals("Accept#1#", response);
    }
    
    @Test
    public void testAddMultipleEvents() throws IOException {
        // Add second event (first was added in previous test)
        String response = sendEventToServer(
            "Workshop", 
            "2025-04-16", 
            "10:00", 
            "Coding workshop",
            "password2"
        );
        
        assertEquals("Accept#2#", response);
    }
    
    @Test
    public void testAddDuplicateEvent() throws IOException {
        // Try to add duplicate event (same name)
        String response = sendEventToServer(
            "Conference", 
            "2025-05-20", 
            "09:00", 
            "Different description",
            "password3"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testInvalidName() throws IOException {
        // Empty name
        String response = sendEventToServer(
            "", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference",
            "password4"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testInvalidDate() throws IOException {
        // Invalid date format
        String response = sendEventToServer(
            "Conference2", 
            "15/04/2025", // Not ISO format
            "14:30", 
            "Annual tech conference",
            "password5"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testInvalidTime() throws IOException {
        // Invalid time format
        String response = sendEventToServer(
            "Conference3", 
            "2025-04-15", 
            "2:30 PM", // Not 24-hour format
            "Annual tech conference",
            "password6"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testEmptyDescription() throws IOException {
        // Empty description
        String response = sendEventToServer(
            "Conference4", 
            "2025-04-15", 
            "14:30", 
            "",
            "password7"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testInvalidPassword() throws IOException {
        // Invalid password
        String response = sendEventToServer(
            "Conference5", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference",
            "invalid-password"
        );
        
        assertEquals("Reject#", response);
    }
    
    @Test
    public void testMissingFields() throws IOException {
        // Connect to server
        try (Socket socket = new Socket("localhost", PORT)) {
            // Send incomplete request
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print("IncompleteEvent#2025-04-15#14:30#");
            out.flush();
            
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            
            assertEquals("Reject#", response);
        }
    }
    
    /**
     * Helper method to send an event to the server and get the response
     */
    private String sendEventToServer(String name, String date, String time, String description, String password) 
            throws IOException {
        // Connect to server
        try (Socket socket = new Socket("localhost", PORT)) {
            // Send event
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String request = name + "#" + date + "#" + time + "#" + description + "#" + password;
            out.print(request);
            out.flush();
            
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return in.readLine();
        }
    }
    
    @AfterAll
    public static void cleanup() {
        // Delete test password file
        File passwordFile = new File(PASSWORD_FILE);
        if (passwordFile.exists()) {
            passwordFile.delete();
        }
    }
}
