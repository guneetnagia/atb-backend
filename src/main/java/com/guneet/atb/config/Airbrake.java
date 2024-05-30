package com.guneet.atb.config;

import io.airbrake.javabrake.Config;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.Notifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Airbrake {

    @Value("${airbrake.project_id}")
    int airbrakeProjectId;
    @Value("${airbrake.project_key}")
    String airbrakeProjectKey;
    @Value("${spring.profiles.active}")
    String env;

    @Bean
    public Notifier notifier() {
        Config config = new Config();

        config.projectId = airbrakeProjectId;
        config.projectKey = airbrakeProjectKey;

        Notifier notifier = new Notifier(config);
        notifier.addFilter(
                (Notice notice) -> {
                    if (env.equals("local") || env.equals("test")) return null;
                    notice.setContext("environment", env);
                    return notice;
                });
        return notifier;
    }

}
