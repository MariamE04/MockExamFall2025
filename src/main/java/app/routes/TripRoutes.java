package app.routes;

import Security.enums.Role;
import app.controllers.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {
    TripController tripController = new TripController();

   public EndpointGroup getRoutes() {
        return () -> {
            get(tripController::getAllTrips);
            post(tripController::createTrip, Role.ADMIN);
            path("/{id}", () -> {
               get(tripController::getById);
               put(tripController::updateTrip, Role.ADMIN);
               delete(tripController::deleteTrip, Role.ADMIN);
            });
            path("/{tripId}/guides/{guideId}", () -> {
                put(tripController::linkGuideToTrip);
            });
            path("/{id}/packing/weight", () ->{
                get(tripController::getPackingWeight);
            });
        };
    }
}