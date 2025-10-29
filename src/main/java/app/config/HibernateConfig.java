package app.config;

import Security.entities.Role;
import Security.entities.User;
import app.entities.Guide;
import app.entities.Trip;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    public static void setTest(Boolean test) {
        isTest = test;
    }

    public static Boolean getTest() {
        return isTest;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = createEMF(getTest());
        }
        return emf;
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null || !emfTest.isOpen()) {
            setTest(true);
            emfTest = createEMF(getTest());
        }
        return emfTest;
    }

    // TODO: IMPORTANT: Add Entity classes here for them to be registered with Hibernate
    private static void getAnnotationConfiguration(Configuration configuration) {
         configuration.addAnnotatedClass(Trip.class);
         configuration.addAnnotatedClass(Guide.class);
         configuration.addAnnotatedClass(User.class);
         configuration.addAnnotatedClass(Role.class);
    }

    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            // Set the properties
            setBaseProperties(props);
            if (forTest) {
                props = setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                props = setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
            return emf;
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static String getDBName() {
        return Utils.getPropertyValue("db.name", "from-pom.properties");
    }

    private static Properties setBaseProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "create");  // set to "update" when in production
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
        return props;
    }

    private static Properties setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props){
        String DBName = getDBName();

        // Hvis environment variable CONNECTION_STR ikke er sat, brug db-container navnet
        String connectionStr = System.getenv("CONNECTION_STR");
        if (connectionStr == null) connectionStr = "jdbc:postgresql://localhost:5432/";


        String username = System.getenv("DB_USERNAME");
        if (username == null) username = "postgres";

        String password = System.getenv("DB_PASSWORD");
        if (password == null) password = "postgres";

        props.put("hibernate.connection.url", connectionStr + DBName);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);

        return props;
    }

    private static Properties setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:16.2:///test_db");
        props.put("hibernate.archive.autodetection", "hbm,class");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        return props;
    }
}