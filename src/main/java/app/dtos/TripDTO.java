package app.dtos;

import app.entities.Guide;
import app.enums.Category;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class TripDTO {
    private int id;
    private String name;
    private LocalDateTime startTrip;
    private LocalDateTime endTrip;
    private double latitude;
    private double longitude;
    private double price;
    private Category category;
    private int guideId;
    private String guideName;

    //Liste af packing items
    private List<PackingItemDTO> packingItems;

}
