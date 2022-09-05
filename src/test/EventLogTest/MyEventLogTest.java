package EventLogTest;

import model.Event;
import model.EventLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyEventLogTest {

    EventLog logger;

    @BeforeEach
    public void init() {
        logger = EventLog.getInstance();
        assertNotNull(logger);
    }

    @Test
    public void testLog() throws InterruptedException {
        Event oneEvent = new Event("Event 1");

        Event sameDayDiffDesc = new Event("Event 2");
        // set sameDayDiffDesc to the same date as oneEvent
        sameDayDiffDesc.getDate().setTime(oneEvent.getDate().getTime());
        Thread.sleep(1000);
        Event diffDaySameDesc = new Event("Event 1");
        Event diffDayDiffDesc = new Event("Event 2");

        logger.logEvent(oneEvent);

        // make an object with different class
        String thisIsNotAnEvent = "";

        // there's only one event in the log
        for (Event logEvent : logger) {
            assertTrue(oneEvent.equals(logEvent));
            assertTrue(oneEvent.getDate().equals(logEvent.getDate()));
            assertTrue(oneEvent.getDescription().equals(logEvent.getDescription()));
            assertEquals(oneEvent.hashCode(), logEvent.hashCode());
            assertFalse(oneEvent.equals(null)); // test null
            assertFalse(oneEvent.equals(thisIsNotAnEvent)); // test different object
            assertFalse(logEvent.equals(sameDayDiffDesc)); // test same date, different description
            assertFalse(logEvent.equals(diffDaySameDesc)); // test different date, same description
            assertFalse(logEvent.equals(diffDayDiffDesc)); // test different date, same description
            assertNotNull(oneEvent.toString());
        }

        Event twoEvent = new Event("Event 2");
        logger.logEvent(twoEvent);

        // now we have 2 events in log
        int numEvents = 0;
        for (Event event : logger) {
            numEvents++;
        }
        assertEquals(numEvents, 2);
        logger.clear();
        numEvents = 0;
        for (Event event : logger) {
            numEvents++;
        }
        // once clear is called, the EventLog log one entry of
        // logEvent(new Event("Event log cleared."));
        // therefore the number of events after calling clear() is one
        assertEquals(numEvents, 1);
    }

}
