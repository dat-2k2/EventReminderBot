import config.BotConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.longpolling.BotSession;

public class ClientApplication {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                BotConfiguration.class
        );
        context.getBean(BotSession.class);
        Thread.currentThread().join();
    }
}
