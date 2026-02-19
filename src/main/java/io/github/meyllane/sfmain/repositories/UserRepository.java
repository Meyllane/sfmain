package io.github.meyllane.sfmain.repositories;

import io.github.meyllane.sfmain.database.entities.User;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class UserRepository {
    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User getUser(UUID uuid) {
        return sessionFactory.fromTransaction(session -> session.bySimpleNaturalId(User.class).load(uuid));
    }

    public User getUser(Player player) {
        return sessionFactory.fromTransaction(session -> session.bySimpleNaturalId(User.class).load(player.getUniqueId()));
    }

    public User createUser(Player player) {
        User user = new User();
        user.setMinecraftUUID(player.getUniqueId());
        String minecraftName = PlainTextComponentSerializer.plainText().serialize(player.displayName());
        user.setMinecraftName(minecraftName);
        sessionFactory.inTransaction(session -> {
            session.persist(user);
            session.flush();
        });
        return user;
    }
}
