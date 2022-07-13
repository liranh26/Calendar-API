package ajbc.doodle.calendar;

import org.springframework.context.annotation.Configuration;

import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.EventUser;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.User;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@EnableAspectJAutoProxy
@Configuration

public class AppConfig {

	@Value("${user}")
	private String user;
	@Value("${password}")
	private String password;
	@Value("${server_address}")
	private String serverAddress;
	@Value("${port}")
	private String port;
	@Value("${db_name}")
	private String databaseName;
	@Value("${server_name}")
	private String serverName;
	@Value("${driver_class_name}")
	private String driverClassName;
	

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(createUrl());
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		
		return dataSource;
	}
	
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(DataSource  dataSource) {
		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setAnnotatedClasses(User.class, Event.class, Notification.class, EventUser.class);
		
		Properties props = new Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		props.setProperty("hibernate.show_sql", "false");
		props.setProperty("hibernate.format_sql", "true");
		
		factory.setHibernateProperties(props);
		return factory;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory) {
		return new HibernateTemplate(sessionFactory);
	}
	
	
	@Bean
	public HibernateTransactionManager txMgr(SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}
	
	
	private String createUrl() {
		return "jdbc:sqlserver://" + serverAddress + ":" + port + ";databaseName=" + databaseName + ";servername="
				+ serverName + ";" + ";encrypt=false";
	}
}
