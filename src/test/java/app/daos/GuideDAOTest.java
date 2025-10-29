package app.daos;

import app.config.HibernateConfig;
import app.entities.Guide;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuideDAOTest {
    private static EntityManagerFactory emf;
    private static GuideDAO dao;
    //private User user;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = new GuideDAO(emf);
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

            Guide g3 = Guide.builder()
                    .name("Sara Holm")
                    .email("sara@example.com")
                    .phone("11112222")
                    .yearsOfExperience(3)
                    .build();

            em.persist(g1);
            em.persist(g2);
            em.persist(g3);

            tx.commit();

        }catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }

    }

    @Test
    void create() {
        Guide newGuide = Guide.builder()
                .name("Lumiere El-Mir")
                .email("Lumiere@example.com")
                .phone("04040404")
                .yearsOfExperience(4)
                .build();

        Guide created= dao.create(newGuide);
        assertNotNull(created.getId());
        assertEquals("Lumiere El-Mir", created.getName());

    }

    @Test
    void getById() {
        Guide found = dao.getById(1);
        assertNotNull(found);
        assertEquals("Mariam El-Mir", found.getName());
    }

    @Test
    void update() {
        Guide guide = dao.getById(2);
        guide.setYearsOfExperience(9);

        Guide updated = dao.update(guide);
        assertEquals(9, updated.getYearsOfExperience());

    }

    @Test
    void getAll() {
        List<Guide> guides = dao.getAll();

        assertNotNull(guides);
        assertEquals(3, guides.size());
    }

    @Test
    void delete() {
        Guide g = dao.getById(3);
        boolean deleted = dao.delete(g.getId());
        assertTrue(deleted);
        assertNull(dao.getById(g.getId()));
    }
}