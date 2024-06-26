package controller;

import entity.Event;
import exception.EventNotFound;
import exception.UserNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import services.EventService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/{id}")
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

    @GetMapping("/range")
    public @ResponseBody List<Event> getEventsFromTo(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "start") LocalDateTime start,
            @RequestParam(name = "end") LocalDateTime end) throws DateTimeParseException, UserNotFound {
        return eventService.getEventsInRange(userId, start, end);
    }

    @PostMapping
    @ResponseBody
    public Event addEvent(
            @RequestBody Event event
    ) throws DateTimeParseException, UserNotFound {
//        validate
        if (
                event.getDuration() == null
                ||event.getSummary() == null
                ||event.getUser() == null
                ||event.getStart() ==null
        )
            throw new IllegalArgumentException("Request doesn't have enough field of an Event");

        return eventService.addEvent(event.getUser(), event.getSummary(), event.getStart(), event.getDuration(), event.getRepeat());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteEvent(
            @PathVariable("id") long eventId
    ) throws EventNotFound {
        eventService.deleteEvent(eventId);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Event editEvent(
            @PathVariable("id") long eventId,
            @RequestBody Event event
            ) throws EventNotFound, DateTimeParseException {
        var newEvent = eventService.getEventById(eventId);
        if (event.getSummary() != null)
            newEvent = eventService.updateSummary(eventId, event.getSummary());
        if (event.getDuration() != null)
            newEvent = eventService.updateDuration(eventId, event.getDuration());
        if (event.getStart() != null)
            newEvent = eventService.updateStart(eventId, event.getStart());
        if (event.getRepeat() != null)
            newEvent = eventService.updateRepeat(eventId, event.getRepeat());
        return newEvent;
    }
}
