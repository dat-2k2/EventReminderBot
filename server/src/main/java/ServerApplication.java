import config.ServerInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ServerInitializer.class
        );
        Thread.currentThread().join();
    }
}
