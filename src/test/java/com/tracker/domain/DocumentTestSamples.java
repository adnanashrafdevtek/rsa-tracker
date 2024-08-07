package com.tracker.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Document getDocumentSample1() {
        return new Document().id(1L).name("name1").mimeType("mimeType1").url("url1").createdBy("createdBy1");
    }

    public static Document getDocumentSample2() {
        return new Document().id(2L).name("name2").mimeType("mimeType2").url("url2").createdBy("createdBy2");
    }

    public static Document getDocumentRandomSampleGenerator() {
        return new Document()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .mimeType(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
