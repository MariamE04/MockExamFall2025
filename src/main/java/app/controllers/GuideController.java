package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import jakarta.persistence.EntityManagerFactory;

public class GuideController {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private GuideDAO guideDAO = new GuideDAO(emf);
    private TripDAO tripDAO = new TripDAO(emf);
}
