package DAO;

import model.City;
import model.Weather;
import org.hibernate.Session;
import util.HibernateCfg;

import java.time.LocalDate;
import java.util.Optional;

public class WeatherDAO {
    private final CityDAO cityDAO = new CityDAO();

    public void saveWeather(Weather weatherEntity, String cityRequest) {
        Weather weather = new Weather(LocalDate.now(), weatherEntity.getMaxTemperature(), weatherEntity.getMinTemperature());

        try (Session session = HibernateCfg.getSessionFactory().openSession()) {
            session.beginTransaction();
            City city = cityDAO.getCityByName(cityRequest);
            if (city == null) {
                City cityNew = new City(cityRequest);
                session.save(cityNew);
                weather.setCity(cityNew);
            } else {
                weather.setCity(city);
            }

            session.save(weather);

            session.getTransaction().commit();
        }
    }

    public Weather getByDateAndCity(LocalDate date, String cityRequest) {
        Optional<Weather> weather;
        try (Session session = HibernateCfg.getSessionFactory().openSession()) {
            session.beginTransaction();
            City city = cityDAO.getCityByName(cityRequest);
            weather = session.createQuery("from Weather where city=:city and date=:date", Weather.class)
                    .setParameter("date", date)
                    .setParameter("city", city)
                    .getResultStream().findAny();
            session.getTransaction().commit();
        }
        return weather.orElse(null);
    }
}
