package io.github.meyllane.sfmain;

import com.zaxxer.hikari.HikariDataSource;
import io.github.meyllane.sfmain.application.registries.element.MasteryElementRegistry;
import io.github.meyllane.sfmain.application.registries.element.MasterySpeElementRegistry;
import io.github.meyllane.sfmain.application.registries.element.SpeciesElementRegistry;
import io.github.meyllane.sfmain.application.registries.element.TraitElementRegistry;
import io.github.meyllane.sfmain.application.registries.model.ProfileRegistry;
import io.github.meyllane.sfmain.application.registries.model.UserRegistry;
import io.github.meyllane.sfmain.commands.profile.ProfileCommand;
import io.github.meyllane.sfmain.commands.user.UserCommand;
import io.github.meyllane.sfmain.persistence.database.HibernateUtil;
import io.github.meyllane.sfmain.events.PlayerJoinEventListener;
import io.github.meyllane.sfmain.application.loaders.DatabaseLoader;
import io.github.meyllane.sfmain.persistence.database.FlywayMigrator;
import io.github.meyllane.sfmain.persistence.database.repositories.ProfileEntityRepository;
import io.github.meyllane.sfmain.persistence.database.repositories.UserEntityRepository;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;

public final class SFMain extends JavaPlugin {
    public static final boolean IS_DEV = SFMain.resolveEnvironment();

    private YamlConfiguration databaseConfig;
    private YamlConfiguration traitsConfig;
    private YamlConfiguration speciesConfig;
    private YamlConfiguration masteriesConfig;

    public static DatabaseLoader dbManager;
    private final String[] configFilesPath = {"database.yml", "traits.yml", "species.yml", "masteries.yml"};

    public static final TraitElementRegistry traitsElementRegistry = new TraitElementRegistry();
    public static final SpeciesElementRegistry speciesElementRegistry = new SpeciesElementRegistry();
    public static final MasteryElementRegistry masteriesElementRegistry = new MasteryElementRegistry();
    public static final MasterySpeElementRegistry masterySpeElementRegistry = new MasterySpeElementRegistry();

    public static ProfileRegistry profileRegistry;
    public static ProfileEntityRepository profileEntityRepository;

    public static UserRegistry userRegistry;
    public static UserEntityRepository userEntityRepository;

    public static SessionFactory sessionFactory;

    @Override
    public void onEnable() {
        this.saveConfigFiles(configFilesPath);
        this.loadConfigFiles();

        HikariDataSource datasource;

        datasource = DatabaseLoader.getDataSource(databaseConfig);

        FlywayMigrator.migrate(datasource);
        sessionFactory = HibernateUtil.buildSessionFactory(datasource);

        //Loading from files
        traitsElementRegistry.load(traitsConfig);
        speciesElementRegistry.load(speciesConfig);
        masteriesElementRegistry.load(masteriesConfig);
        masterySpeElementRegistry.load(masteriesConfig);

        profileEntityRepository = new ProfileEntityRepository(sessionFactory);
        profileRegistry = new ProfileRegistry();

        profileEntityRepository.getAll().forEach(profile -> profileRegistry.register(profile));

        userEntityRepository = new UserEntityRepository(sessionFactory);
        userRegistry = new UserRegistry();
        userEntityRepository.getAll().forEach(user -> userRegistry.register(user));

        //Register listeners

        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);

        //Command registration
        UserCommand.register();
        ProfileCommand.register();
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

    private static boolean resolveEnvironment() {
        String env = System.getProperty("env", "prod");
        return env.equalsIgnoreCase("dev");
    }
}
