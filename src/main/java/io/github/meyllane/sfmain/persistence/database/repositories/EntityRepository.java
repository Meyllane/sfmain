package io.github.meyllane.sfmain.persistence.database.repositories;

import org.hibernate.SessionFactory;

import java.util.Set;

public abstract class EntityRepository<E, M> {
    protected SessionFactory sessionFactory;

    public EntityRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public abstract Set<M> getAll();

    public abstract M create(M model);

    public abstract M update(M model);

    public abstract void delete(M model);
}
