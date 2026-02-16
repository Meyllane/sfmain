package io.github.meyllane.sfmain.database;

import io.github.meyllane.sfmain.SFMain;
import org.flywaydb.core.Flyway;

import java.util.logging.Level;

import static io.github.meyllane.sfmain.SFMain.dbManager;

public class FlywayMigrator {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    public static void migrate() {
        plugin.getLogger().log(Level.INFO, "Starting handling of Flyway's migrations.");

        Flyway flyway = Flyway.configure(plugin.getClass().getClassLoader())
                .dataSource(dbManager.getUrl(), dbManager.getUser(), dbManager.getPassword())
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
        plugin.getLogger().log(Level.INFO, "Finished handling the migrations.");
    }
}
