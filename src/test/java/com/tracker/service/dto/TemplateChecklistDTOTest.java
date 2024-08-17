package com.tracker.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TemplateChecklistDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TemplateChecklistDTO.class);
        TemplateChecklistDTO templateChecklistDTO1 = new TemplateChecklistDTO();
        templateChecklistDTO1.setId(1L);
        TemplateChecklistDTO templateChecklistDTO2 = new TemplateChecklistDTO();
        assertThat(templateChecklistDTO1).isNotEqualTo(templateChecklistDTO2);
        templateChecklistDTO2.setId(templateChecklistDTO1.getId());
        assertThat(templateChecklistDTO1).isEqualTo(templateChecklistDTO2);
        templateChecklistDTO2.setId(2L);
        assertThat(templateChecklistDTO1).isNotEqualTo(templateChecklistDTO2);
        templateChecklistDTO1.setId(null);
        assertThat(templateChecklistDTO1).isNotEqualTo(templateChecklistDTO2);
    }
}
