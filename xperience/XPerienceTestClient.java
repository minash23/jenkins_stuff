/************************************************
 *
 * Author: Mina Shehata
 * Assignment: XPerience Server DB Improvements (Program 3)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * XPerienceTestClient is used to send test cases to the XPerience server.
 */
public class XPerienceTestClient {
    private static final Logger logger = Logger.getLogger(XPerienceTestClient.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java XPerienceTestClient <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String host = "localhost";

        String[] testCases = {
            // Valid cases
            "A#2025-03-04#12:00#Valid min name and description#", // Minimum valid name and description
            "EventMax" + "a".repeat(292) + "#2025-04-10#23:59#" + "b".repeat(65530) + "#", // Max name and description
            "Dance Night#2025-06-15#18:30#Evening of joy#", // Regular valid event
            
            // Invalid name cases
            "#2025-07-01#20:00#Missing name#", // Name missing
            "TooLong" + "a".repeat(301) + "#2025-05-20#14:00#Valid description#", // Name exceeding 300 chars
            
            // Invalid date cases
            "Festival#2025-02-30#15:00#Invalid date#", // Invalid date
            "Party#25-12-2025#20:00#Wrong date format#", // Incorrect date format
            
            // Invalid time cases
            "Midnight Show#2025-12-31#24:01#Invalid time format#", // Invalid time
            "Morning Event#2025-08-01#8:60#Invalid minutes#", // Invalid minutes
            
            // Invalid description cases
            "Concert#2025-10-20#21:00#", // Missing description
            "Meeting#2025-11-11#10:30# " + "c".repeat(65536) + "#", // Description exceeding 65,535 chars
            
            // Duplicate event case
            "Unique Event#2025-09-09#19:00#This should be accepted first#", // First occurrence (should be accepted)
            "Unique Event#2025-09-09#19:00#This should be rejected as duplicate#", // Duplicate (should be rejected)
        };

        for (int i = 0; i < testCases.length; i++) {
            sendTestCase(host, port, testCases[i], i + 1);
        }
    }

    private static void sendTestCase(String host, int port, String message, int caseNumber) {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            
            out.write(message.getBytes());
            out.flush();
            
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            String response = new String(buffer, 0, bytesRead);
            
            System.out.println("Test Case " + caseNumber + ":");
            System.out.println("Sending: " + message);
            System.out.println("Received: " + response + "\n");
        } catch (IOException e) {
            logger.severe("Error in test case " + caseNumber + ": " + e.getMessage());
        }
    }
}

