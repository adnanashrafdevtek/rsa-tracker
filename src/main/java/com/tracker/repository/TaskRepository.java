package com.tracker.repository;

import com.tracker.domain.Task;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @Query("select task from Task task where task.assignedTo.login = ?#{authentication.name}")
    List<Task> findByAssignedToIsCurrentUser();
}
