package com.tracker.domain;

import static com.tracker.domain.DocumentTestSamples.*;
import static com.tracker.domain.MessageTestSamples.*;
import static com.tracker.domain.TaskTestSamples.*;
import static com.tracker.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Team.class);
        Team team1 = getTeamSample1();
        Team team2 = new Team();
        assertThat(team1).isNotEqualTo(team2);

        team2.setId(team1.getId());
        assertThat(team1).isEqualTo(team2);

        team2 = getTeamSample2();
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    void tasksTest() throws Exception {
        Team team = getTeamRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        team.addTasks(taskBack);
        assertThat(team.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getTeam()).isEqualTo(team);

        team.removeTasks(taskBack);
        assertThat(team.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getTeam()).isNull();

        team.tasks(new HashSet<>(Set.of(taskBack)));
        assertThat(team.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getTeam()).isEqualTo(team);

        team.setTasks(new HashSet<>());
        assertThat(team.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getTeam()).isNull();
    }

    @Test
    void messagesTest() throws Exception {
        Team team = getTeamRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        team.addMessages(messageBack);
        assertThat(team.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getTeam()).isEqualTo(team);

        team.removeMessages(messageBack);
        assertThat(team.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getTeam()).isNull();

        team.messages(new HashSet<>(Set.of(messageBack)));
        assertThat(team.getMessages()).containsOnly(messageBack);
        assertThat(messageBack.getTeam()).isEqualTo(team);

        team.setMessages(new HashSet<>());
        assertThat(team.getMessages()).doesNotContain(messageBack);
        assertThat(messageBack.getTeam()).isNull();
    }

    @Test
    void documentsTest() throws Exception {
        Team team = getTeamRandomSampleGenerator();
        Document documentBack = getDocumentRandomSampleGenerator();

        team.addDocuments(documentBack);
        assertThat(team.getDocuments()).containsOnly(documentBack);
        assertThat(documentBack.getTeam()).isEqualTo(team);

        team.removeDocuments(documentBack);
        assertThat(team.getDocuments()).doesNotContain(documentBack);
        assertThat(documentBack.getTeam()).isNull();

        team.documents(new HashSet<>(Set.of(documentBack)));
        assertThat(team.getDocuments()).containsOnly(documentBack);
        assertThat(documentBack.getTeam()).isEqualTo(team);

        team.setDocuments(new HashSet<>());
        assertThat(team.getDocuments()).doesNotContain(documentBack);
        assertThat(documentBack.getTeam()).isNull();
    }
}
