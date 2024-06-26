package services.impl;

import entity.Event;
import entity.RepeatType;
import entity.User;
import exception.EventNotFound;
import exception.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import repos.EventRepository;
import repos.UserRepository;
import services.EventService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public Event addEvent(User user, String summary, LocalDateTime start, Duration duration, RepeatType repeatType) throws UserNotFound {
        var _user = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFound(user.getId()));
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
        return eventRepository.findEvents(userRepository.findById(userId).orElseThrow(() -> new UserNotFound(userId)));
    }

    private boolean onThisDay(Event e, LocalDate date){
        var start = e.getStart().toLocalDate();
        return switch (e.getRepeat()){
            case NONE ->  start.equals(date);
            case HOURLY, DAILY -> true;
            case WEEKLY -> start.getDayOfWeek().equals(date.getDayOfWeek());
            case MONTHLY -> start.getDayOfMonth() == date.getDayOfMonth();
        };
    }

    private boolean atThisTime(Event e, LocalTime time){
        var start = e.getStart().toLocalTime();
        return switch (e.getRepeat()){
            case HOURLY -> start.getMinute() == time.getMinute();
            default -> start.equals(time);
        };
    }

    @Override
    public List<Event> getEventsByDate(long userId, LocalDate date) throws UserNotFound {
        List<Event> allEvents = getEvents(userId);
        return allEvents
                .stream()
                .filter(event -> onThisDay(event, date)).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsByTime(long userId, LocalTime time) throws UserNotFound {
        List<Event> allEvents = getEvents(userId);
        return allEvents
                .stream()
                .filter(event -> atThisTime(event, time)).collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsByDateTime(long userId, LocalDate date, LocalTime time) throws UserNotFound {
        return getEventsByDate(userId,date).stream()
                .filter(event -> atThisTime(event, time))
                .collect(Collectors.toList());
    }

    @Deprecated
    @Override
    public List<Event> getEventsInRange(long userId, LocalDateTime start, LocalDateTime end) throws UserNotFound {
        if (!userRepository.existsById(userId)) throw new UserNotFound(userId);
        return eventRepository.findEventsFromTo(userRepository.getReferenceById(userId), start, end);
    }

    @Override
    public void deleteEvent(long eventId) throws EventNotFound {
        if (!eventRepository.existsById(eventId))
            throw new EventNotFound(eventId);
        eventRepository.deleteById(eventId);
    }


//
//    @Override
//    public Event updateSummary(long eventId, String summary) throws EventNotFound {
//        var currentEvent = eventRepository.findById(eventId).orElseThrow(()->new EventNotFound(eventId));
//        eventRepository.deleteById(eventId);
//        currentEvent.setSummary(summary);
//        return eventRepository.save(currentEvent);
//    }
//
//    @Override
//    public Event updateStart(long eventId, LocalDateTime start) throws EventNotFound {
//        var currentEvent = eventRepository.findById(eventId).orElseThrow(()->new EventNotFound(eventId));
//        eventRepository.deleteById(eventId);
//        currentEvent.setStart(start);
//        return eventRepository.save(currentEvent);
//    }
//    @Override
//    public Event updateDuration(long eventId, Duration duration) throws EventNotFound {
//        var currentEvent = eventRepository.findById(eventId).orElseThrow(()->new EventNotFound(eventId));
//        eventRepository.deleteById(eventId);
//        currentEvent.setDuration(duration);
//        return eventRepository.save(currentEvent);
//    }
//    @Override
//    public Event updateRepeat(long eventId, RepeatType repeatType) throws EventNotFound {
//        var currentEvent = eventRepository.findById(eventId).orElseThrow(()->new EventNotFound(eventId));
//        eventRepository.deleteById(eventId);
//        currentEvent.setRepeat(repeatType);
//        return eventRepository.save(currentEvent);
//    }
    @Override
    public Event updateSummary(long eventId, String summary) throws EventNotFound {
        if (!eventRepository.existsById(eventId))
            throw new EventNotFound(eventId);
        eventRepository.updateSummary(eventId, summary);
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }

    @Override
    public Event updateStart(long eventId, LocalDateTime start) throws EventNotFound {
        if (!eventRepository.existsById(eventId))
            throw new EventNotFound(eventId);
        eventRepository.updateStart(eventId, start);
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }
    @Override
    public Event updateDuration(long eventId, Duration duration) throws EventNotFound {
        if (!eventRepository.existsById(eventId))
            throw new EventNotFound(eventId);
        eventRepository.updateDuration(eventId, duration);
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }
    @Override
    public Event updateRepeat(long eventId, RepeatType repeatType) throws EventNotFound {
        if (!eventRepository.existsById(eventId))
            throw new EventNotFound(eventId);
        eventRepository.updateRepeat(eventId, repeatType);
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFound(eventId));
    }

}
