package app.daos;

import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripDAOTest {
    private static EntityManagerFactory emf;
    private static TripDAO dao;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = new TripDAO(emf);
    }

    @BeforeEach
    void setUpTestData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE guide_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE trip_id_seq RESTART WITH 1").executeUpdate();

            // === Opret guider ===
            Guide g1 = Guide.builder()
                    .name("Mariam El-Mir")
                    .email("mariam@example.com")
                    .phone("12345678")
                    .yearsOfExperience(5)
                    .build();

            Guide g2 = Guide.builder()
                    .name("Jonas Jensen")
                    .email("jonas@example.com")
                    .phone("87654321")
                    .yearsOfExperience(8)
                    .build();

            em.persist(g1);
            em.persist(g2);

            // === Opret trips ===
            Trip t1 = Trip.builder()
                    .name("Mountain Hike")
                    .startTrip(LocalDateTime.of(2025, 5, 10, 8, 0))
                    .endTrip(LocalDateTime.of(2025, 5, 10, 18, 0))
                    .latitude(55.6761)
                    .longitude(12.5683)
                    .price(499.99)
                    .category(Category.HIKING)
                    .guide(g1)
                    .build();

            Trip t2 = Trip.builder()
                    .name("Copenhagen City Tour")
                    .startTrip(LocalDateTime.of(2025, 6, 1, 10, 0))
                    .endTrip(LocalDateTime.of(2025, 6, 1, 15, 0))
                    .latitude(55.6850)
                    .longitude(12.5790)
                    .price(299.50)
                    .category(Category.CITY)
                    .guide(g2)
                    .build();

            Trip t3 = Trip.builder()
                    .name("Forest Camping")
                    .startTrip(LocalDateTime.of(2025, 7, 20, 9, 0))
                    .endTrip(LocalDateTime.of(2025, 7, 22, 12, 0))
                    .latitude(56.1234)
                    .longitude(9.8765)
                    .price(899.00)
                    .category(Category.BEACH)
                    .guide(g1)
                    .build();

            em.persist(t1);
            em.persist(t2);
            em.persist(t3);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    @Test
    void create() {
        EntityManager em = emf.createEntityManager();
        Guide guide = em.find(Guide.class, 1); // Brug eksisterende guide
        em.close();

        Trip newTrip = Trip.builder()
                .name("Desert Adventure")
                .startTrip(LocalDateTime.of(2025, 9, 1, 9, 0))
                .endTrip(LocalDateTime.of(2025, 9, 3, 18, 0))
                .latitude(30.0444)
                .longitude(31.2357)
                .price(1200.00)
                .category(Category.BEACH)
                .guide(guide)
                .build();

        Trip created = dao.create(newTrip);

        assertNotNull(created.getId());
        assertEquals("Desert Adventure", created.getName());
        assertEquals(Category.BEACH, created.getCategory());
    }

    @Test
    void getById() {
        Trip found = dao.getById(1);
        assertNotNull(found);
        assertEquals("Mountain Hike", found.getName());
        assertEquals(Category.HIKING, found.getCategory());
    }

    @Test
    void update() {
        Trip trip = dao.getById(2);
        trip.setPrice(350.00);
        Trip updated = dao.update(trip);

        assertEquals(350.00, updated.getPrice());
    }

    @Test
    void getAll() {
        List<Trip> trips = dao.getAll();
        assertNotNull(trips);
        assertEquals(3, trips.size());
    }

    @Test
    void delete() {
        Trip trip = dao.getById(3);
        boolean deleted = dao.delete(trip.getId());
        assertTrue(deleted);
        assertNull(dao.getById(trip.getId()));
    }
}