package td.book.tdbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// extend SpringBootServletInitializer to deploy springboot app to tomcat
@SpringBootApplication
public class TdbookApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TdbookApplication.class, args);
	}

}
