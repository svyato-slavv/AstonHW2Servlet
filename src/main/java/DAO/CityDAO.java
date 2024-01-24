package DAO;

import model.City;
import org.hibernate.Session;
import util.HibernateCfg;

import java.util.Optional;

public class CityDAO {
    public City getCityByName(String name) {
        Optional<City> city;
        try (Session session = HibernateCfg.getSessionFactory().openSession()) {
            session.beginTransaction();
            city = session.createQuery("from City where cityName=:name", City.class)
                    .setParameter("name", name)
                    .getResultStream().findAny();
            session.getTransaction().commit();
        }
        return city.orElse(null);
    }

}
