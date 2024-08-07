package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.Task;
import com.tracker.repository.TaskRepository;
import com.tracker.service.criteria.TaskCriteria;
import com.tracker.service.dto.TaskDTO;
import com.tracker.service.mapper.TaskMapper;
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
 * Service for executing complex queries for {@link Task} entities in the database.
 * The main input is a {@link TaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TaskDTO} or a {@link Page} of {@link TaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskQueryService extends QueryService<Task> {

    private final Logger log = LoggerFactory.getLogger(TaskQueryService.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskQueryService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Return a {@link List} of {@link TaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> findByCriteria(TaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskMapper.toDto(taskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> findByCriteria(TaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.findAll(specification, page).map(taskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Task> createSpecification(TaskCriteria criteria) {
        Specification<Task> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Task_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Task_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Task_.description));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildSpecification(criteria.getPriority(), Task_.priority));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Task_.status));
            }
            if (criteria.getDueDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDueDate(), Task_.dueDate));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Task_.createdDate));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Task_.createdBy));
            }
            if (criteria.getModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedDate(), Task_.modifiedDate));
            }
            if (criteria.getModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModifiedBy(), Task_.modifiedBy));
            }
            if (criteria.getAssignedToId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAssignedToId(), root -> root.join(Task_.assignedTo, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getTeamId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTeamId(), root -> root.join(Task_.team, JoinType.LEFT).get(Team_.id)));
            }
        }
        return specification;
    }
}
