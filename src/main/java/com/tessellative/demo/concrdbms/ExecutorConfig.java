package com.tessellative.demo.concrdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;


/**
 * This class controls the parallel async execution service
 * The executor service needs to be explicitly shut down during the context closed event, in case of the graceful shutdown
 *
 * @see actuator/shutdown
 * <p>
 * without shutting the executor down on the context closed event, the context can be corrupted with alreadym closed resources that the currently executing threads are using.
 * Example: the datasource could be closed while the processor is still working on something, and when it fisihed the status writeback will fail.
 * <p>
 * To test this, remove this config and shut down the application with the executors still running.
 */
@Configuration
public class ExecutorConfig implements ApplicationListener<ContextClosedEvent>, AsyncConfigurer {


    private static final Logger LOG = LoggerFactory.getLogger(ExecutorConfig.class);

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("Processor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        taskExecutor().shutdown();
        try {
            taskExecutor().getThreadPoolExecutor().awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ie) {
            LOG.error("Interrupted!", ie);
        }
    }
}
