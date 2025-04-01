/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import java.util.*;
import donabase.*;

/**
 * Database implementation of EventStore.
 */
public class EventStoreDB implements EventStore {
    private final DonaBaseConnection dbConnection;
    private static int eventCount = 0;

    public EventStoreDB(DonaBaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        
        // Initialize event count from database
        try {
            String countQuery = "SELECT COUNT(*) FROM Event";
            List<List<String>> result = dbConnection.query(countQuery);
            if (result != null && !result.isEmpty()) {
                eventCount = Integer.parseInt(result.get(0).get(0));
            }
        } catch (Exception e) {
            // Log error or handle appropriately
        }
    }

    @Override
    public Result addEvent(String name, String date, String time, String description) {
        // Validate the event
        if (!EventStore.EventValidator.validate(name, date, time, description)) {
            return new Result(false, eventCount);
        }

        try {
            // Escape single quotes in input values to prevent SQL injection
            String escapedName = escapeSQLString(name);
            String escapedDate = escapeSQLString(date);
            String escapedTime = escapeSQLString(time);
            String escapedDescription = escapeSQLString(description);
            
            // Check if the event name already exists in the database
            String checkQuery = String.format(
                "SELECT COUNT(*) FROM Event WHERE name = '%s'",
                escapedName
            );
            List<List<String>> result = dbConnection.query(checkQuery);
            
            if (result != null && !result.isEmpty() && Integer.parseInt(result.get(0).get(0)) > 0) {
                return new Result(false, eventCount);
            }
            
            // Insert the event into the database
            String insertQuery = String.format(
                "INSERT INTO Event (name, event_date, event_time, description) VALUES ('%s', '%s', '%s', '%s')",
                escapedName, escapedDate, escapedTime, escapedDescription
            );
            
            boolean rowsAffected = dbConnection.insert(insertQuery);
            
            if (rowsAffected) {
                eventCount++; // Increment event count on successful insert
                return new Result(true, eventCount);
            } else {
                return new Result(false, eventCount);
            }
        } catch (Exception e) {
            return new Result(false, eventCount);
        }
    }

    /**
     * Escapes single quotes in a string for safe use in SQL queries.
     * 
     * @param input The string to be escaped.
     * @return The escaped string.
     */
    private String escapeSQLString(String input) {
        return input.replace("'", "''");
    }
}
