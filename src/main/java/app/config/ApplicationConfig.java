package app.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import app.routes.Routes;

// starter serveren og fortæller den, hvor endpoints er.

public class ApplicationConfig {

    private static Routes routes = new Routes(); // instans af Routes, som indeholder alle endpoints

    public static void configuration(JavalinConfig config) {
        // → skjuler Javalin-banneret i konsollen ved opstart
        config.showJavalinBanner = false;

        // → gør det muligt at se en oversigt over alle endpoints på http://localhost:7007/api/v1/routes
        config.bundledPlugins.enableRouteOverview("/routes");

        // → base path for alle endpoints. Alle paths vil starte med /api/v1
        config.router.contextPath = "/api/poem";

        // → registrerer alle endpoints fra Routes-klassen
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        // → opretter Javalin-server med ovenstående konfiguration
        var app = Javalin.create(ApplicationConfig::configuration);

        // → starter serveren på valgt port
        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        // → stopper serveren (bruges kun til test eller shutdown)
        app.stop();
    }
}
