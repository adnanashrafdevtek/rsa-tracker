package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.Template;
import com.tracker.repository.TemplateRepository;
import com.tracker.service.criteria.TemplateCriteria;
import com.tracker.service.dto.TemplateDTO;
import com.tracker.service.mapper.TemplateMapper;
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
 * Service for executing complex queries for {@link Template} entities in the database.
 * The main input is a {@link TemplateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TemplateDTO} or a {@link Page} of {@link TemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TemplateQueryService extends QueryService<Template> {

    private final Logger log = LoggerFactory.getLogger(TemplateQueryService.class);

    private final TemplateRepository templateRepository;

    private final TemplateMapper templateMapper;

    public TemplateQueryService(TemplateRepository templateRepository, TemplateMapper templateMapper) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
    }

    /**
     * Return a {@link List} of {@link TemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TemplateDTO> findByCriteria(TemplateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Template> specification = createSpecification(criteria);
        return templateMapper.toDto(templateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TemplateDTO> findByCriteria(TemplateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Template> specification = createSpecification(criteria);
        return templateRepository.findAll(specification, page).map(templateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TemplateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Template> specification = createSpecification(criteria);
        return templateRepository.count(specification);
    }

    /**
     * Function to convert {@link TemplateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Template> createSpecification(TemplateCriteria criteria) {
        Specification<Template> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Template_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Template_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Template_.description));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Template_.createdDate));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Template_.createdBy));
            }
            if (criteria.getModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedDate(), Template_.modifiedDate));
            }
            if (criteria.getModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModifiedBy(), Template_.modifiedBy));
            }
            if (criteria.getTasksId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTasksId(), root -> root.join(Template_.tasks, JoinType.LEFT).get(TemplateTask_.id))
                    );
            }
        }
        return specification;
    }
}
