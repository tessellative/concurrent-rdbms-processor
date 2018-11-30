package com.tessellative.demo.concrdbms.service;

import com.tessellative.demo.concrdbms.dao.CityDao;
import com.tessellative.demo.concrdbms.model.City;
import com.tessellative.demo.concrdbms.model.ProcessingStatus;
import com.tessellative.demo.concrdbms.web.ExecutionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * This is our Transactional Boundary
 */
@Service
public class CityService {

    @Autowired
    private CityDao cityDao;

    @Transactional()
    public List<City> loadCitiesForProcessing() {
        List<City> cities = cityDao.loadForProcessing();

        // Setting the processing status to in progress, for the locked rows.
        // This will avoid selecting the same rows from now on
        // This is within the same transaction, so the rows are exclusively locked for the update
        // See he CityDao for the query
        for (City city : cities) {
            try {
                cityDao.updateStatus(city.getId(), ProcessingStatus.IN_PROGRESS);
                city.setProcessingStatus(ProcessingStatus.IN_PROGRESS);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return cities;
    }

    @Transactional
    public void updateComplete(Integer id) {
        cityDao.updateStatus(id, ProcessingStatus.COMPLETED);
    }

    @Transactional
    public void updateFailed(Integer id) {
        cityDao.updateStatus(id, ProcessingStatus.FAILED);

    }

    @Transactional
    public ExecutionStatus getStats() {
        ExecutionStatus execStat = new ExecutionStatus();
        execStat.setPending(cityDao.countForStatus(ProcessingStatus.NOT_STARTED));
        execStat.setFailed(cityDao.countForStatus(ProcessingStatus.FAILED));
        execStat.setInProgress(cityDao.countForStatus(ProcessingStatus.IN_PROGRESS));
        execStat.setCompleted(cityDao.countForStatus(ProcessingStatus.COMPLETED));
        return execStat;
    }
}
