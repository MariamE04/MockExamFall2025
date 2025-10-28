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

        // Her tjekkes guide
        if (trip.getGuide() != null) {
            dto.setGuideId(trip.getGuide().getId());
            dto.setGuideName(trip.getGuide().getName());
        } else {
            dto.setGuideId(0);       // eller null, hvis du vil
            dto.setGuideName(null);
        }

        return dto;
    }
}
