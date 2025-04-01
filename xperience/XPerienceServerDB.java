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
import donabase.*;

/**
 * XPerienceServerDB is the main server class that listens for client connections
 * and processes events by interacting with the database.
 * Updated to include password security.
 */
public class XPerienceServerDB {
    
    /**
     * Logger for logging messages and errors
     */
    private static final Logger logger = Logger.getLogger(XPerienceServerDB.class.getName());
    
    /**
     * Executor service for handling client requests concurrently with virtual threads
     */
    private final ExecutorService executor;
    
    /**
     * Event store for managing events in the database
     */
    private final EventStore eventStore;
    
    /**
     * Password list for validating one-time passwords
     */
    private final PasswordList passwordList;

    /**
     * Constructor that initializes the server, establishes a connection to the database,
     * loads the password list, and starts listening for client connections.
     * 
     * @param port The port number for the server to listen on.
     * @param dbServer The database server information (format: hostname)
     * @param passwordFile Path to the file containing passwords
     * @throws IOException if there is an issue with the server socket, database connection, or password file.
     */
    public XPerienceServerDB(int port, String dbServer, String passwordFile) throws IOException {
        // Initialize virtual threads executor
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // Initialize database connection
        DonaBaseConnection dbConnection;
        try {
            dbConnection = new DonaBaseConnection(
                dbServer,     // Database host from command line
                3306,         // Database port
                "shehata",    // Database name (changed to last name as specified)
                "mina",       // Username
                "password123"  // Password
            );
        } catch (Exception e) {
            logger.severe("Database connection failed: " + e.getMessage());
            throw new IOException("Failed to connect to database", e);
        }
        
        // Create database event store
        this.eventStore = new EventStoreDB(dbConnection);
        
        // Initialize password list
        this.passwordList = new PasswordList(passwordFile);
        logger.info("Loaded " + passwordList.size() + " passwords");
        
        configureLogging();

        // Initialize server socket to listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("XPerienceServerDB started on port " + port);
            
            while (true) {
                // Accept client connection and handle it in a new thread
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

        // Extract fields
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
            "Accept#" + result.eventCount + "#" : 
            "Reject#";
    }

    /**
     * Main method to start the XPerienceServerDB.
     * 
     * @param args Command-line arguments, where the first argument is the port number,
     *             the second is the database server, and the third is the path to the password file.
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java XPerienceServerDB <port> <db server> <password file>");
            System.exit(1);
        }
        
        try {
            int port = Integer.parseInt(args[0]);
            if (port < 0 || port > 65535) {
                throw new NumberFormatException("Invalid port number");
            }
            
            String dbServer = args[1];
            String passwordFile = args[2];
            
            new XPerienceServerDB(port, dbServer, passwordFile);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            System.exit(1);
        }
    }
}
