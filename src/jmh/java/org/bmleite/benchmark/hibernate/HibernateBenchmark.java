package org.bmleite.benchmark.hibernate;

import org.bmleite.benchmark.Environment;
import org.bmleite.model.DbObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Properties;

@State(Scope.Benchmark)
public class HibernateBenchmark {

    private SessionFactory sessionFactory;

    private EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;

    @Setup
    public void setup(Environment env) {
        Configuration configuration = new Configuration();
        Properties props = new Properties();
        props.put(org.hibernate.cfg.Environment.DATASOURCE, env.dataSource);
        configuration.setProperties(props);

        configuration.addAnnotatedClass(DbObject.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(props)
                .build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        entityManager = sessionFactory.createEntityManager();
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Benchmark
    public void measureSelectAllUsingHql(Environment env, Blackhole blackhole) {
        try (Session session = sessionFactory.openSession()) {
            List<DbObject> objects = session.createQuery("from DbObject", DbObject.class)
                    .setMaxResults(env.totalRows)
                    .list();
            blackhole.consume(objects);
        }
    }

    @Benchmark
    public void measureSelectAllUsingCriteriaApi(Environment env, Blackhole blackhole) {
        CriteriaQuery<DbObject> query = criteriaBuilder.createQuery(DbObject.class);
        Root<DbObject> root = query.from(DbObject.class);
        query.select(root);
        List<DbObject> objects = entityManager.createQuery(query)
                .setMaxResults(env.totalRows)
                .getResultList();
        blackhole.consume(objects);
    }
}
