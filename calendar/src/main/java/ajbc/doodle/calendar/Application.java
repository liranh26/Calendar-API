package ajbc.doodle.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.utils.SeedDB;


@EnableScheduling
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class Application {

	
	public final static Logger logger = LoggerFactory.getLogger(Application.class);
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		

	}

}
