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

class TripControllerTest {
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
        RestAssured.basePath = "/api/TripPlanning/trips";
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
    void getAllTrips() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    void getById() {
        given()
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Mountain Hike"))
                .body("packingItems", notNullValue());

        given()
                .when()
                .get("/999")
                .then()
                .statusCode(404)
                .body(equalTo("Trip not found"));
    }

    @Test
    void createTrip() {
        String json = """
                {
                  "name": "Sunny Beach Trip",
                  "startTrip": "2025-11-10T09:00:00",
                  "endTrip": "2025-11-15T18:00:00",
                  "latitude": 56.12345,
                  "longitude": 12.54321,
                  "price": 499.99,
                  "category": "BEACH",
                  "guideId": 1
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("name", equalTo("Sunny Beach Trip"))
                .body("guideId", equalTo(1));
    }

    @Test
    void updateTrip() {
        String json = """
                {
                  "name": "Mountain Hike Updated",
                  "startTrip": "2025-05-10T08:00:00",
                  "endTrip": "2025-05-10T18:00:00",
                  "latitude": 0,
                  "longitude": 0,
                  "price": 1300,
                  "category": "HIKING",
                  "guideId": 1
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(json)
                .when()
                .put("/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Mountain Hike Updated"))
                .body("price", equalTo(1300F));
    }

    @Test
    void deleteTrip() {
        given()
                .when()
                .delete("/2")
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/2")
                .then()
                .statusCode(404);
    }

    @Test
    void linkGuideToTrip() {
        given()
                .queryParam("category", "BEACH")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].name", equalTo("City Tour"));
    }

    @Test
    void getPackingWeight() {
        given()
                .when()
                .get("/1/packing/weight")
                .then()
                .statusCode(200)
                .body("tripId", equalTo(1))
                .body("totalWeightInGrams", greaterThanOrEqualTo(0));
    }
}