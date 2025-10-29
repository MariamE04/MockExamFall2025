package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GuideControllerTest {
    private static Javalin app;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Start Javalin-serveren
        app = ApplicationConfig.getInstance().startServer(7072);

        // REST-Assured base setup
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7072;
        RestAssured.basePath = "/api/TripPlanning/guides";
    }

    @BeforeEach
    void prepareTestData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            // ryd tabeller
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE guide_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE trip_id_seq RESTART WITH 1").executeUpdate();


            // opret guides
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

            // opret trips
            Trip t1 = Trip.builder()
                    .name("Mountain Hike")
                    .startTrip(LocalDateTime.of(2025, 5, 10, 8, 0))
                    .endTrip(LocalDateTime.of(2025, 5, 10, 18, 0))
                    .category(Category.HIKING)
                    .price(1200)
                    .guide(g1)
                    .build();

            Trip t2 = Trip.builder()
                    .name("City Tour")
                    .startTrip(LocalDateTime.of(2025, 6, 15, 10, 0))
                    .endTrip(LocalDateTime.of(2025, 6, 15, 14, 0))
                    .category(Category.BEACH)
                    .price(800)
                    .guide(g1)
                    .build();

            g1.getTrips().add(t1);
            g1.getTrips().add(t2);

            em.persist(g1);
            em.persist(g2);
            em.persist(t1);
            em.persist(t2);
            tx.commit();
        } finally {
            em.close();
        }
    }


    @Test
    void getAllGuides() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].name", notNullValue());
    }

    @Test
    void getGuideById() {
        given()
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Mariam El-Mir"))
                .body("tripNames.size()", equalTo(2))
                .body("tripNames[0]", equalTo("Mountain Hike"))
                .body("tripNames[1]", equalTo("City Tour"));
    }

    @Test
    void createGuide() {
        String json = """
    {
      "name": "Anna K. Jensen",
      "email": "anna.kj@example.com",
      "phone": "99999999",
      "yearsOfExperience": 6
    }
""";

        given()
                .header("Content-Type", "application/json")
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Anna K. Jensen"))
                .body("yearsOfExperience", equalTo(6));
    }

    @Test
    void updateGuide() {
        String json = """
            {
              "name": "Anna K. Jensen",
              "email": "anna.kj@example.com",
              "phone": "12345678",
              "yearsOfExperience": 6
            }
            """;

        given()
                .header("Content-Type", "application/json")
                .body(json)
                .when()
                .put("/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Anna K. Jensen"))
                .body("yearsOfExperience", equalTo(6));
    }

    @Test
    void deleteGuide() {
        given()
                .when()
                .delete("/2")
                .then()
                .statusCode(anyOf(is(200), is(204)));

        // check it is deleted
        given()
                .when()
                .get("/2")
                .then()
                .statusCode(404);
    }

    @Test
    void getTripsForGuide() {
        given()
                .when()
                .get("/1/trips")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0]", equalTo("Mountain Hike"))
                .body("[1]", equalTo("City Tour"));

    }

    @Test
    void getTotalTripPrice() {
        int guideId = 1;
        given()
                .when()
                .get("/totalprice")
                .then()
                .statusCode(200)
                .body(String.valueOf(guideId), equalTo(2000.0f));
    }
}