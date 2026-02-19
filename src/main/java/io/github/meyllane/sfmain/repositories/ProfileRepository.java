package io.github.meyllane.sfmain.repositories;

import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import org.hibernate.SessionFactory;

public class ProfileRepository {
    private final SessionFactory sessionFactory;
    private final NamedElementRegistry<SpeciesElement> speciesRegistry;

    public ProfileRepository(SessionFactory sessionFactory, NamedElementRegistry<SpeciesElement> speciesRegistry) {
        this.sessionFactory = sessionFactory;
        this.speciesRegistry = speciesRegistry;
    }

    public Profile getProfile(String profileName) {
        return sessionFactory.fromTransaction(session -> session.bySimpleNaturalId(Profile.class).load(profileName));
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
}
