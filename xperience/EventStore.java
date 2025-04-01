/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;

import java.util.regex.*;
import java.time.*;
import java.time.format.*;

/**
 * Interface for storing and managing events.
 */
public interface EventStore {
    /**
     * Attempts to add an event to the store.
     * 
     * @param name Event name
     * @param date Event date
     * @param time Event time
     * @param description Event description
     * @return Pair of boolean (success) and event count if successful
     */
    Result addEvent(String name, String date, String time, String description);

    /**
     * Represents the result of an event addition attempt.
     */
    class Result {
        public final boolean success;
        public final int eventCount;

        public Result(boolean success, int eventCount) {
            this.success = success;
            this.eventCount = eventCount;
        }
    }

    /**
     * Utility class for event validation.
     */
    class EventValidator {
        private static final int MAX_NAME_LENGTH = 300;
        private static final int MAX_DESCRIPTION_LENGTH = 65535;

        /**
         * Validates an event's attributes.
         * 
         * @param name Event name
         * @param date Event date
         * @param time Event time
         * @param description Event description
         * @return True if all validations pass
         */
        public static boolean validate(String name, String date, String time, String description) {
            return validateName(name) &&
                   validateDate(date) &&
                   validateTime(time) &&
                   validateDescription(description);
        }

        /**
         * Validates event name.
         * 
         * @param name Event name to validate
         * @return True if name is valid
         */
        public static boolean validateName(String name) {
            return name != null && 
                   !name.isEmpty() && 
                   name.length() <= MAX_NAME_LENGTH;
        }

        /**
         * Validates event date in YYYY-MM-DD format.
         * 
         * @param date Event date to validate
         * @return True if date is valid
         */
        public static boolean validateDate(String date) {
            try {
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }

        /**
         * Validates event time in HH:MM 24-hour format.
         * 
         * @param time Event time to validate
         * @return True if time is valid
         */
        public static boolean validateTime(String time) {
            Pattern timePattern = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");
            return time != null && timePattern.matcher(time).matches();
        }

        /**
         * Validates event description.
         * 
         * @param description Event description to validate
         * @return True if description is valid
         */
        public static boolean validateDescription(String description) {
            return description != null && 
                   !description.isEmpty() && 
                   description.length() <= MAX_DESCRIPTION_LENGTH;
        }
    }
}
