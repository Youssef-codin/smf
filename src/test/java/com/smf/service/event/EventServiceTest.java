
package com.smf.service.event;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.smf.model.Event;
import com.smf.model.enums.EventTypes;
import com.smf.repo.EventRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEvents() {
        Instant now = Instant.now();
        Event e1 = new Event(EventTypes.TESTING, "MAC1", "metadata1");
        Event e2 = new Event(EventTypes.DEVICE_ONLINE, "MAC2", "metadata2");

        e1.setCreatedAt(now.minusSeconds(100));
        e1.setId(UUID.randomUUID());
        e2.setCreatedAt(now.minusSeconds(50));
        e2.setId(UUID.randomUUID());

        when(eventRepository.findRecent(any())).thenReturn(Arrays.asList(e1, e2));

        List<Event> result = eventService.getEvents(200);
        assertEquals(2, result.size());
        assertEquals(e1.getMacAddress(), result.get(0).getMacAddress());
    }

    @Test
    void testGetAllEvents() {
        Event e1 = new Event(EventTypes.TESTING, "MAC1", "metadata1");
        Event e2 = new Event(EventTypes.DEVICE_ONLINE, "MAC2", "metadata2");
        e1.setId(UUID.randomUUID());
        e2.setId(UUID.randomUUID());

        when(eventRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Event> result = eventService.getAllEvents();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getEventType() == EventTypes.TESTING));
    }

    @Test
    void testHandleMethods() {

        assertDoesNotThrow(() -> eventService.handleTest("MAC1"));
        assertDoesNotThrow(() -> eventService.handleDenied("MAC2"));
        assertDoesNotThrow(() -> eventService.handleOnline("MAC3"));
        assertDoesNotThrow(() -> eventService.handleGranted("MAC4"));
        assertDoesNotThrow(() -> eventService.handleSos("MAC5"));
        assertDoesNotThrow(() -> eventService.handleOffline("MAC6"));
    }
}