package app.mappers;

import app.dtos.GuideDTO;
import app.entities.Guide;
import app.entities.Trip;

import java.util.ArrayList;
import java.util.List;

public class GuideMapper {
    public static GuideDTO toDto(Guide guide) {
        List<String> tripNames;

        if (guide.getTrips() != null) {
            tripNames = new ArrayList<>();
            for (Trip t : guide.getTrips()) {
                tripNames.add(t.getName());
            }
        } else {
            tripNames = null;
        }


        return new GuideDTO(
                guide.getId(),
                guide.getName(),
                guide.getEmail(),
                guide.getPhone(),
                guide.getYearsOfExperience(),
                tripNames
        );
    }
}
