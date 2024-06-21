package services;

import entity.Event;
import entity.RepeatType;
import entity.User;
import repos.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event addEvent(Event event){
        return eventRepository.save(event);
    }

//    assume that the using frequency is low.
    public List<Event> getEventsByUser(User user){
        var eventExample = new Event();
        eventExample.setUser(user);
        return eventRepository.findAll(Example.of(eventExample));
    }

    public List<Event> getEventsByDate(User user, Instant start){
        var eventExample = new Event();
        eventExample.setUser(user);
        eventExample.setStart(start);

        return eventRepository.findAll(Example.of(eventExample));
    }

    public void deleteEvent(long eventId){
        eventRepository.deleteById(eventId);
    }

    public Event updateEvent(long oldEventId, Event newEvent){
        eventRepository.deleteById(oldEventId);
        return eventRepository.save(newEvent);
    }

    public void setRepeatType(long eventId, RepeatType repeatType){
        eventRepository.changeEventRepeatType(eventId, repeatType);
    }


}
