package io.github.meyllane.sfmain;

import io.github.meyllane.sfmain.character.species.Species;
import io.github.meyllane.sfmain.character.species.SpeciesLoader;
import io.github.meyllane.sfmain.character.traits.Trait;
import io.github.meyllane.sfmain.character.traits.TraitLoader;
import io.github.meyllane.sfmain.database.DatabaseManager;
import io.github.meyllane.sfmain.database.FlywayMigrator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public final class SFMain extends JavaPlugin {
    private YamlConfiguration databaseConfig;
    private YamlConfiguration traitsConfig;
    private YamlConfiguration speciesConfig;
    public static DatabaseManager dbManager;
    private final String[] configFilesPath = {"database.yml", "traits.yml", "species.yml"};

    public static HashMap<String, Trait> traitsMap;
    public static HashMap<String, Species> speciesMap;

    @Override
    public void onEnable() {
        this.saveConfigFiles();
        this.loadConfigFiles();

        DatabaseManager.init(databaseConfig);
        FlywayMigrator.migrate();

        //Loading from files
        TraitLoader.load(traitsConfig);
        SpeciesLoader.load(speciesConfig);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void saveConfigFiles() {
        for (String f : this.configFilesPath) {
            File file = new File(this.getDataFolder(), f);
            if (!file.exists()) {
                saveResource(f, false);
            }
        }
    }

    private void loadConfigFiles() {
        databaseConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "database.yml"));
        traitsConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "traits.yml"));
        speciesConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "species.yml"));
    }
}
