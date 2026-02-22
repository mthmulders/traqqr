package it.mulders.traqqr.db;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import javax.sql.DataSource;

@ApplicationScoped
public class JNDIDataSourceProducer {
    @Resource(name = "jdbc/traqqr-ds")
    private DataSource dataSource;

    @ApplicationScoped
    @Produces
    public DataSource createDataSource() {
        return dataSource;
    }
}
