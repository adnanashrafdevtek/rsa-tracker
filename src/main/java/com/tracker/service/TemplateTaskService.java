package com.tracker.service;

import com.tracker.domain.TemplateTask;
import com.tracker.repository.TemplateTaskRepository;
import com.tracker.service.dto.TemplateTaskDTO;
import com.tracker.service.mapper.TemplateTaskMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.tracker.domain.TemplateTask}.
 */
@Service
@Transactional
public class TemplateTaskService {

    private final Logger log = LoggerFactory.getLogger(TemplateTaskService.class);

    private final TemplateTaskRepository templateTaskRepository;

    private final TemplateTaskMapper templateTaskMapper;

    public TemplateTaskService(TemplateTaskRepository templateTaskRepository, TemplateTaskMapper templateTaskMapper) {
        this.templateTaskRepository = templateTaskRepository;
        this.templateTaskMapper = templateTaskMapper;
    }

    /**
     * Save a templateTask.
     *
     * @param templateTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public TemplateTaskDTO save(TemplateTaskDTO templateTaskDTO) {
        log.debug("Request to save TemplateTask : {}", templateTaskDTO);
        TemplateTask templateTask = templateTaskMapper.toEntity(templateTaskDTO);
        templateTask = templateTaskRepository.save(templateTask);
        return templateTaskMapper.toDto(templateTask);
    }

    /**
     * Update a templateTask.
     *
     * @param templateTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public TemplateTaskDTO update(TemplateTaskDTO templateTaskDTO) {
        log.debug("Request to update TemplateTask : {}", templateTaskDTO);
        TemplateTask templateTask = templateTaskMapper.toEntity(templateTaskDTO);
        templateTask = templateTaskRepository.save(templateTask);
        return templateTaskMapper.toDto(templateTask);
    }

    /**
     * Partially update a templateTask.
     *
     * @param templateTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TemplateTaskDTO> partialUpdate(TemplateTaskDTO templateTaskDTO) {
        log.debug("Request to partially update TemplateTask : {}", templateTaskDTO);

        return templateTaskRepository
            .findById(templateTaskDTO.getId())
            .map(existingTemplateTask -> {
                templateTaskMapper.partialUpdate(existingTemplateTask, templateTaskDTO);

                return existingTemplateTask;
            })
            .map(templateTaskRepository::save)
            .map(templateTaskMapper::toDto);
    }

    /**
     * Get all the templateTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TemplateTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TemplateTasks");
        return templateTaskRepository.findAll(pageable).map(templateTaskMapper::toDto);
    }

    /**
     * Get one templateTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TemplateTaskDTO> findOne(Long id) {
        log.debug("Request to get TemplateTask : {}", id);
        return templateTaskRepository.findById(id).map(templateTaskMapper::toDto);
    }

    /**
     * Delete the templateTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TemplateTask : {}", id);
        templateTaskRepository.deleteById(id);
    }
}
