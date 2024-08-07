package com.tracker.domain;

import static com.tracker.domain.DocumentTestSamples.*;
import static com.tracker.domain.MessageTestSamples.*;
import static com.tracker.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Document.class);
        Document document1 = getDocumentSample1();
        Document document2 = new Document();
        assertThat(document1).isNotEqualTo(document2);

        document2.setId(document1.getId());
        assertThat(document1).isEqualTo(document2);

        document2 = getDocumentSample2();
        assertThat(document1).isNotEqualTo(document2);
    }

    @Test
    void messageTest() throws Exception {
        Document document = getDocumentRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        document.setMessage(messageBack);
        assertThat(document.getMessage()).isEqualTo(messageBack);

        document.message(null);
        assertThat(document.getMessage()).isNull();
    }

    @Test
    void teamTest() throws Exception {
        Document document = getDocumentRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        document.setTeam(teamBack);
        assertThat(document.getTeam()).isEqualTo(teamBack);

        document.team(null);
        assertThat(document.getTeam()).isNull();
    }
}
