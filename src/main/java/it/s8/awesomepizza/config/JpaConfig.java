package it.s8.awesomepizza.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "it.s8.awesomepizza.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}
