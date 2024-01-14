package com.example.project01.config.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager",
        basePackages = {"com.example.project01.youtube.repository"}
)
@MapperScan(
//        sqlSessionFactoryRef = "mysqlSqlSessionFactory",
        basePackages = {"com.example.project01.youtube.mapper"}
)
public class DataAccessConfig {

    // JPA의 영속성 컨텍스트를 관리하는 엔티티 매니저 팩토리 빈 등록
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource datasource, JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(datasource);
        entityManagerFactoryBean.setPackagesToScan("com.example.project01.youtube.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());
        entityManagerFactoryBean.setJpaProperties(properties);
        return entityManagerFactoryBean;
    }

    // Mybatis SQL 구문을 실행하기 위한 sql 세션 팩토리 빈 등록
//    @Bean(name = "mysqlSqlSessionFactory")
//    public SqlSessionFactoryBean sessionFactoryBean(DataSource datasource, MybatisProperties mybatisProperties) throws IOException {
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//
//        sessionFactoryBean.setDataSource(datasource);
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//
//        sessionFactoryBean.setMapperLocations(resolver.getResources(mybatisProperties.getMapperLocations()[0]));
//        return sessionFactoryBean;
//    }

    // 트랜잭션 경계 설정을 관리하는 트랜잭션 매니저 빈 등록
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }
}