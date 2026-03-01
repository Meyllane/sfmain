package io.github.meyllane.sfmain.persistence.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.meyllane.sfmain.SFMain;
import org.flywaydb.core.Flyway;

import java.util.logging.Level;

import static io.github.meyllane.sfmain.SFMain.dbManager;

public class FlywayMigrator {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    public static void migrate(HikariDataSource dataSource) {
        plugin.getLogger().log(Level.INFO, "Starting handling of Flyway's migrations.");

        Flyway flyway = Flyway.configure(plugin.getClass().getClassLoader())
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
        plugin.getLogger().log(Level.INFO, "Finished handling the migrations.");
    }
}
