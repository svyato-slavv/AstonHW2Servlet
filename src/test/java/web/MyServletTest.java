package web;

import model.City;
import model.Weather;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import util.HibernateCfg;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MyServletTest {
    private static SessionFactory sessionFactory;
    private Session session;

    @BeforeAll
    public static void setup() {
        sessionFactory = HibernateCfg.getSessionFactory();
        System.out.println("SessionFactory created");
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) sessionFactory.close();
        System.out.println("SessionFactory destroyed");
    }

    @BeforeEach
    public void openSession() {
        session = sessionFactory.openSession();
        System.out.println("Session created");
    }

    @AfterEach
    public void closeSession() {
        if (session != null) session.close();
        System.out.println("Session closed\n");
    }


    @Test
    public void testCreate() {
        session.beginTransaction();
        Weather weather = new Weather(LocalDate.now(), 5, 2);
        City city = new City("test");
        session.save(city);
        weather.setCity(city);
        Integer savedId = (Integer) session.save(weather);
        assertTrue(savedId > 0);
        assertEquals("test", weather.getCity().getCityName());
    }


}


