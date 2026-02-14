package io.github.meyllane.sfmain.database;

import io.github.meyllane.sfmain.SFMain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import static io.github.meyllane.sfmain.SFMain.dbManager;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;
    private Connection conn;
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    public DatabaseManager(YamlConfiguration databaseConfig) throws Exception {
        String host = databaseConfig.getString("host");
        String user = databaseConfig.getString("user");
        String password = databaseConfig.getString("password");
        String database = databaseConfig.getString("database_name");

        if (host == null || host.isEmpty()) throw new Exception("host parameter in database.yml is null or empty.");

        if (database == null || database.isEmpty()) throw new Exception("database_name parameter in database.yml is null or empty.");

        int port = databaseConfig.getInt("port");

        if (port == 0) throw new Exception("port parameter in database.yml is invalid. It has to be a non-null positive integer");

        if (user == null || user.isEmpty()) throw new Exception("user parameter in database.yml is null or empty.");

        if (password == null || password.isEmpty()) throw new Exception("password parameter in database.yml is null or empty.");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.user = user;
        this.password = password;
    }

    public Connection getConn() {
        if (this.conn == null) {
           try {
               this.conn = DriverManager.getConnection(this.url, this.user, this.password);
           } catch (SQLException e) {
               throw new RuntimeException(e);
           }
        }

        return this.conn;
    }

    public static void init(YamlConfiguration databaseConfig) {
        try {
            dbManager = new DatabaseManager(databaseConfig);
        } catch (Exception e) {
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            throw new RuntimeException(e);
        }

        if (dbManager.getConn() == null) {
            plugin.getLogger().log(Level.SEVERE, "Can't connect to the database. The plugin will be disabled");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        plugin.getLogger().log(Level.INFO, "Successfully connected to the database.");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
