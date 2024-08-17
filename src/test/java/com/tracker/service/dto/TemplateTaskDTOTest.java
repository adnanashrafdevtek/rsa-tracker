package com.tracker.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemplateTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemplateTaskDTO.class);
        TemplateTaskDTO templateTaskDTO1 = new TemplateTaskDTO();
        templateTaskDTO1.setId(1L);
        TemplateTaskDTO templateTaskDTO2 = new TemplateTaskDTO();
        assertThat(templateTaskDTO1).isNotEqualTo(templateTaskDTO2);
        templateTaskDTO2.setId(templateTaskDTO1.getId());
        assertThat(templateTaskDTO1).isEqualTo(templateTaskDTO2);
        templateTaskDTO2.setId(2L);
        assertThat(templateTaskDTO1).isNotEqualTo(templateTaskDTO2);
        templateTaskDTO1.setId(null);
        assertThat(templateTaskDTO1).isNotEqualTo(templateTaskDTO2);
    }
}
