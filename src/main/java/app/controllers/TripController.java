package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.mappers.TripMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripController {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private TripDAO dao = new TripDAO(emf);
    private GuideDAO guideDAO = new GuideDAO(emf);

    public void getAllTrips(Context ctx){
        List<Trip> trips = dao.getAll();
        List<TripDTO> tripDTOS = trips.stream().map(TripMapper::toDTO).toList();
        ctx.status(HttpStatus.OK);
        ctx.json(tripDTOS);
    }

    public void getById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Trip trip = dao.getById(id);

        if(trip != null){
            ctx.status(200);
            ctx.json(TripMapper.toDTO(trip));
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Trip not found");
        }
    }

    public void createTrip(Context ctx){
        TripDTO dto = ctx.bodyAsClass(TripDTO.class);

        Trip trip = new Trip();
        trip.setName(dto.getName());
        trip.setStartTrip(dto.getStartTrip());
        trip.setEndTrip(dto.getEndTrip());
        trip.setLatitude(dto.getLatitude());
        trip.setLongitude(dto.getLongitude());
        trip.setPrice(dto.getPrice());
        trip.setCategory(dto.getCategory());

        if(dto.getGuideId() != 0){
            Guide guide = guideDAO.getById(dto.getGuideId());
            if(guide != null){
                trip.setGuide(guide);
            }
        }

        Trip created = dao.create(trip);
        ctx.status(HttpStatus.CREATED).json(TripMapper.toDTO(created));

    }

    public void updateTrip(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        TripDTO dto = ctx.bodyAsClass(TripDTO.class);

        Trip existing = dao.getById(id);
        if(existing == null){
            ctx.status(HttpStatus.NOT_FOUND).result("Trip not found");
        } else {
            existing.setName(dto.getName());
            existing.setStartTrip(dto.getStartTrip());
            existing.setEndTrip(dto.getEndTrip());
            existing.setLatitude(dto.getLatitude());
            existing.setLongitude(dto.getLongitude());
            existing.setPrice(dto.getPrice());
            existing.setCategory(dto.getCategory());

            Trip updated = dao.update(existing);
            ctx.status(HttpStatus.OK).json(TripMapper.toDTO(updated));

        }

    }

    public void deleteTrip(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean delete = dao.delete(id);

        if(delete){
            ctx.result("Trip with id " + id + " deleted");
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.result("Trip not found");
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    public void linkGuideToTrip(Context ctx){
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        int guideId = Integer.parseInt(ctx.pathParam("guideId"));

        Trip trip = dao.getById(tripId);
        Guide guide = guideDAO.getById(guideId);

        if(trip == null || guide == null){
            ctx.status(HttpStatus.NOT_FOUND).result("Trip or Guide not found");
            return;
        }

        trip.setGuide(guide);
        dao.update(trip);

        ctx.status(HttpStatus.OK).json(TripMapper.toDTO(trip));
    }


}
