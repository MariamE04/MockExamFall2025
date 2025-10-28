package app.daos;

import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideDAO implements IDAO<Guide, Integer>{

    private final EntityManagerFactory emf;

    public GuideDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Guide create(Guide guide) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
        }
        return guide;
    }

    @Override
    public Guide getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            return em.find(Guide.class,id);
        }
    }

    @Override
    public Guide update(Guide guide) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.merge(guide);
            em.getTransaction().commit();
        }
        return guide;
    }

    @Override
    public List<Guide> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            List<Guide> guides = em.createQuery("SELECT g FROM Guide g",Guide.class)
                    .getResultList();
            return guides;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Guide toDelete = em.find(Guide.class,id);
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
