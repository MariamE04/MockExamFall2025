package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private TripRoutes tripRoutes = new TripRoutes();
    private GuideRoutes guideRoutes = new GuideRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            // root endpoint
            get("/", ctx -> ctx.result("Welcome to Trip Planning API!"));

            path("/trips", tripRoutes.getRoutes());
            path("/guides", guideRoutes.getRoutes());

        };
    }
}
