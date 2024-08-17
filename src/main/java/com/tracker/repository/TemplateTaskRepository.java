package com.tracker.repository;

import com.tracker.domain.TemplateTask;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TemplateTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TemplateTaskRepository extends JpaRepository<TemplateTask, Long>, JpaSpecificationExecutor<TemplateTask> {}
