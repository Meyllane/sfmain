package io.github.meyllane.sfmain.database;

import io.github.meyllane.sfmain.entities.Profile;
import io.github.meyllane.sfmain.entities.ProfileTrait;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;

import org.hibernate.cfg.AvailableSettings;

public class HibernateUtil {

    public static SessionFactory buildSessionFactory(String url, String user, String password) {

        Map<String, Object> settings = new HashMap<>();

        settings.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
        settings.put(AvailableSettings.JAKARTA_JDBC_URL, url);
        settings.put(AvailableSettings.JAKARTA_JDBC_USER, user);
        settings.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, password);

        settings.put(
                AvailableSettings.CONNECTION_PROVIDER,
                "hikaricp"
        );

        settings.put("hibernate.hikari.maximumPoolSize", "4");
        settings.put("hibernate.hikari.minimumIdle", "1");
        settings.put("hibernate.hikari.idleTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "SFMainPool");

        settings.put(AvailableSettings.SHOW_SQL, false);
        settings.put(AvailableSettings.FORMAT_SQL, false);

        settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(Profile.class)
                .addAnnotatedClass(ProfileTrait.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
