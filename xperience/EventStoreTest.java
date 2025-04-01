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

/**
 * JUnit 5 test for the EventStore interface and implementations.
 * Uses EventStoreMemory as the implementation under test.
 */
public class EventStoreTest {
    
    private EventStore eventStore;
    
    @BeforeEach
    public void setup() {
        // Use EventStoreMemory implementation as specified in the assignment
        eventStore = new EventStoreMemory();
    }
    
    @Test
    public void testAddValidEvent() {
        // Valid event with all fields properly formatted
        EventStore.Result result = eventStore.addEvent(
            "Conference", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference"
        );
        
        assertTrue(result.success);
        assertEquals(1, result.eventCount);
    }
    
    @Test
    public void testAddMultipleEvents() {
        // Add first event
        EventStore.Result result1 = eventStore.addEvent(
            "Conference", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference"
        );
        
        assertTrue(result1.success);
        assertEquals(1, result1.eventCount);
        
        // Add second event
        EventStore.Result result2 = eventStore.addEvent(
            "Workshop", 
            "2025-04-16", 
            "10:00", 
            "Coding workshop"
        );
        
        assertTrue(result2.success);
        assertEquals(2, result2.eventCount);
    }
    
    @Test
    public void testAddDuplicateEvent() {
        // Add first event
        EventStore.Result result1 = eventStore.addEvent(
            "Conference", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference"
        );
        
        assertTrue(result1.success);
        
        // Try to add duplicate event (same name)
        EventStore.Result result2 = eventStore.addEvent(
            "Conference", 
            "2025-05-20", 
            "09:00", 
            "Different description"
        );
        
        assertFalse(result2.success);
        assertEquals(1, result2.eventCount);
    }
    
    @Test
    public void testInvalidName() {
        // Empty name
        EventStore.Result result = eventStore.addEvent(
            "", 
            "2025-04-15", 
            "14:30", 
            "Annual tech conference"
        );
        
        assertFalse(result.success);
        assertEquals(0, result.eventCount);
    }
    
    @Test
    public void testInvalidDate() {
        // Invalid date format
        EventStore.Result result = eventStore.addEvent(
            "Conference", 
            "15/04/2025", // Not ISO format
            "14:30", 
            "Annual tech conference"
        );
        
        assertFalse(result.success);
        assertEquals(0, result.eventCount);
    }
    
    @Test
    public void testInvalidTime() {
        // Invalid time format
        EventStore.Result result = eventStore.addEvent(
            "Conference", 
            "2025-04-15", 
            "2:30 PM", // Not 24-hour format
            "Annual tech conference"
        );
        
        assertFalse(result.success);
        assertEquals(0, result.eventCount);
    }
    
    @Test
    public void testEmptyDescription() {
        // Empty description
        EventStore.Result result = eventStore.addEvent(
            "Conference", 
            "2025-04-15", 
            "14:30", 
            ""
        );
        
        assertFalse(result.success);
        assertEquals(0, result.eventCount);
    }
    
    @Test
    public void testValidator() {
        // Test the validator directly for coverage
        
        // Valid inputs
        assertTrue(EventStore.EventValidator.validateName("Conference"));
        assertTrue(EventStore.EventValidator.validateDate("2025-04-15"));
        assertTrue(EventStore.EventValidator.validateTime("14:30"));
        assertTrue(EventStore.EventValidator.validateDescription("Description"));
        
        // Invalid inputs
        assertFalse(EventStore.EventValidator.validateDate("2025/04/15"));
        assertFalse(EventStore.EventValidator.validateTime("2:30 PM"));
        assertFalse(EventStore.EventValidator.validateName(null));
        assertFalse(EventStore.EventValidator.validateDescription(null));
    }
}

