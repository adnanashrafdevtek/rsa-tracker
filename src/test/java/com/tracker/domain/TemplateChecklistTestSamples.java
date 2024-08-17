package com.tracker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TemplateChecklistTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TemplateChecklist getTemplateChecklistSample1() {
        return new TemplateChecklist().id(1L).name("name1").createdBy("createdBy1").modifiedBy("modifiedBy1");
    }

    public static TemplateChecklist getTemplateChecklistSample2() {
        return new TemplateChecklist().id(2L).name("name2").createdBy("createdBy2").modifiedBy("modifiedBy2");
    }

    public static TemplateChecklist getTemplateChecklistRandomSampleGenerator() {
        return new TemplateChecklist()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .modifiedBy(UUID.randomUUID().toString());
    }
}
