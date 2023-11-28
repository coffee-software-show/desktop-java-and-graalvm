package bootiful.java21;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless" , "false");
        new SpringApplicationBuilder()
                .headless(false)
                .sources(Application.class)
                .run(args);
    }

    @Bean
    ApplicationRunner uiRunner() {
        return args -> {
            var jFrame = new JFrame("the title");
            jFrame.add(new JLabel("hello, itext fans!"));
            jFrame.setSize(500, 500);
            jFrame.setVisible(true);
            jFrame.setLocationRelativeTo(null);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.out.println("jframe started");
        };
    }

}
