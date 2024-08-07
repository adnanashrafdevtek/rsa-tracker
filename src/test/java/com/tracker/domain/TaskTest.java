package com.tracker.domain;

import static com.tracker.domain.TaskTestSamples.*;
import static com.tracker.domain.TeamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void teamTest() throws Exception {
        Task task = getTaskRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        task.setTeam(teamBack);
        assertThat(task.getTeam()).isEqualTo(teamBack);

        task.team(null);
        assertThat(task.getTeam()).isNull();
    }
}
