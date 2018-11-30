package com.tessellative.demo.concrdbms.business;

import com.tessellative.demo.concrdbms.BatchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledService.class);

    @Autowired
    private AsyncProcessorService asyncProcessorService;

    @Autowired
    private BatchConfig batchConfig;


    @Scheduled(fixedDelay = 2_000, initialDelay = 2_000)
    public void scheduleExecution() {
        // Executing in parallel via the async execution
        LOG.info("Starting scheduling cycle!");
        List<CompletableFuture> futures = new ArrayList<>(batchConfig.getThreadCount());
        for (int i = 0; i < batchConfig.getThreadCount(); i++) {
            futures.add(asyncProcessorService.processCitiesAsync());
        }

        // waiting for the task executors
        for (CompletableFuture future : futures) {
            future.join();
        }
        LOG.info("Finished scheduling cycle!");
    }
}
