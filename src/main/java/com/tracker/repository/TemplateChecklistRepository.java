package com.tracker.repository;

import com.tracker.domain.TemplateChecklist;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TemplateChecklist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TemplateChecklistRepository extends JpaRepository<TemplateChecklist, Long>, JpaSpecificationExecutor<TemplateChecklist> {}
