package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.mappers.GuideMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideController {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private GuideDAO guideDAO = new GuideDAO(emf);
    private TripDAO tripDAO = new TripDAO(emf);

    // GET /guides
    public void getAllGuides(Context ctx){
        List<Guide> guides = guideDAO.getAll();
        List<GuideDTO> guideDTOS = guides.stream().map(GuideMapper::toDto).toList();
        ctx.status(HttpStatus.OK).json(guideDTOS);
    }

    // GET /guides/{id}
    public void getGuideById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Guide guide = guideDAO.getById(id);

        if(guide != null){
            ctx.status(HttpStatus.OK).json(GuideMapper.toDto(guide));
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Guide not found");
        }
    }

    // POST /guides
    public void createGuide(Context ctx){
        GuideDTO dto = ctx.bodyAsClass(GuideDTO.class);

        Guide guide = new Guide();
        guide.setName(dto.getName());
        guide.setEmail(dto.getEmail());
        guide.setPhone(dto.getPhone());
        guide.setYearsOfExperience(dto.getYearsOfExperience());

        Guide created = guideDAO.create(guide);
        ctx.status(HttpStatus.CREATED).json(GuideMapper.toDto(created));

    }

    // PUT /guides/{id}
    public void updateGuide(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        GuideDTO dto = ctx.bodyAsClass(GuideDTO.class);

        Guide existing = guideDAO.getById(id);
        if(existing == null){
            ctx.status(HttpStatus.NOT_FOUND).result("Guide not found");
            return;
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setYearsOfExperience(dto.getYearsOfExperience());

        Guide updated = guideDAO.update(existing);
        ctx.status(HttpStatus.OK).json(GuideMapper.toDto(updated));
    }

    // DELETE /guides/{id}
    public void deleteGuide(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = guideDAO.delete(id);

        if(deleted){
            ctx.status(HttpStatus.NO_CONTENT).result("Guide deleted");
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Guide not found");
        }
    }

    // GET /guides/{id}/trips
    // Liste af trips for en guide
    public void getTripsForGuide(Context ctx){
        int guideId = Integer.parseInt(ctx.pathParam("id"));
        Guide guide = guideDAO.getById(guideId);

        if(guide == null){
            ctx.status(HttpStatus.NOT_FOUND).result("Guide not found");
            return;
        }

        List<Trip> trips = guide.getTrips();
        ctx.status(HttpStatus.OK).json(trips.stream().map(trip -> trip.getName()).toList());
    }

    public void getTotalTripPrice(Context ctx) {
        List<Trip> trips = tripDAO.getAll();

        Map<Integer, Double> totalPricePerGuide = trips.stream()
                .filter((t -> t.getGuide() != null))  // ignorer trips uden guide
                .collect(Collectors.groupingBy(t -> t.getGuide().getId(),
                        Collectors.summingDouble(Trip::getPrice)
                ));

        ctx.json(totalPricePerGuide);
    }

}
