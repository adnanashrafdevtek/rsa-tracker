package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.Team;
import com.tracker.repository.TeamRepository;
import com.tracker.service.criteria.TeamCriteria;
import com.tracker.service.dto.TeamDTO;
import com.tracker.service.mapper.TeamMapper;
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
 * Service for executing complex queries for {@link Team} entities in the database.
 * The main input is a {@link TeamCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TeamDTO} or a {@link Page} of {@link TeamDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TeamQueryService extends QueryService<Team> {

    private final Logger log = LoggerFactory.getLogger(TeamQueryService.class);

    private final TeamRepository teamRepository;

    private final TeamMapper teamMapper;

    public TeamQueryService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    /**
     * Return a {@link List} of {@link TeamDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TeamDTO> findByCriteria(TeamCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Team> specification = createSpecification(criteria);
        return teamMapper.toDto(teamRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TeamDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TeamDTO> findByCriteria(TeamCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Team> specification = createSpecification(criteria);
        return teamRepository.findAll(specification, page).map(teamMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TeamCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Team> specification = createSpecification(criteria);
        return teamRepository.count(specification);
    }

    /**
     * Function to convert {@link TeamCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Team> createSpecification(TeamCriteria criteria) {
        Specification<Team> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Team_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Team_.name));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Team_.active));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Team_.createdDate));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Team_.createdBy));
            }
            if (criteria.getModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedDate(), Team_.modifiedDate));
            }
            if (criteria.getModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModifiedBy(), Team_.modifiedBy));
            }
            if (criteria.getTeamLeadId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeamLeadId(), root -> root.join(Team_.teamLead, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getTeamMembersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeamMembersId(), root -> root.join(Team_.teamMembers, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getTasksId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTasksId(), root -> root.join(Team_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
            if (criteria.getMessagesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMessagesId(), root -> root.join(Team_.messages, JoinType.LEFT).get(Message_.id))
                    );
            }
            if (criteria.getDocumentsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDocumentsId(), root -> root.join(Team_.documents, JoinType.LEFT).get(Document_.id))
                    );
            }
        }
        return specification;
    }
}
