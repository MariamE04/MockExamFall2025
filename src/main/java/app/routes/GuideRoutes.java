package app.routes;

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
            post(guideController::createGuide);
            path("/{id}", () -> {
               get(guideController::getGuideById);
               put(guideController::updateGuide);
               delete(guideController::deleteGuide);
            });
            get("/{id}/trips", guideController::getTripsForGuide);
        };
    }

}
