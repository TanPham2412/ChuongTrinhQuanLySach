package nhom5.phamminhtan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PhamminhtanApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhamminhtanApplication.class, args);
    }
}
