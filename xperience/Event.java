/************************************************
 *
 * Author: Mina Shehata
 * Assignment: XPerience Server
 * Class: Software and System Security
 *
 ************************************************/

package xperience;

/**
 * Represents an event with a name, date, time, and description.
 */
public class Event {

    /** The name of the event. */
    private String name;

    /** The date of the event. */
    private String date;

    /** The time of the event. */
    private String time;

    /** A description of the event. */
    private String description;

    /**
     * Constructs an {@code Event} with the specified name, date, time, and description.
     *
     * @param name        The name of the event.
     * @param date        The date of the event.
     * @param time        The time of the event.
     * @param description A description of the event.
     */
    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    /**
     * Returns the name of the event.
     *
     * @return The event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the date of the event.
     *
     * @return The event date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the time of the event.
     *
     * @return The event time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns the description of the event.
     *
     * @return The event description.
     */
    public String getDescription() {
        return description;
    }
}

