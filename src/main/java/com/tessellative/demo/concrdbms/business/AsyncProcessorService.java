package com.tessellative.demo.concrdbms.business;


import com.tessellative.demo.concrdbms.model.City;
import com.tessellative.demo.concrdbms.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncProcessorService {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncProcessorService.class);

    @Autowired
    private CityService cityService;

    @Async
    public CompletableFuture<Void> processCitiesAsync() {
        // Set the status to in progress
        List<City> cities = cityService.loadCitiesForProcessing();

        LOG.info("Loaded {} cities", cities.size());

        for (City city : cities) {
            // Do the time consuming stuff here.
            // This is outside of DB transaction on purpose.
            // This result list will only be accessible to this thread due to "FOR UPDATE SKIP LOCKED" statement
            LOG.info("Processing started: {}", city.getName());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                LOG.error("interrupted: ", ie);
            }

            // adding some chance to book a few failed statuses.
            if (Math.floor(Math.random() * 3) == 0) {
                // update with failed
                LOG.info("Processing failed: {}", city.getName());
                cityService.updateFailed(city.getId());
            } else {
                // update with complete
                LOG.info("Processing completed: {}", city.getName());
                cityService.updateComplete(city.getId());
            }
        }
        return CompletableFuture.completedFuture(null);
    }


}
