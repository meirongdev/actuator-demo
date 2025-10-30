package dev.meirong.showcase.actuator_demo;

import org.springframework.boot.SpringApplication;
// import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.Map;

@SpringBootApplication
public class ActuatorDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ActuatorDemoApplication.class, args);
    // var app = new SpringApplication(ActuatorDemoApplication.class);
    // app.setApplicationStartup(new BufferingApplicationStartup(4096));
    // app.run(args);
  }

  // add securitychain beans for actuator endpoints and other endpoints
  // @Bean
  // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  // http.securityMatcher("/")
  // .authorizeHttpRequests(authz -> authz
  // .anyRequest().permitAll()
  // )
  // .httpBasic(Customizer.withDefaults());
  // return http.build();
  // }
  // @Bean
  // SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws
  // Exception {
  // http
  // .securityMatcher(EndpointRequest.toAnyEndpoint())
  // .authorizeHttpRequests(requests -> requests
  // .requestMatchers(EndpointRequest.to("health", "info", "metrics",
  // "prometheus")).permitAll()
  // .anyRequest().authenticated()
  // )
  // .httpBasic(Customizer.withDefaults())
  // .csrf(csrf -> csrf.disable());
  // return http.build();
  // }
  @Bean(name = "depsIndicator")
  HealthIndicator depsHealthIndicator() {
    return () -> {
      Health.Builder healthBuilder = Health.up();

      // Define the services to check
      Map<String, Boolean> serviceHealthMap = Map.of(
          "serviceA", checkServiceA(),
          "serviceB", checkServiceB());

      // Check each service and update health status
      serviceHealthMap.entrySet().forEach(entry -> {
        if (!entry.getValue()) {
          healthBuilder.down().withDetail(entry.getKey(), entry.getKey() + " is down");
        } else {
          healthBuilder.withDetail(entry.getKey(), entry.getKey() + " is healthy");
        }
      });

      boolean allServicesHealthy = serviceHealthMap.values().stream()
          .allMatch(Boolean::booleanValue);

      return allServicesHealthy ? healthBuilder.build() : healthBuilder.down().build();
    };
  }

  private boolean checkServiceA() {
    // mock implementation of Service A health check
    return true; // assume Service A is healthy
  }

  private boolean checkServiceB() {
    // mock implementation of Service B health check
    return false; // assume Service B is unhealthy
  }

  // @Bean
  // InfoContributor barInfoContributor() {
  // return new InfoContributor() {
  // @Override
  // public void contribute(Info.Builder builder) {
  // builder.withDetail("bar", Map.of("now", Instant.now().toString()));
  // }
  // };
  // }

  // @Bean
  // SanitizingFunction sanitizeEnv() {
  //   return (SanitizableData data) -> {
  //       if (data.getLowerCaseKey().equals("spring.application.pid")) {
  //           return data.withValue(999);
  //       }
  //       if (data.getLowerCaseKey().equals("spring.security.user.password")) {
  //           return data.withSanitizedValue();
  //       }
  //       return data;
  //   };
  // }

  @Bean
  InMemoryHttpExchangeRepository httpExchangeRepository() {
    return new InMemoryHttpExchangeRepository();
  }
}
