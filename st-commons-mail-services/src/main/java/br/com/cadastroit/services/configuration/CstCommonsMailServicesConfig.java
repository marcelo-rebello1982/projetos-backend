package br.com.cadastroit.services.configuration;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CstCommonsMailServicesConfig {

    @EventListener(classes = {ApplicationStartedEvent.class})
    public void applicationStarted(){
        log.info("Application already...");
    }
}
