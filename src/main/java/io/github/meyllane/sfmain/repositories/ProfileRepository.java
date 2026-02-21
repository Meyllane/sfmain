package io.github.meyllane.sfmain.repositories;

import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.database.entities.ProfileTrait;
import io.github.meyllane.sfmain.database.entities.Profile_;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import jakarta.persistence.EntityGraph;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.graph.GraphSemantic;

public class ProfileRepository {
    private final SessionFactory sessionFactory;
    private final NamedElementRegistry<SpeciesElement> speciesRegistry;

    public ProfileRepository(SessionFactory sessionFactory, NamedElementRegistry<SpeciesElement> speciesRegistry) {
        this.sessionFactory = sessionFactory;
        this.speciesRegistry = speciesRegistry;
    }

    public Profile getProfile(String profileName) {
        EntityGraph<Profile> graph = sessionFactory.createEntityGraph(Profile.class);

        graph.addElementSubgraph(Profile_.profileTraits);

        return sessionFactory.fromTransaction(session -> {
            return session.createSelectionQuery("FROM Profile p WHERE p.name = :name", Profile.class)
                    .setParameter("name", profileName)
                    .setEntityGraph(graph, GraphSemantic.FETCH)
                    .getSingleResult();
        });
    }

    public Profile create(String name) {
        Profile profile = new Profile();
        profile.setName(name);
        profile.setSpeciesElement(speciesRegistry.getById(1));
        sessionFactory.inTransaction(session -> {
            session.persist(profile);
            session.flush();
        });

        return profile;
    }

    public boolean exists(String name) {
        return sessionFactory.fromTransaction(session -> {
            Long count = session.createQuery("SELECT COUNT(*) FROM Profile WHERE name=:name", Long.class)
                    .setParameter("name", name)
                    .getSingleResult();

            return count > 0;
        });
    }

    public String[] getProfileNames() {
        return sessionFactory.fromTransaction(session -> {
            return session.createQuery("SELECT name FROM Profile", String.class)
                    .getResultList().toArray(new String[0]);
        });
    }

    public void update(Profile profile) {
        sessionFactory.inTransaction(session -> {
            session.merge(profile);
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
}
