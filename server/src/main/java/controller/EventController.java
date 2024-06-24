package controller;

import entity.Event;
import entity.RepeatType;
import exception.EventNotFound;
import exception.UserNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/get/{id}")
    @ResponseBody
    public Event getEventsById(
            @PathVariable("id") long eventId
    ) throws EventNotFound {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/find")
    public @ResponseBody List<Event> getEventsByDateTime(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "time", required = false) LocalTime time) throws DateTimeParseException, UserNotFound {
        if (date == null) {
            if (time == null)
                return eventService.getEvents(userId);
            else
                return eventService.getEventsByTime(userId, time);
        } else {
            if (time == null)
                return eventService.getEventsByDate(userId, date);
            else
                return eventService.getEventsByDateTime(userId, date, time);
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public Event addEvent(
            @RequestBody Event event
    ) throws DateTimeParseException {
        return eventService.addEvent(event);
    }

    @PostMapping("/test-add")
    @ResponseBody
    public Event testAddEvent(@RequestBody Event event) throws DateTimeParseException {
        return eventService.addEvent(event);
    }

    @DeleteMapping("/delete")
    public @ResponseBody void deleteEvent(
            @RequestBody long eventId
    ) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping("/edit/{id}")
    public @ResponseBody Event editEvent(
            @PathVariable("id") long eventId,
            @RequestBody Event newEvent
    ) throws EventNotFound, DateTimeParseException {
        var _event = eventService.getEventById(eventId);
        return eventService.updateEvent(_event.getId(), newEvent);
    }
}
