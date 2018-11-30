package com.tessellative.demo.concrdbms.dao;

import com.tessellative.demo.concrdbms.model.City;
import com.tessellative.demo.concrdbms.model.ProcessingStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CityDao {

    /**
     *  The limit 13 is for demonstration purposes only, Optimally this should match the default fetch size on the driver
     * See the classpath:application.yml - default-fetch-size
     *
      * @return
     */
    @Select("SELECT * FROM city WHERE processing_status = 'NOT_STARTED' FOR UPDATE SKIP LOCKED LIMIT 13")
    List<City> loadForProcessing();

    @Update("UPDATE city set processing_status=#{status} where id=#{id}")
    void updateStatus(@Param("id") Integer id, @Param("status") ProcessingStatus status);

    @Select("SELECT count(*) FROM city WHERE processing_status=#{status}")
    Integer countForStatus(@Param("status") ProcessingStatus status);

}
