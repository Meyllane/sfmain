package io.github.meyllane.sfmain.persistence.database.repositories;

import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.User;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity_;
import jakarta.persistence.EntityGraph;
import org.bukkit.entity.Player;
import org.hibernate.SessionFactory;
import org.hibernate.graph.GraphSemantic;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserEntityRepository extends EntityRepository<UserEntity, User>{
    public UserEntityRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Set<User> getAll() {
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

    @Override
    public User create(User model) {
        return sessionFactory.fromTransaction(session -> {
            UserEntity entity = new UserEntity(model.getMinecraftUUID(), model.getMinecraftName());
            session.persist(entity);
            session.flush();

            return User.fromEntity(entity);
        });
    }

    public User update(User domain) {
        return sessionFactory.fromTransaction(session -> {
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

            session.persist(userEntity);
            session.flush();
            return User.fromEntity(userEntity);
        });
    }

    @Override
    public void delete(User model) {
        sessionFactory.inTransaction(session -> {
            model.getProfiles().stream()
                    .map(profile -> session.getReference(ProfileEntity.class, profile.getId()))
                    .forEach(profile -> profile.setUser(null));

            UserEntity entity = session.getReference(UserEntity.class, model.getId());

            session.remove(entity);
        });
    }
}
