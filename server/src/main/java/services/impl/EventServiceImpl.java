package services.impl;

import entity.Event;
import entity.RepeatType;
import exception.EventNotFound;
import exception.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repos.EventRepository;
import repos.UserRepository;
import services.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event addEvent(long userId, String summary, LocalDateTime start, Duration duration, RepeatType repeatType) throws UserNotFound {
        var _user = userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId));
        var newEvent = new Event();
        newEvent.setDuration(duration);
        newEvent.setSummary(summary);
        newEvent.setRepeat(repeatType);
        newEvent.setStart(start);
        newEvent.setUser(_user);
        return eventRepository.save(newEvent);
    }

    @Override
    //    assume that the using frequency is low.
    public Event getEventById(long eventId) throws EventNotFound {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }

    @Override
    public List<Event> getEvents(long userId) throws UserNotFound {
        return eventRepository.findEvents(userRepository.getReferenceById(userId));
    }

    private boolean onThisDay(Event e, LocalDate date){
        var start = e.getStart();

        var dtf = switch (e.getRepeat()){
            case NONE,HOURLY -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case DAILY -> DateTimeFormatter.ofPattern("yyyy-MM");
            case WEEKLY -> DateTimeFormatter.ofPattern("F");
            case MONTHLY -> DateTimeFormatter.ofPattern("yyyy-dd");
        };
        return start.format(dtf).equals(date.format(dtf));
    }

    private boolean atThisTime(Event e, LocalTime time){
        var start = e.getStart();
        var dtf = switch (e.getRepeat()){
            case HOURLY -> DateTimeFormatter.ofPattern("HH");
            default -> DateTimeFormatter.ofPattern("HH:mm");
        };
        return start.format(dtf).equals(time.format(dtf));
    }

    @Override
    public List<Event> getEventsByDate(long userId, LocalDate date) throws UserNotFound {
        List<Event> allEvents = getEvents(userId);
        return allEvents
                .parallelStream()
                .map(event -> onThisDay(event, date)?event:null)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsByTime(long userId, LocalTime time) throws UserNotFound {
        List<Event> allEvents = getEvents(userId);
        return allEvents
                .parallelStream()
                .map(event -> atThisTime(event, time)?event:null)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsByDateTime(long userId, LocalDate date, LocalTime time) throws UserNotFound {
        return getEventsByDate(userId,date).parallelStream()
                .map(event -> atThisTime(event, time)?event:null)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsInRange(long userId, LocalDateTime start, LocalDateTime end) throws UserNotFound {
        if (!userRepository.existsById(userId)) throw new UserNotFound(userId);
        return eventRepository.findEventsFromTo(userRepository.getReferenceById(userId), start, end);
    }

    @Override
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public Event updateEvent(long oldEventId, Event newEvent) {
        eventRepository.deleteById(oldEventId);
        return eventRepository.save(newEvent);
    }

}
