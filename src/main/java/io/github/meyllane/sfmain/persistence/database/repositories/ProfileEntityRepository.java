package io.github.meyllane.sfmain.persistence.database.repositories;

import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileMastery;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity_;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileMasteryEntity_;
import jakarta.persistence.EntityGraph;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.graph.GraphSemantic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileEntityRepository extends EntityRepository<ProfileEntity, Profile> {
    public ProfileEntityRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Set<Profile> getAll() {
        EntityGraph<ProfileEntity> graph = sessionFactory.createEntityGraph(ProfileEntity.class);

        graph.addElementSubgraph(ProfileEntity_.profileTraitEntities);
        graph.addSubgraph(ProfileEntity_.profileMastery).addElementSubgraph(ProfileMasteryEntity_.profileMasterySpeEntities);

        return sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM ProfileEntity", ProfileEntity.class)
                    .setEntityGraph(graph, GraphSemantic.FETCH)
                    .getResultList()
                    .stream().map(Profile::fromEntity)
                    .collect(Collectors.toSet());
        });
    }

    public Profile update(Profile domain) {
        return sessionFactory.fromTransaction(session -> {
            EntityGraph<ProfileEntity> graph = session.createEntityGraph(ProfileEntity.class);
            graph.addElementSubgraph(ProfileEntity_.profileTraitEntities);
            graph.addSubgraph(ProfileEntity_.profileMastery)
                    .addElementSubgraph(ProfileMasteryEntity_.profileMasterySpeEntities);

            ProfileEntity entity = session.find(
                    ProfileEntity.class,
                    domain.getId(),
                    Map.of(GraphSemantic.FETCH.getJakartaHintName(), graph)
            );

            entity.syncFromDomain(domain);

            try {
                session.flush();
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException ex) {
                    String message = "Le paramètre " + ex.getConstraintName() + " existe déjà avec cette valeur et doit être unique.";
                    throw new SFException(message, ex);
                }
                throw new RuntimeException(e);
            }

            return Profile.fromEntity(entity);
        });
    }

    @Override
    public void delete(Profile model) {
        this.sessionFactory.inTransaction(session -> {
            ProfileEntity entity = session.getReference(ProfileEntity.class, model.getId());

            session.remove(entity);
        });
    }

    public Profile create(Profile domain) {
        return sessionFactory.fromTransaction(session -> {
            ProfileEntity entity = new ProfileEntity();
            entity.syncFromDomain(domain);
            session.persist(entity);
            session.flush();

            return Profile.fromEntity(entity);
        });
    }
}
