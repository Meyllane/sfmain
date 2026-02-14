package io.github.meyllane.sfmain;

import io.github.meyllane.sfmain.database.DatabaseManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;

import java.io.File;
import java.util.logging.Level;

public final class SFMain extends JavaPlugin {
    private static YamlConfiguration databaseConfig;
    public static DatabaseManager dbManager;

    @Override
    public void onEnable() {
        this.saveConfigFiles();
        this.loadConfigFiles();

        try {
            dbManager = new DatabaseManager(databaseConfig);
        } catch (Exception e) {
            this.getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        if (dbManager.getConn() == null) {
            this.getLogger().log(Level.SEVERE, "Can't connect to the database. The plugin will be disabled");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.getLogger().log(Level.INFO, "Successfully connected to the database.");

        //Migration

        this.getLogger().log(Level.INFO, "Starting handling of Flyway's migrations.");
        this.handleMigration();
        this.getLogger().log(Level.INFO, "Finished handling the migrations.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void saveConfigFiles() {
        File databaseFile = new File(this.getDataFolder(), "database.yml");
        if (!databaseFile.exists()) {
            saveResource("database.yml", false);
        }
    }

    private void loadConfigFiles() {
        databaseConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "database.yml"));
    }

    private void handleMigration() {
        Flyway flyway = Flyway.configure(this.getClassLoader())
                .dataSource(dbManager.getUrl(), dbManager.getUser(), dbManager.getPassword())
                .locations("classpath:db/migrations")
                .load();

        flyway.migrate();
    }
}
