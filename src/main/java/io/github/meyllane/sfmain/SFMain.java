package io.github.meyllane.sfmain;

import io.github.meyllane.sfmain.commands.profile.ProfileCommand;
import io.github.meyllane.sfmain.persistence.database.HibernateUtil;
import io.github.meyllane.sfmain.events.PlayerJoinEventListener;
import io.github.meyllane.sfmain.elements.MasterySpecializationElement;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.application.loaders.MasteryLoader;
import io.github.meyllane.sfmain.application.loaders.SpeciesLoader;
import io.github.meyllane.sfmain.elements.TraitElement;
import io.github.meyllane.sfmain.application.loaders.TraitLoader;
import io.github.meyllane.sfmain.application.loaders.DatabaseLoader;
import io.github.meyllane.sfmain.persistence.database.FlywayMigrator;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.application.registries.ProfileRegistry;
import io.github.meyllane.sfmain.application.registries.UserRegistry;
import io.github.meyllane.sfmain.persistence.database.repositories.ProfileRepository;
import io.github.meyllane.sfmain.persistence.database.repositories.UserRepository;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.application.services.UserService;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.io.File;
import java.util.logging.Level;

public final class SFMain extends JavaPlugin {
    private YamlConfiguration databaseConfig;
    private YamlConfiguration traitsConfig;
    private YamlConfiguration speciesConfig;
    private YamlConfiguration masteriesConfig;

    public static DatabaseLoader dbManager;
    private final String[] configFilesPath = {"database.yml", "traits.yml", "species.yml", "masteries.yml"};

    public static final ElementRegistry<TraitElement> traitsRegistry = new ElementRegistry<>();
    public static final ElementRegistry<SpeciesElement> speciesRegistry = new ElementRegistry<>();
    public static final ElementRegistry<MasteryElement> masteriesRegistry = new ElementRegistry<>();
    public static final ElementRegistry<MasterySpecializationElement> masterySpecializationsRegistry = new ElementRegistry<>();

    public static UserService userService;

    public static ProfileService profileService;
    public static ProfileRegistry profileRegistry;

    public static SessionFactory sessionFactory;

    @Override
    public void onEnable() {
        this.saveConfigFiles(configFilesPath);
        this.loadConfigFiles();

        try {
            dbManager = new DatabaseLoader(databaseConfig);
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

        UserRepository userRepository = new UserRepository(sessionFactory);
        UserRegistry userRegistry = new UserRegistry();
        userService = new UserService(userRepository, userRegistry);
        userService.loadAllUsers();

        ProfileRepository profileRepository = new ProfileRepository(sessionFactory);
        profileRegistry = new ProfileRegistry();
        profileService = new ProfileService(profileRepository, profileRegistry);
        profileService.loadAllProfiles();

        this.getLogger().log(Level.INFO, "Loaded " + profileRegistry.values().size() + " Profiles from the database.");

        //Register listeners

        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(userService, this), this);

        //Command registration
        new ProfileCommand().register();
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
