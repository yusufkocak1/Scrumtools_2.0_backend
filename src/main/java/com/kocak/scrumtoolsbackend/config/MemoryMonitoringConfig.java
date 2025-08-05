package com.kocak.scrumtoolsbackend.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.springframework.boot.actuator.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Memory monitoring configuration
 * JVM memory kullanımını ve GC performansını izler
 */
@Configuration
public class MemoryMonitoringConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config().commonTags("application", "scrum-tools");

            // JVM Memory metrics
            new JvmMemoryMetrics().bindTo(registry);

            // GC metrics
            new JvmGcMetrics().bindTo(registry);

            // CPU metrics
            new ProcessorMetrics().bindTo(registry);
        };
    }
}
