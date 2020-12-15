package io.ningyu;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Client启动类
 *
 * @author jiuye
 */
@SpringBootApplication
@ServletComponentScan
public class ClientApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ClientApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
	}
}
