package app.services;

import app.dtos.PackingItemDTO;
import app.dtos.PackingListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class PackingService {

    // Henter liste af pakkeitems fra API
    public static List<PackingItemDTO> getPackingItems(String category) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://packingapi.cphbusinessapps.dk/packinglist/" + category))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());


            if (response.statusCode() == 200) {
                String json = response.body();
                PackingListDTO packingList = objectMapper.readValue(json, PackingListDTO.class);
                return packingList.getItems();
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Returner tom liste, hvis noget går galt
        return Collections.emptyList();
    }

    // Beregner samlet vægt af pakkeitems
    public static int getTotalWeight(String category) {
        List<PackingItemDTO> items = getPackingItems(category);
        return items.stream()
                .mapToInt(i -> i.getWeightInGrams() * i.getQuantity())
                .sum();
    }
}
