package app.entities;

import app.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder

@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private LocalDateTime startTrip;
    private LocalDateTime endTrip;
    private double latitude;
    private double longitude;
    private double price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    private Guide guide;
}
