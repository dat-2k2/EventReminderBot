
# Event Reminder Telegram Bot
Bot: @hsai_er_bot

This is a Telegram bot used to remind events. The Security layer is uncompleted, so the API must be private.

Link Docker Hub: https://hub.docker.com/repository/docker/mydockerdatto/eventreminderbot/tags.

In docker-compose.yml, replace ```service.client.build``` with ```service.client.image:mydockerdatto/eventreminderbot:client-latest``` and ```service.server.build``` with ```service.client.image:mydockerdatto/eventreminderbot:server-latest```.

## Overview
Recurring an event is specified inside the Event. This keeps the database stable and minimized but requires backend logic or even frontend to find all the recurrences. 

The application contains 2 parts: client and server. The server is a Spring Web MVC application that exposes REST API for the client that handles Telegram requests.

## Dependencies
- Telegram API (Java)
- Spring Web MVC
- Spring REST 
- Spring JPA + Hibernate ORM
- PostgreSQL
- Tomcat Server 7

## Features
### Event
- Create event (summary, date + time, duration)
- List events (next, day, week)
- Delete event
- Update event
### Recurring events
- Create reccuring events (hourly, daily, weekly, monthly)
- Show recurring events
- Update recurring events
- Delete recurring events
### Reminder
- Remind about upcoming event (1 hour in advance)
  
## Limitation
- If an event was recurred at moment X, message about that event will be shown but the start date is its first recurrence.
- No security, adding later.
- No ics import.
## Usage
- /add Add an event with these fields, separated by whitespace:
  {Summary (no whitespace} {Date(yyyy-MM-dd)} {Time(HH:mm)} {Duration(ISO-8601)}.
  Example: /add test-event 2024-06-26 12:31 PT10H
- /add test-event 2024-06-26 23:11 P1DT2H.
- /help Show all commands and usage.
- /updatestart Update start date time to [date] and time to [time] of an event: updatestart "{date} {time}".
  Example: /updatestart 2023-01-01 12:00
- /week Get all events in this week
- /find Find all of your events by date, by time or both. Recurred events is showed as the first recurrence. If no date or time is provided, get all of your events. 
  Use /find [date yyyy-MM-dd] + [time HH:mm] 
  Example: /find, /find 2024-06-26, /find 2024-06-26 12:00
-  /today Get all your events today.
- /updateduration
    Update new duration for event. Duration needs to follow ISO 8061. 
    Use: /updateduration {id of event} {new duration}.
    Example: /updateduration 1 PT10H
- /updatesummary
  Update summary of an event. Summary needs to be without whitespace.
  Example /updatesummary 252 A-new-summary
- /register Sign up. You will be logged in automatically.

Below each event there are 2 options: reschedule the event or delete it. 
- Reschedule :
  ![image](https://github.com/dat-2k2/EventReminderBot/assets/73431073/9320f161-9341-4a1b-af7e-f8db983cde91)
- Delete:
  ![image](https://github.com/dat-2k2/EventReminderBot/assets/73431073/a1182022-6944-406e-9b14-819f102dbb64)

## Encountered Problems (while developing)
### Deployment
In case the application.properties doesn't exist
- In client/src/main/resources/ create file application.properties with 2 fields: bot.name - your bot's name and
  bot.token - your bot's token (retrieved from Telegram).
- If you want to change database configuration, change at .env first, then look into docker-compose and
  database.properties to change respective properties.
- Run ```docker compose up```.
### Hibernate
- Hibernate doesn't automatically deduce PostgresSQLDialect (though it keeps saying it does later). Needs to configure
_Database_/_DatabasePlatform_ (and other properties of jpa) in JpaVendorAdapter.

- Hibernate ORM creates columns with their object name, no matter how the @Column.name is set.
### Telegram Bot APIs

Latest versions have lots of changes, recommend to
see [examples](https://github.com/rubenlagus/TelegramBotsExample/tree/master). Current stable version for all
dependencies is **7.2.0**.

### Database host in Docker

[Explanation](https://docs.docker.com/network/drivers/bridge/#differences-between-user-defined-bridges-and-the-default-bridge)

> Containers on the default bridge network can only access each other by IP addresses, unless you use the --link option,
> which is considered legacy. On a user-defined bridge network, containers can resolve each other by name or alias.

> Imagine an application with a web front-end and a database back-end. If you call your containers web and db, the web
> container can connect to the db container at db, no matter which Docker host the application stack is running on.

So in this case the database url is _{your_db}:5432_, not localhost.

### Spring and JPA

Current Spring only supports JPA up to **3.2.0** (18 Jun 2024)

### Deployment

To expose the REST APIs, the application needs to run on a server, which means it is an Spring Web application. The web
application is packaged in a **war** file, and later copied to a Tomcat server to be active. Current examples often use
Spring Boot so the application doesn't need to be deployed, instead an embedded Tomcat server is automatically
configured after initialization.

The Telegram Client uses another implementation of Servlet - Jetty; put it in the same package with server using Tomcat
causes conflict.

### @RequestParam
Maven compiler, if not specified, doesn't include parameters' names. 



