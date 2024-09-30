package br.com.complianceit.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Slf4j
@ExtendWith(value = {SpringExtension.class})
class CstCommonsConnectionApplicationTests {

    @Resource
    private EntityManagerFactory entityManagerFactory;

    @Test
    void checkConnection() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        Object r = entityManager.createNativeQuery("SELECT 1 FROM DUAL").getSingleResult();
        log.info("R = " + r.toString());


        entityManager.clear();
        entityManager.close();
    }

}
