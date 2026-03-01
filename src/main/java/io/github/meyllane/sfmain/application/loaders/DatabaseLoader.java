package io.github.meyllane.sfmain.application.loaders;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.YamlConfiguration;

public class DatabaseLoader {
    public static HikariDataSource getDataSource(YamlConfiguration databaseConfig) throws IllegalStateException {
        HikariConfig config = new HikariConfig();

        String host = databaseConfig.getString("host");
        String user = databaseConfig.getString("user");
        String password = databaseConfig.getString("password");
        String database = databaseConfig.getString("database_name");

        if (host == null || host.isEmpty()) throw new IllegalStateException("host parameter in database.yml is null or empty.");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        if (user == null || user.isEmpty()) throw new IllegalStateException("user parameter in database.yml is null or empty.");
        config.setUsername(user);

        if (password == null || password.isEmpty()) throw new IllegalStateException("password parameter in database.yml is null or empty.");
        config.setPassword(password);

        if (database == null || database.isEmpty()) throw new IllegalStateException("database_name parameter in database.yml is null or empty.");

        int port = databaseConfig.getInt("port");

        if (port == 0) throw new IllegalStateException("port parameter in database.yml is invalid. It has to be a non-null positive integer");

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

        return new HikariDataSource(config);
    }

    public static HikariDataSource getDevDataSource() {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:database");

        return new HikariDataSource(config);
    }
}
