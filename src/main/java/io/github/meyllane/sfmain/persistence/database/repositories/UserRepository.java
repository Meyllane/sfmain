package io.github.meyllane.sfmain.persistence.database.repositories;

import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity_;
import jakarta.persistence.EntityGraph;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.graph.GraphSemantic;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Set<User> getAllUsers() {
        return sessionFactory.fromTransaction(session -> {
            EntityGraph<UserEntity> graph = session.createEntityGraph(UserEntity.class);

            graph.addSubgraph(UserEntity_.ACTIVE_PROFILE);
            graph.addElementSubgraph(UserEntity_.PROFILES);

           return session.createSelectionQuery("FROM UserEntity", UserEntity.class)
                   .setEntityGraph(graph, GraphSemantic.FETCH)
                   .getResultList()
                   .stream().map(User::fromEntity)
                   .collect(Collectors.toSet());
        });
    }

    public User createUser(UUID minecraftUUID, String minecraftName) {
        return sessionFactory.fromTransaction(session -> {
            UserEntity entity = new UserEntity(minecraftUUID, minecraftName);
            session.persist(entity);
            session.flush();

            return User.fromEntity(entity);
        });
    }

    public void update(User domain) {
        sessionFactory.inTransaction(session -> {
            EntityGraph<UserEntity> graph = session.createEntityGraph(UserEntity.class);
            graph.addSubgraph(UserEntity_.ACTIVE_PROFILE);
            graph.addElementSubgraph(UserEntity_.PROFILES);

            UserEntity userEntity = session.createSelectionQuery("FROM UserEntity WHERE minecraftUUID=:uuid", UserEntity.class)
                    .setEntityGraph(graph, GraphSemantic.FETCH)
                    .setParameter("uuid", domain.getMinecraftUUID())
                    .getSingleResult();

            //userEntity.syncFromDomain(domain);

            if (domain.getActiveProfile() != null) {
                ProfileEntity current = userEntity.getActiveProfile();
                if (current == null || !current.getId().equals(domain.getActiveProfile().getId())) {
                    userEntity.setActiveProfile(session.getReference(ProfileEntity.class, domain.getActiveProfile().getId()));
                }
            }

            Set<Long> domainIDs = domain.getProfiles().stream()
                    .map(Profile::getId)
                    .collect(Collectors.toSet());

            Set<Long> presentIDs = userEntity.getProfiles().stream()
                    .map(ProfileEntity::getId)
                    .collect(Collectors.toSet());

            userEntity.getProfiles().stream()
                    .filter(ent -> !domainIDs.contains(ent.getId()))
                    .forEach(userEntity::removeProfile);

            domainIDs.stream()
                    .filter(id -> !presentIDs.contains(id))
                    .map(id -> session.getReference(ProfileEntity.class, id))
                    .forEach(userEntity::addProfile);
        });
    }
}
