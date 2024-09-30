package br.com.cadastroit.services.configuration;

import com.zaxxer.hikari.HikariDataSource;

import br.com.cadastroit.services.configuration.beans.DbConnectionBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Primary
@EnableJpaRepositories
@EnableTransactionManagement
@Slf4j
public class DatabaseLayer {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    private String poolsize = System.getenv("POOL");
    private String oracle = System.getenv("oracle") == null ? "Y" : System.getenv("oracle");
    private HikariDataSource hikariDataSource;
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Bean
    public HikariDataSource hikariDataSource() {
        if (hikariDataSource == null) {
            hikariDataSource = new HikariDataSource();
            hikariDataSource.setDriverClassName(driver);
            hikariDataSource.setJdbcUrl(url);
            hikariDataSource.setUsername(username);
            hikariDataSource.setPassword(password);
            hikariDataSource.setPoolName("cst-persistence-unit");

            hikariDataSource.setMinimumIdle(10);
            hikariDataSource.setMaxLifetime(40000);
            hikariDataSource.setIdleTimeout(30000);
            hikariDataSource.setLeakDetectionThreshold(40000);
            hikariDataSource.setConnectionTestQuery("SELECT 1 FROM DUAL");
            if (poolsize != null) {
                hikariDataSource.setMaximumPoolSize(Integer.parseInt(poolsize));
            }
        }
        return hikariDataSource;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        if (localContainerEntityManagerFactoryBean == null) {
            HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            jpaVendorAdapter.setDatabase(oracle.equals("Y") ? Database.ORACLE : Database.POSTGRESQL);

            localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            localContainerEntityManagerFactoryBean.setDataSource(this.dataSource());
            localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
            localContainerEntityManagerFactoryBean.setPersistenceUnitName("cst-persistence-unit");
            localContainerEntityManagerFactoryBean.setPackagesToScan("br.com.complianceit.*");
        }
        return localContainerEntityManagerFactoryBean;
    }
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }
    @Bean
    public DbConnectionBean dbConnectionBean(){
        return DbConnectionBean.builder().build();
    }
}
