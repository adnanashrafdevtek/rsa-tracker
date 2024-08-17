package com.tracker.domain;

import static com.tracker.domain.TemplateChecklistTestSamples.*;
import static com.tracker.domain.TemplateTaskTestSamples.*;
import static com.tracker.domain.TemplateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TemplateTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemplateTask.class);
        TemplateTask templateTask1 = getTemplateTaskSample1();
        TemplateTask templateTask2 = new TemplateTask();
        assertThat(templateTask1).isNotEqualTo(templateTask2);

        templateTask2.setId(templateTask1.getId());
        assertThat(templateTask1).isEqualTo(templateTask2);

        templateTask2 = getTemplateTaskSample2();
        assertThat(templateTask1).isNotEqualTo(templateTask2);
    }

    @Test
    void templateTest() throws Exception {
        TemplateTask templateTask = getTemplateTaskRandomSampleGenerator();
        Template templateBack = getTemplateRandomSampleGenerator();

        templateTask.setTemplate(templateBack);
        assertThat(templateTask.getTemplate()).isEqualTo(templateBack);

        templateTask.template(null);
        assertThat(templateTask.getTemplate()).isNull();
    }

    @Test
    void checklistTest() throws Exception {
        TemplateTask templateTask = getTemplateTaskRandomSampleGenerator();
        TemplateChecklist templateChecklistBack = getTemplateChecklistRandomSampleGenerator();

        templateTask.addChecklist(templateChecklistBack);
        assertThat(templateTask.getChecklists()).containsOnly(templateChecklistBack);
        assertThat(templateChecklistBack.getTask()).isEqualTo(templateTask);

        templateTask.removeChecklist(templateChecklistBack);
        assertThat(templateTask.getChecklists()).doesNotContain(templateChecklistBack);
        assertThat(templateChecklistBack.getTask()).isNull();

        templateTask.checklists(new HashSet<>(Set.of(templateChecklistBack)));
        assertThat(templateTask.getChecklists()).containsOnly(templateChecklistBack);
        assertThat(templateChecklistBack.getTask()).isEqualTo(templateTask);

        templateTask.setChecklists(new HashSet<>());
        assertThat(templateTask.getChecklists()).doesNotContain(templateChecklistBack);
        assertThat(templateChecklistBack.getTask()).isNull();
    }
}
