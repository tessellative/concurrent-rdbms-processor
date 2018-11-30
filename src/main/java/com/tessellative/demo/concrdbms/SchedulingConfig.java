package com.tessellative.demo.concrdbms;

import com.tessellative.demo.concrdbms.business.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class controls the scheduled execution
 *
 * @see ScheduledService
 */
@Configuration
public class SchedulingConfig implements SchedulingConfigurer, ApplicationListener<ContextClosedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulingConfig.class);
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());

    }

    @Bean
    public ScheduledExecutorService taskScheduler() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        return scheduledExecutorService;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        taskScheduler().shutdownNow();
        try {
            taskScheduler().awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ie) {
            LOG.error("interrupted!", ie);
        }
    }
}
