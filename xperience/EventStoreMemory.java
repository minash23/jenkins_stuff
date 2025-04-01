/************************************************
 *
 * Author: Mina Shehata
 * Assignment: SeaCure (Program 4)
 * Class: Software and System Security
 *
 ************************************************/
package xperience;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory implementation of EventStore.
 */
public class EventStoreMemory implements EventStore {
    private final Set<String> eventNames = ConcurrentHashMap.newKeySet();
    private final AtomicInteger eventCount = new AtomicInteger(0);
    
    @Override
    public Result addEvent(String name, String date, String time, String description) {
        // Validate the event
        if (!EventStore.EventValidator.validate(name, date, time, description)) {
            return new Result(false, eventCount.get());
        }
        
        // Check for duplicate name
        if (eventNames.contains(name)) {
            return new Result(false, eventCount.get());
        }
        
        // Add event
        eventNames.add(name);
        int count = eventCount.incrementAndGet();
        return new Result(true, count);
    }
}
