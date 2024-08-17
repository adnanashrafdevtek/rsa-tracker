package com.tracker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TemplateTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TemplateTask getTemplateTaskSample1() {
        return new TemplateTask().id(1L).name("name1").description("description1").createdBy("createdBy1").modifiedBy("modifiedBy1");
    }

    public static TemplateTask getTemplateTaskSample2() {
        return new TemplateTask().id(2L).name("name2").description("description2").createdBy("createdBy2").modifiedBy("modifiedBy2");
    }

    public static TemplateTask getTemplateTaskRandomSampleGenerator() {
        return new TemplateTask()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .modifiedBy(UUID.randomUUID().toString());
    }
}
