package zerobase.sijak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class SijakApplication {

    public static void main(String[] args) {
        SpringApplication.run(SijakApplication.class, args);
    }
}
