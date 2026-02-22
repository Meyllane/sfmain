package io.github.meyllane.sfmain.persistence.database.repositories;

import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity_;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileMasteryEntity_;
import jakarta.persistence.EntityGraph;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.graph.GraphSemantic;

import java.util.List;

public class ProfileRepository {
    private final SessionFactory sessionFactory;

    public ProfileRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Profile> getAllProfiles() {
        EntityGraph<ProfileEntity> graph = sessionFactory.createEntityGraph(ProfileEntity.class);

        graph.addElementSubgraph(ProfileEntity_.profileTraitEntities);
        graph.addSubgraph(ProfileEntity_.profileMastery);

        return sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM ProfileEntity", ProfileEntity.class)
                    .setEntityGraph(graph, GraphSemantic.FETCH)
                    .getResultList()
                    .stream().map(Profile::fromEntity)
                    .toList();
        });
    }

    public void update(Profile domain) {
        sessionFactory.inTransaction(session -> {
            ProfileEntity entity = session.find(ProfileEntity.class, domain.getId());
            entity.syncFromDomain(domain);
            session.persist(entity);
            try {
                session.flush();
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException ex) {
                    String message = "Le paramètre " + ex.getConstraintName() + " existe déjà avec cette valeur et doit être unique.";
                    throw new SFException(message, ex);
                }
                throw new RuntimeException(e);
            }
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
