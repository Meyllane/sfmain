package io.github.meyllane.sfmain.application.loaders;

import org.bukkit.configuration.file.YamlConfiguration;

public class DatabaseLoader {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseLoader(YamlConfiguration databaseConfig) throws Exception {
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
