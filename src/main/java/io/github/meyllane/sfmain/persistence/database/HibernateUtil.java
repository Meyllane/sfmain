package io.github.meyllane.sfmain.persistence.database;

import com.zaxxer.hikari.HikariDataSource;
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

    public static SessionFactory buildSessionFactory(HikariDataSource dataSource) {
        dataSource.setMaximumPoolSize(4);
        dataSource.setMinimumIdle(1);
        dataSource.setIdleTimeout(15000);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.JAKARTA_JTA_DATASOURCE, dataSource)
                .applySetting(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect")
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
