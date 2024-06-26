package services;

import entity.Event;
import entity.RepeatType;
import entity.User;
import exception.EventNotFound;
import exception.UserNotFound;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
public interface EventService {

    Event addEvent(User user, String summary, LocalDateTime start, Duration duration, RepeatType repeatType) throws UserNotFound;

    //    assume that the using frequency is low.
    Event getEventById(long eventId) throws EventNotFound;

    List<Event> getEvents(long userId) throws UserNotFound;

    List<Event> getEventsByDate(long userId, LocalDate date) throws UserNotFound;

    List<Event> getEventsByTime(long userId, LocalTime time) throws UserNotFound;

    List<Event> getEventsByDateTime(long userId, LocalDate date, LocalTime time) throws UserNotFound;

    List<Event> getEventsInRange(long userId, LocalDateTime start, LocalDateTime end) throws UserNotFound;
    void deleteEvent(long eventId) throws EventNotFound;

    Event updateSummary(long eventId, String summary) throws EventNotFound;

    Event updateStart(long eventId, LocalDateTime start) throws EventNotFound;

    Event updateDuration(long eventId, Duration duration) throws EventNotFound;

    Event updateRepeat(long eventId, RepeatType repeatType) throws EventNotFound;
}
