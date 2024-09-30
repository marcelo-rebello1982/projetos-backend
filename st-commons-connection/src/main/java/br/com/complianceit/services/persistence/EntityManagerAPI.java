package br.com.complianceit.services.persistence;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

@Builder
@Data
@Slf4j
public class EntityManagerAPI {

    private static final String MSG_ERROR_CLOSE_CONNECTION = "Erro ao encerrar conexoes, [Erro] = %s";
    private Logger logger;
    
    private Logger logger(){
        if(this.logger == null){
            this.logger = Logger.getLogger(EntityManagerAPI.class);
        }
        return this.logger;
    }

    public Object merge(EntityManager entityManager, Object record){
        if(!entityManager.getTransaction().isActive()){
            this.logger().info(String.format("Renovando transacao [%s].", entityManager));
            entityManager.getTransaction().begin();
        }
        return entityManager.merge(record);
    }

    public void commit(EntityManager entityManager){
        if(!entityManager.getTransaction().isActive()){
            this.logger().info(String.format("Renovando transacao [%s].", entityManager));
            entityManager.getTransaction().begin();
        }
        entityManager.getTransaction().commit();
    }

    public void closeConnection(EntityManager entityManager) {
        try {
            if (entityManager.isOpen()) {
                entityManager.clear();
                entityManager.close();
                this.logger().info(String.format("Connection %s has been closed", entityManager));
            }
        }catch (Exception ex){
            this.logger().error(String.format(MSG_ERROR_CLOSE_CONNECTION, ex.getMessage()));
        }
    }
}
