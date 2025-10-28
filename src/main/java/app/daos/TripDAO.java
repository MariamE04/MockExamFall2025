package app.daos;

import app.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripDAO implements IDAO<Trip, Integer>{

    private final EntityManagerFactory emf;

    public TripDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Trip create(Trip trip) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
        }
        return trip;
    }

    @Override
    public Trip getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            return em.find(Trip.class,id);
        }
    }

    @Override
    public Trip update(Trip trip) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(trip);
            em.getTransaction().commit();
        }
        return trip;
    }

    @Override
    public List<Trip> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            List<Trip> trips = em.createQuery("SELECT t FROM Trip t",Trip.class)
                    .getResultList();
            return trips;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Trip toDelete = em.find(Trip.class,id);
            if(toDelete != null){
                em.remove(toDelete);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        }
    }
}
