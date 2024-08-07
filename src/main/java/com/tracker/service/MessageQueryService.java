package com.tracker.service;

import com.tracker.domain.*; // for static metamodels
import com.tracker.domain.Message;
import com.tracker.repository.MessageRepository;
import com.tracker.service.criteria.MessageCriteria;
import com.tracker.service.dto.MessageDTO;
import com.tracker.service.mapper.MessageMapper;
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
 * Service for executing complex queries for {@link Message} entities in the database.
 * The main input is a {@link MessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MessageDTO} or a {@link Page} of {@link MessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MessageQueryService extends QueryService<Message> {

    private final Logger log = LoggerFactory.getLogger(MessageQueryService.class);

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    public MessageQueryService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    /**
     * Return a {@link List} of {@link MessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> findByCriteria(MessageCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Message> specification = createSpecification(criteria);
        return messageMapper.toDto(messageRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MessageDTO> findByCriteria(MessageCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Message> specification = createSpecification(criteria);
        return messageRepository.findAll(specification, page).map(messageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MessageCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Message> specification = createSpecification(criteria);
        return messageRepository.count(specification);
    }

    /**
     * Function to convert {@link MessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Message> createSpecification(MessageCriteria criteria) {
        Specification<Message> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Message_.id));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Message_.email));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Message_.message));
            }
            if (criteria.getSendToAllTeamMembers() != null) {
                specification = specification.and(buildSpecification(criteria.getSendToAllTeamMembers(), Message_.sendToAllTeamMembers));
            }
            if (criteria.getDateSent() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateSent(), Message_.dateSent));
            }
            if (criteria.getToUserIdId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getToUserIdId(), root -> root.join(Message_.toUserId, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getFromUserIdId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFromUserIdId(), root -> root.join(Message_.fromUserId, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getTeamId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeamId(), root -> root.join(Message_.team, JoinType.LEFT).get(Team_.id))
                    );
            }
            if (criteria.getDocumentsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDocumentsId(),
                            root -> root.join(Message_.documents, JoinType.LEFT).get(Document_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
