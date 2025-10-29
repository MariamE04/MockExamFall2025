package app.routes;

import Security.enums.Role;
import app.controllers.GuideController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.put;

public class GuideRoutes {
    GuideController guideController = new GuideController();

    public EndpointGroup getRoutes() {
        return () -> {
            get(guideController::getAllGuides);
            post(guideController::createGuide, Role.ADMIN);
            get("/totalprice", guideController::getTotalTripPrice);
            path("/{id}", () -> {
               get(guideController::getGuideById);
               put(guideController::updateGuide, Role.ADMIN);
               delete(guideController::deleteGuide, Role.ADMIN);
            });
            get("/{id}/trips", guideController::getTripsForGuide);
        };
    }

}
