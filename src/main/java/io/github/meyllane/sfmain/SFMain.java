package io.github.meyllane.sfmain;

import io.github.meyllane.sfmain.database.HibernateUtil;
import io.github.meyllane.sfmain.entities.Profile;
import io.github.meyllane.sfmain.entities.ProfileTrait;
import io.github.meyllane.sfmain.named_elements.MasterySpecializationElement;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.named_elements.MasteryElement;
import io.github.meyllane.sfmain.loaders.MasteryLoader;
import io.github.meyllane.sfmain.loaders.SpeciesLoader;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import io.github.meyllane.sfmain.loaders.TraitLoader;
import io.github.meyllane.sfmain.database.DatabaseManager;
import io.github.meyllane.sfmain.database.FlywayMigrator;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.io.File;

public final class SFMain extends JavaPlugin {
    private YamlConfiguration databaseConfig;
    private YamlConfiguration traitsConfig;
    private YamlConfiguration speciesConfig;
    private YamlConfiguration masteriesConfig;

    public static DatabaseManager dbManager;
    private final String[] configFilesPath = {"database.yml", "traits.yml", "species.yml", "masteries.yml"};

    public static final NamedElementRegistry<TraitElement> traitsRegistry = new NamedElementRegistry<>();
    public static final NamedElementRegistry<SpeciesElement> speciesRegistry = new NamedElementRegistry<>();
    public static final NamedElementRegistry<MasteryElement> masteriesRegistry = new NamedElementRegistry<>();
    public static final NamedElementRegistry<MasterySpecializationElement> masterySpecializationsRegistry = new NamedElementRegistry<>();

    public static SessionFactory sessionFactory;

    @Override
    public void onEnable() {
        this.saveConfigFiles(configFilesPath);
        this.loadConfigFiles();

        try {
            dbManager = new DatabaseManager(databaseConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        FlywayMigrator.migrate();

        sessionFactory = HibernateUtil.buildSessionFactory(
                dbManager.getUrl(),
                dbManager.getUser(),
                dbManager.getPassword()
        );

        //Loading from files
        TraitLoader.load(traitsConfig);
        SpeciesLoader.load(speciesConfig);
        MasteryLoader.load(masteriesConfig);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void saveConfigFiles(String[] configFilesPath) {
        for (String f : configFilesPath) {
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
        masteriesConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "masteries.yml"));
    }
}
