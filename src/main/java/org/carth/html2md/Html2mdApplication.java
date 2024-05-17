package org.carth.html2md;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class Html2mdApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Html2mdApplication.class, args)));
    }
}
