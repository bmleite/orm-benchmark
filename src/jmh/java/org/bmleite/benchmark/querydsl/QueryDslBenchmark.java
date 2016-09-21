package org.bmleite.benchmark.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.types.DateTimeType;
import com.querydsl.sql.types.LocalDateType;
import org.bmleite.benchmark.Environment;
import org.bmleite.model.querydsl.QDbObject;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.SQLException;
import java.util.List;

@State(Scope.Benchmark)
public class QueryDslBenchmark {

    public static final QDbObject qDbObject = QDbObject.dbObject;

    private SQLQueryFactory sqlQueryFactory;

    @Setup
    public void setup(Environment env) {
        SQLTemplates templates;
        switch (env.dbEngine) {
            case H2:
                templates = H2Templates.builder().build();
                break;

            default:
                throw new IllegalStateException("No SQL template mapping for db engine: " + env.dbEngine.name());
        }

        Configuration configuration = new Configuration(templates);
        configuration.register(new DateTimeType());
        configuration.register(new LocalDateType());
        sqlQueryFactory = new SQLQueryFactory(configuration, env.dataSource);
    }

    @Benchmark
    public void measureSelectAll(Environment env, Blackhole blackhole) throws SQLException {
        List<Tuple> objects = sqlQueryFactory
                .select(qDbObject.id, qDbObject.prop1, qDbObject.prop2, qDbObject.prop3, qDbObject.prop4)
                .from(qDbObject)
                .limit(env.totalRows)
                .fetch();
        blackhole.consume(objects);
    }

}
