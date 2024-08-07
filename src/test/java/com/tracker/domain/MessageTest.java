package com.tracker.domain;

import static com.tracker.domain.DocumentTestSamples.*;
import static com.tracker.domain.MessageTestSamples.*;
import static com.tracker.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = getMessageSample1();
        Message message2 = new Message();
        assertThat(message1).isNotEqualTo(message2);

        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);

        message2 = getMessageSample2();
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    void teamTest() throws Exception {
        Message message = getMessageRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        message.setTeam(teamBack);
        assertThat(message.getTeam()).isEqualTo(teamBack);

        message.team(null);
        assertThat(message.getTeam()).isNull();
    }

    @Test
    void documentsTest() throws Exception {
        Message message = getMessageRandomSampleGenerator();
        Document documentBack = getDocumentRandomSampleGenerator();

        message.addDocuments(documentBack);
        assertThat(message.getDocuments()).containsOnly(documentBack);
        assertThat(documentBack.getMessage()).isEqualTo(message);

        message.removeDocuments(documentBack);
        assertThat(message.getDocuments()).doesNotContain(documentBack);
        assertThat(documentBack.getMessage()).isNull();

        message.documents(new HashSet<>(Set.of(documentBack)));
        assertThat(message.getDocuments()).containsOnly(documentBack);
        assertThat(documentBack.getMessage()).isEqualTo(message);

        message.setDocuments(new HashSet<>());
        assertThat(message.getDocuments()).doesNotContain(documentBack);
        assertThat(documentBack.getMessage()).isNull();
    }
}
