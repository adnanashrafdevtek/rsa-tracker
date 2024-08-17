package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.TemplateTask;
import com.tracker.repository.TemplateTaskRepository;
import com.tracker.service.criteria.TemplateTaskCriteria;
import com.tracker.service.dto.TemplateTaskDTO;
import com.tracker.service.mapper.TemplateTaskMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TemplateTask} entities in the database.
 * The main input is a {@link TemplateTaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TemplateTaskDTO} or a {@link Page} of {@link TemplateTaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TemplateTaskQueryService extends QueryService<TemplateTask> {

    private final Logger log = LoggerFactory.getLogger(TemplateTaskQueryService.class);

    private final TemplateTaskRepository templateTaskRepository;

    private final TemplateTaskMapper templateTaskMapper;

    public TemplateTaskQueryService(TemplateTaskRepository templateTaskRepository, TemplateTaskMapper templateTaskMapper) {
        this.templateTaskRepository = templateTaskRepository;
        this.templateTaskMapper = templateTaskMapper;
    }

    /**
     * Return a {@link List} of {@link TemplateTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TemplateTaskDTO> findByCriteria(TemplateTaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TemplateTask> specification = createSpecification(criteria);
        return templateTaskMapper.toDto(templateTaskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TemplateTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TemplateTaskDTO> findByCriteria(TemplateTaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TemplateTask> specification = createSpecification(criteria);
        return templateTaskRepository.findAll(specification, page).map(templateTaskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TemplateTaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TemplateTask> specification = createSpecification(criteria);
        return templateTaskRepository.count(specification);
    }

    /**
     * Function to convert {@link TemplateTaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TemplateTask> createSpecification(TemplateTaskCriteria criteria) {
        Specification<TemplateTask> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TemplateTask_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TemplateTask_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TemplateTask_.description));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), TemplateTask_.createdDate));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), TemplateTask_.createdBy));
            }
            if (criteria.getModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedDate(), TemplateTask_.modifiedDate));
            }
            if (criteria.getModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModifiedBy(), TemplateTask_.modifiedBy));
            }
            if (criteria.getTemplateId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTemplateId(),
                            root -> root.join(TemplateTask_.template, JoinType.LEFT).get(Template_.id)
                        )
                    );
            }
            if (criteria.getChecklistId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getChecklistId(),
                            root -> root.join(TemplateTask_.checklists, JoinType.LEFT).get(TemplateChecklist_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
