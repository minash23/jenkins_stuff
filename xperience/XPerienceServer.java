/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * The main server class for the XPerience protocol.
 * This class listens for client connections, processes event registration
 * requests, and sends appropriate responses back to the clients.
 * Updated to include password security.
 */
public class XPerienceServer {

    /**
     * Logger for the server, used to log information, warnings, and errors.
     */
    private static final Logger logger = Logger.getLogger(XPerienceServer.class.getName());

    /**
     * Executor service for handling client requests concurrently with virtual threads.
     */
    private final ExecutorService executor;

    /**
     * Event store for managing events.
     */
    private final EventStore eventStore;
    
    /**
     * Password list for validating one-time passwords.
     */
    private final PasswordList passwordList;

    /**
     * Constructs the XPerience server with an in-memory event store and password security.
     * 
     * @param port The port to listen on
     * @param passwordFile Path to the file containing passwords
     * @throws IOException If server socket creation or password file reading fails
     */
    public XPerienceServer(int port, String passwordFile) throws IOException {
        this(port, new EventStoreMemory(), passwordFile);
    }

    /**
     * Constructs the XPerience server with a specific event store and password security.
     * 
     * @param port The port to listen on
     * @param eventStore The event store implementation to use
     * @param passwordFile Path to the file containing passwords
     * @throws IOException If server socket creation or password file reading fails
     */
    public XPerienceServer(int port, EventStore eventStore, String passwordFile) throws IOException {
        // Use virtual threads executor
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.eventStore = eventStore;
        
        // Initialize password list
        this.passwordList = new PasswordList(passwordFile);
        logger.info("Loaded " + passwordList.size() + " passwords");

        configureLogging();

        // Start server
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClient(clientSocket));
            }
        }
    }

    /**
     * Configures the logging settings for the server.
     */
    private void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        
        // Remove default handlers
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        
        // Add console handler
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.INFO);
        rootLogger.addHandler(handler);
    }

    /**
     * Handles communication with a connected client.
     *
     * @param clientSocket The socket representing the connection to the client.
     */
    private void handleClient(Socket clientSocket) {
        try {
            // Read raw bytes to handle exact string matching
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            
            // Buffer for reading
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            
            if (bytesRead > 0) {
                // Convert to string preserving exact characters
                String inputLine = new String(buffer, 0, bytesRead);
                logger.info("Received from client: " + inputLine);
                
                // Process and send response
                String response = processEvent(inputLine);
                out.write(response.getBytes());
                out.flush();
                
                logger.info("Sent to client: " + response);
            }
        } catch (IOException e) {
            logger.severe("Error with client communication: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.warning("Error closing client socket: " + e.getMessage());
            }
        }
    }

    /**
     * Processes an event registration request with password security.
     *
     * @param input The raw input string from the client.
     * @return A response string indicating whether the event was accepted or rejected.
     */
    private String processEvent(String input) {
        // Remove trailing newlines if any
        input = input.replaceAll("[\r\n]+$", "");
        
        // Split on # and get all parts
        String[] parts = input.split("#", -1);
        
        // Must have at least 5 parts (name, date, time, description, password)
        if (parts.length < 5) {
            return "Reject#";
        }

        // Extract the fields
        String name = parts[0];
        String date = parts[1];
        String time = parts[2];
        String description = parts[3];
        String password = parts[4];
        
        // Validate password first
        if (!passwordList.validateAndConsume(password)) {
            logger.info("Password validation failed");
            return "Reject#";
        }
        
        logger.info("Password validation successful");

        // Try to add event
        EventStore.Result result = eventStore.addEvent(name, date, time, description);

        // Return response based on result
        return result.success ? 
            "Aksept#" + result.eventCount + "#" : 
            "Reject#";
    }

    /**
     * Main method to start the XPerienceServer.
     * 
     * @param args Command-line arguments, where the first argument is the port number
     *             and the second is the path to the password file.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java XPerienceServer <port> <password file>");
            System.exit(1);
        }
        
        try {
            int port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65535) {
                throw new NumberFormatException("Invalid port number");
            }
            
            String passwordFile = args[1];
            new XPerienceServer(port, passwordFile);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            System.exit(1);
        }
    }
}
