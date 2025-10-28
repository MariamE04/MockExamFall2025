package app.dtos;

import app.entities.Trip;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class GuideDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int yearsOfExperience;
    private List<String> tripNames;
}
