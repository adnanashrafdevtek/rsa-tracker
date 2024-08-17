package com.tracker.domain;

import static com.tracker.domain.TemplateChecklistTestSamples.*;
import static com.tracker.domain.TemplateTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemplateChecklistTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemplateChecklist.class);
        TemplateChecklist templateChecklist1 = getTemplateChecklistSample1();
        TemplateChecklist templateChecklist2 = new TemplateChecklist();
        assertThat(templateChecklist1).isNotEqualTo(templateChecklist2);

        templateChecklist2.setId(templateChecklist1.getId());
        assertThat(templateChecklist1).isEqualTo(templateChecklist2);

        templateChecklist2 = getTemplateChecklistSample2();
        assertThat(templateChecklist1).isNotEqualTo(templateChecklist2);
    }

    @Test
    void taskTest() throws Exception {
        TemplateChecklist templateChecklist = getTemplateChecklistRandomSampleGenerator();
        TemplateTask templateTaskBack = getTemplateTaskRandomSampleGenerator();

        templateChecklist.setTask(templateTaskBack);
        assertThat(templateChecklist.getTask()).isEqualTo(templateTaskBack);

        templateChecklist.task(null);
        assertThat(templateChecklist.getTask()).isNull();
    }
}
