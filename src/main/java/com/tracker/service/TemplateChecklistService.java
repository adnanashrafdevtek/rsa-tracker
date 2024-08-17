package com.tracker.service;

import com.tracker.domain.TemplateChecklist;
import com.tracker.repository.TemplateChecklistRepository;
import com.tracker.service.dto.TemplateChecklistDTO;
import com.tracker.service.mapper.TemplateChecklistMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.tracker.domain.TemplateChecklist}.
 */
@Service
@Transactional
public class TemplateChecklistService {

    private final Logger log = LoggerFactory.getLogger(TemplateChecklistService.class);

    private final TemplateChecklistRepository templateChecklistRepository;

    private final TemplateChecklistMapper templateChecklistMapper;

    public TemplateChecklistService(
        TemplateChecklistRepository templateChecklistRepository,
        TemplateChecklistMapper templateChecklistMapper
    ) {
        this.templateChecklistRepository = templateChecklistRepository;
        this.templateChecklistMapper = templateChecklistMapper;
    }

    /**
     * Save a templateChecklist.
     *
     * @param templateChecklistDTO the entity to save.
     * @return the persisted entity.
     */
    public TemplateChecklistDTO save(TemplateChecklistDTO templateChecklistDTO) {
        log.debug("Request to save TemplateChecklist : {}", templateChecklistDTO);
        TemplateChecklist templateChecklist = templateChecklistMapper.toEntity(templateChecklistDTO);
        templateChecklist = templateChecklistRepository.save(templateChecklist);
        return templateChecklistMapper.toDto(templateChecklist);
    }

    /**
     * Update a templateChecklist.
     *
     * @param templateChecklistDTO the entity to save.
     * @return the persisted entity.
     */
    public TemplateChecklistDTO update(TemplateChecklistDTO templateChecklistDTO) {
        log.debug("Request to update TemplateChecklist : {}", templateChecklistDTO);
        TemplateChecklist templateChecklist = templateChecklistMapper.toEntity(templateChecklistDTO);
        templateChecklist = templateChecklistRepository.save(templateChecklist);
        return templateChecklistMapper.toDto(templateChecklist);
    }

    /**
     * Partially update a templateChecklist.
     *
     * @param templateChecklistDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TemplateChecklistDTO> partialUpdate(TemplateChecklistDTO templateChecklistDTO) {
        log.debug("Request to partially update TemplateChecklist : {}", templateChecklistDTO);

        return templateChecklistRepository
            .findById(templateChecklistDTO.getId())
            .map(existingTemplateChecklist -> {
                templateChecklistMapper.partialUpdate(existingTemplateChecklist, templateChecklistDTO);

                return existingTemplateChecklist;
            })
            .map(templateChecklistRepository::save)
            .map(templateChecklistMapper::toDto);
    }

    /**
     * Get all the templateChecklists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TemplateChecklistDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TemplateChecklists");
        return templateChecklistRepository.findAll(pageable).map(templateChecklistMapper::toDto);
    }

    /**
     * Get one templateChecklist by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TemplateChecklistDTO> findOne(Long id) {
        log.debug("Request to get TemplateChecklist : {}", id);
        return templateChecklistRepository.findById(id).map(templateChecklistMapper::toDto);
    }

    /**
     * Delete the templateChecklist by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TemplateChecklist : {}", id);
        templateChecklistRepository.deleteById(id);
    }
}
