package app.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PackingListDTO {
    private List<PackingItemDTO> items;
}
