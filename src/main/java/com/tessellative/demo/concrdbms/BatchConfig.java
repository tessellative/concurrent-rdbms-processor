package com.tessellative.demo.concrdbms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {


    @Value("${processing.thread.count}")
    private Integer threadCount;


    public Integer getThreadCount() {
        return threadCount;
    }
}
