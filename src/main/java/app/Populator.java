package app;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;

public class Populator {

    public static void populate(EntityManagerFactory emf) {
        GuideDAO guideDAO = new GuideDAO(emf);
        TripDAO tripDAO = new TripDAO(emf);

        // --- GUIDES ---
        Guide guide1 = Guide.builder()
                .name("Anna Jensen")
                .email("anna@example.com")
                .phone("12345678")
                .yearsOfExperience(5)
                .build();

        Guide guide2 = Guide.builder()
                .name("Mikkel Sørensen")
                .email("mikkel@example.com")
                .phone("87654321")
                .yearsOfExperience(8)
                .build();

        Guide guide3 = Guide.builder()
                .name("Sara Holm")
                .email("sara@example.com")
                .phone("11223344")
                .yearsOfExperience(3)
                .build();

        guideDAO.create(guide1);
        guideDAO.create(guide2);
        guideDAO.create(guide3);

        // --- TRIPS ---
        Trip trip1 = Trip.builder()
                .name("Sunny Beach Tour")
                .startTrip(LocalDateTime.of(2025, 6, 10, 9, 0))
                .endTrip(LocalDateTime.of(2025, 6, 15, 18, 0))
                .latitude(42.698334)
                .longitude(27.710389)
                .price(799.99)
                .category(Category.BEACH)
                .guide(guide1)
                .build();

        Trip trip2 = Trip.builder()
                .name("City Break Copenhagen")
                .startTrip(LocalDateTime.of(2025, 5, 1, 10, 0))
                .endTrip(LocalDateTime.of(2025, 5, 3, 20, 0))
                .latitude(55.676098)
                .longitude(12.568337)
                .price(499.50)
                .category(Category.CITY)
                .guide(guide2)
                .build();

        Trip trip3 = Trip.builder()
                .name("Forest Adventure in Sweden")
                .startTrip(LocalDateTime.of(2025, 7, 1, 8, 0))
                .endTrip(LocalDateTime.of(2025, 7, 10, 18, 0))
                .latitude(59.3293)
                .longitude(18.0686)
                .price(999.00)
                .category(Category.FOREST)
                .guide(guide3)
                .build();

        tripDAO.create(trip1);
        tripDAO.create(trip2);
        tripDAO.create(trip3);

        System.out.println("Database populated with sample data!");
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        populate(emf);
    }
}
