package app.mappers;

import app.dtos.TripDTO;
import app.entities.Trip;

public class TripMapper {
    public static TripDTO toDTO(Trip trip){
        int guideId = 0;
        String guideName = null;

        if (trip.getGuide() != null) {
            guideId = trip.getGuide().getId();
            guideName = trip.getGuide().getName();
        }

        TripDTO dto = new TripDTO(
                trip.getId(),
                trip.getName(),
                trip.getStartTrip(),
                trip.getEndTrip(),
                trip.getLatitude(),
                trip.getLongitude(),
                trip.getPrice(),
                trip.getCategory(),
                guideId,
                guideName
        );

        return dto;
    }
}
