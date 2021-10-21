package com.restflow.core;

import com.restflow.core.Storage.StorageProperties;
import com.restflow.core.Storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
@EnableConfigurationProperties({
        StorageProperties.class
})
public class RESTflowApplication {

    public RESTflowApplication(@NonNull final ApplicationContext context) {
        CGlobal.initialize(context);
    }

    public static void main(String[] args) {
        SpringApplication.run(RESTflowApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

    /**
     * global context
     */
    public static final class CGlobal {
        // Global instance
        private static final AtomicReference<CGlobal> instance = new AtomicReference<>();
        // Spring application context
        private final ApplicationContext context;

        private CGlobal(@NonNull final ApplicationContext pContext) {
            context = pContext;
        }

        /**
         * returns singleton instance
         *
         * @return instance
         */
        public static CGlobal instance() {
            return instance.get();
        }

        private static void initialize(@NonNull final ApplicationContext context) {
            instance.compareAndSet(null, new CGlobal(context));
        }

        /**
         * returns the application context
         *
         * @return ApplicationContext
         */
        public ApplicationContext context() {
            return context;
        }
    }

    @ComponentScan
    @EnableSpringConfigured
    public class AspectJConfig { }
}

