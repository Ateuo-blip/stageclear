package io.stageclear.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.stageclear.common.mapper")
public class CustomerCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerCoreApplication.class, args);
    }

}
