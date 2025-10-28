package app.mappers;

import app.dtos.TripDTO;
import app.entities.Trip;
import app.services.PackingService;

public class TripMapper {
    public static TripDTO toDTO(Trip trip){
        int guideId = 0;
        String guideName = null;

        if (trip.getGuide() != null) {
            guideId = trip.getGuide().getId();
            guideName = trip.getGuide().getName();
        }

        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        dto.setName(trip.getName());
        dto.setStartTrip(trip.getStartTrip());
        dto.setEndTrip(trip.getEndTrip());
        dto.setLatitude(trip.getLatitude());
        dto.setLongitude(trip.getLongitude());
        dto.setPrice(trip.getPrice());
        dto.setCategory(trip.getCategory());
        dto.setGuideId(guideId);
        dto.setGuideName(guideName);
        dto.setPackingItems(PackingService.getPackingItems(trip.getCategory().name().toLowerCase()));

        return dto;
    }
}
