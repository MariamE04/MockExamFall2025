package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

import static app.Populator.populate;


public class Main {
    public static void main(String[] args) {
       EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        ApplicationConfig config = ApplicationConfig.getInstance();
        config.startServer(7072);

        populate(emf);
    }

}