package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.TemplateChecklist;
import com.tracker.repository.TemplateChecklistRepository;
import com.tracker.service.criteria.TemplateChecklistCriteria;
import com.tracker.service.dto.TemplateChecklistDTO;
import com.tracker.service.mapper.TemplateChecklistMapper;
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
 * Service for executing complex queries for {@link TemplateChecklist} entities in the database.
 * The main input is a {@link TemplateChecklistCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TemplateChecklistDTO} or a {@link Page} of {@link TemplateChecklistDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TemplateChecklistQueryService extends QueryService<TemplateChecklist> {

    private final Logger log = LoggerFactory.getLogger(TemplateChecklistQueryService.class);

    private final TemplateChecklistRepository templateChecklistRepository;

    private final TemplateChecklistMapper templateChecklistMapper;

    public TemplateChecklistQueryService(
        TemplateChecklistRepository templateChecklistRepository,
        TemplateChecklistMapper templateChecklistMapper
    ) {
        this.templateChecklistRepository = templateChecklistRepository;
        this.templateChecklistMapper = templateChecklistMapper;
    }

    /**
     * Return a {@link List} of {@link TemplateChecklistDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TemplateChecklistDTO> findByCriteria(TemplateChecklistCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TemplateChecklist> specification = createSpecification(criteria);
        return templateChecklistMapper.toDto(templateChecklistRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TemplateChecklistDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TemplateChecklistDTO> findByCriteria(TemplateChecklistCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TemplateChecklist> specification = createSpecification(criteria);
        return templateChecklistRepository.findAll(specification, page).map(templateChecklistMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TemplateChecklistCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TemplateChecklist> specification = createSpecification(criteria);
        return templateChecklistRepository.count(specification);
    }

    /**
     * Function to convert {@link TemplateChecklistCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TemplateChecklist> createSpecification(TemplateChecklistCriteria criteria) {
        Specification<TemplateChecklist> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TemplateChecklist_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TemplateChecklist_.name));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), TemplateChecklist_.createdDate));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), TemplateChecklist_.createdBy));
            }
            if (criteria.getModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedDate(), TemplateChecklist_.modifiedDate));
            }
            if (criteria.getModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModifiedBy(), TemplateChecklist_.modifiedBy));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTaskId(),
                            root -> root.join(TemplateChecklist_.task, JoinType.LEFT).get(TemplateTask_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
