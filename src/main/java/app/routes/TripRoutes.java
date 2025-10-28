package app.routes;

import app.controllers.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {
    TripController tripController = new TripController();

   public EndpointGroup getRoutes() {
        return () -> {
            get(tripController::getAllTrips);
            post(tripController::createTrip);
            path("/{id}", () -> {
               get(tripController::getById);
               put(tripController::updateTrip);
               delete(tripController::deleteTrip);
            });
            path("/{tripId}/guides/{guideId}", () -> {
                put(tripController::linkGuideToTrip);
            });
        };
    }
}