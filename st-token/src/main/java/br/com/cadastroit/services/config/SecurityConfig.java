package br.com.cadastroit.services.config;

import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.SslSettings;

import br.com.cadastroit.services.config.security.TokenAuthenticationEntryPoint;
import br.com.cadastroit.services.config.security.TokenRequestFilter;
import br.com.cadastroit.services.config.security.TokenUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String path     =  "administracao"; //System.getenv("path");
    private final String username =  "admin"; //System.getenv("user_mongodb");
    private final String password =  "admin"; //System.getenv("pass_mongodb");
    private final String host     =  "localhost"; //System.getenv("host_mongodb");
    private final String dbName   =  "st_neousuario"; //System.getenv("name_mongodb");
    private final String port     =  "27017"; // System.getenv("port_mongodb");
    private TokenUserDetailsService tokenUserDetailsService;

    @Order(1)
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        try {
            MongoClientSettings settings = MongoClientSettings.builder().applyToSslSettings(ssl -> {
                ssl.applySettings(SslSettings.builder().enabled(false).build());
            }).applyConnectionString(new ConnectionString("mongodb://"+username+":"+password+"@"+host+":"+port))
                    .applyToConnectionPoolSettings(pool -> {pool.maxSize(50).maxWaitTime(60, TimeUnit.SECONDS)
                        .maxConnectionLifeTime(55, TimeUnit.SECONDS)
                        .maxConnectionIdleTime(50, TimeUnit.SECONDS)
                        .minSize(20)
                        .build();
            }).uuidRepresentation(UuidRepresentation.STANDARD).build();
            MongoClient client = MongoClients.create(settings);
            return new MongoTemplate(client, dbName);
        }catch (Exception ex){
            throw new Exception(String.format("Error on mongoClient, [error] = %s",ex.getMessage()));
        }
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(12);
    }

    private TokenRequestFilter tokenRequestFilter() throws Exception{
        if(this.tokenUserDetailsService == null){
            this.tokenUserDetailsService = TokenUserDetailsService.builder().mongoTemplate(this.mongoTemplate()).build();
        }
        return TokenRequestFilter.builder().mongoTemplate(this.mongoTemplate()).build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/"+path+"/**");
        http.addFilterBefore(this.tokenRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers("/eureka/**",
                        "/login",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/swagger-ui.html/**",
                        "/v2/api-docs").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.POST,"/"+path+"/usuario/criar/**",
                        "/"+path+"/usuario/recuperar/token",
                        "/"+path+"/usuario/token/**").permitAll()
                .and().authorizeRequests().anyRequest().authenticated()
                .and()
                        .exceptionHandling().authenticationEntryPoint(TokenAuthenticationEntryPoint.builder().build())// make sure we use stateless session; session won't be used to store user's state.
                        .and()
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }
}
