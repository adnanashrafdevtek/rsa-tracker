package com.tracker.domain;

import static com.tracker.domain.TemplateTaskTestSamples.*;
import static com.tracker.domain.TemplateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Template.class);
        Template template1 = getTemplateSample1();
        Template template2 = new Template();
        assertThat(template1).isNotEqualTo(template2);

        template2.setId(template1.getId());
        assertThat(template1).isEqualTo(template2);

        template2 = getTemplateSample2();
        assertThat(template1).isNotEqualTo(template2);
    }

    @Test
    void tasksTest() throws Exception {
        Template template = getTemplateRandomSampleGenerator();
        TemplateTask templateTaskBack = getTemplateTaskRandomSampleGenerator();

        template.addTasks(templateTaskBack);
        assertThat(template.getTasks()).containsOnly(templateTaskBack);
        assertThat(templateTaskBack.getTemplate()).isEqualTo(template);

        template.removeTasks(templateTaskBack);
        assertThat(template.getTasks()).doesNotContain(templateTaskBack);
        assertThat(templateTaskBack.getTemplate()).isNull();

        template.tasks(new HashSet<>(Set.of(templateTaskBack)));
        assertThat(template.getTasks()).containsOnly(templateTaskBack);
        assertThat(templateTaskBack.getTemplate()).isEqualTo(template);

        template.setTasks(new HashSet<>());
        assertThat(template.getTasks()).doesNotContain(templateTaskBack);
        assertThat(templateTaskBack.getTemplate()).isNull();
    }
}
