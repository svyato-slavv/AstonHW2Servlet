package util;

import model.City;
import model.Weather;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateCfg {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.addAnnotatedClass(Weather.class);
                configuration.addAnnotatedClass(City.class);
                sessionFactory = configuration.buildSessionFactory();
                return sessionFactory;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

}
