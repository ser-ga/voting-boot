package ru.topjava.voting.repository.restaurant;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;
import ru.topjava.voting.model.Restaurant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;

//http://qaru.site/questions/2181737/spring-data-left-join-fetch-query-returning-null
@Transactional(readOnly = true)
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Restaurant getByIdWithMenuByDate(Long id, LocalDate date) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("added").setParameter("added", date);
        Query query = entityManager.createQuery(
                "SELECT r FROM Restaurant r " +
                        "LEFT JOIN FETCH r.menus m " +
                        "WHERE r.id = (:id)");
        query.setParameter("id", id);
        return (Restaurant) query.getSingleResult();
    }
}
