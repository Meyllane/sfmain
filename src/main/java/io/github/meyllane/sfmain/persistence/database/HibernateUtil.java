package io.github.meyllane.sfmain.persistence.database;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jakarta.persistence.Entity;
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

        MetadataSources cfg = new MetadataSources(registry);

        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("io.github.meyllane.sfmain.persistence.database.entities")
                .scan()
        ) {
            for (ClassInfo info : scanResult.getClassesWithAnnotation(Entity.class)) {
                Class<?> cl = info.loadClass();
                cfg.addAnnotatedClass(cl);
            }
        }

        return cfg
                .buildMetadata()
                .buildSessionFactory();
    }
}
