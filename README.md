
# Event Reminder Telegram Bot (insecure)
This is a Telegram bot used to remind events. The Security layer is uncompleted, so the API must be private.

## Dependencies
- Telegram API (Java)
- Spring Web MVC
- Spring REST 
- Spring JPA + Hibernate ORM
- PostgreSQL
- Tomcat Server

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

## Using
In case the application.properties doesn't exist
- In client/src/main/resources/ create file application.properties with 2 fields: bot.name - your bot's name and
  bot.token - your bot's token (retrieved from Telegram).
- If you want to change database configuration, change at .env first, then look into docker-compose and
  database.properties to change respective properties.
- Run ```docker compose up```.

## Encountered Problems

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



