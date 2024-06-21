import config.ServerInitializer;
import controller.UserController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ServerInitializer.class
        );

        context.getBean(UserController.class);
        Thread.currentThread().join();
    }
}
