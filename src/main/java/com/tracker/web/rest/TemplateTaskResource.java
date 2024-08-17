package com.tracker.web.rest;

import com.tracker.repository.TemplateTaskRepository;
import com.tracker.service.TemplateTaskQueryService;
import com.tracker.service.TemplateTaskService;
import com.tracker.service.criteria.TemplateTaskCriteria;
import com.tracker.service.dto.TemplateTaskDTO;
import com.tracker.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tracker.domain.TemplateTask}.
 */
@RestController
@RequestMapping("/api/template-tasks")
public class TemplateTaskResource {

    private final Logger log = LoggerFactory.getLogger(TemplateTaskResource.class);

    private static final String ENTITY_NAME = "templateTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemplateTaskService templateTaskService;

    private final TemplateTaskRepository templateTaskRepository;

    private final TemplateTaskQueryService templateTaskQueryService;

    public TemplateTaskResource(
        TemplateTaskService templateTaskService,
        TemplateTaskRepository templateTaskRepository,
        TemplateTaskQueryService templateTaskQueryService
    ) {
        this.templateTaskService = templateTaskService;
        this.templateTaskRepository = templateTaskRepository;
        this.templateTaskQueryService = templateTaskQueryService;
    }

    /**
     * {@code POST  /template-tasks} : Create a new templateTask.
     *
     * @param templateTaskDTO the templateTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new templateTaskDTO, or with status {@code 400 (Bad Request)} if the templateTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TemplateTaskDTO> createTemplateTask(@Valid @RequestBody TemplateTaskDTO templateTaskDTO)
        throws URISyntaxException {
        log.debug("REST request to save TemplateTask : {}", templateTaskDTO);
        if (templateTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new templateTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TemplateTaskDTO result = templateTaskService.save(templateTaskDTO);
        return ResponseEntity
            .created(new URI("/api/template-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /template-tasks/:id} : Updates an existing templateTask.
     *
     * @param id the id of the templateTaskDTO to save.
     * @param templateTaskDTO the templateTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateTaskDTO,
     * or with status {@code 400 (Bad Request)} if the templateTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the templateTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TemplateTaskDTO> updateTemplateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TemplateTaskDTO templateTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TemplateTask : {}, {}", id, templateTaskDTO);
        if (templateTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TemplateTaskDTO result = templateTaskService.update(templateTaskDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateTaskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /template-tasks/:id} : Partial updates given fields of an existing templateTask, field will ignore if it is null
     *
     * @param id the id of the templateTaskDTO to save.
     * @param templateTaskDTO the templateTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateTaskDTO,
     * or with status {@code 400 (Bad Request)} if the templateTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the templateTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the templateTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TemplateTaskDTO> partialUpdateTemplateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TemplateTaskDTO templateTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TemplateTask partially : {}, {}", id, templateTaskDTO);
        if (templateTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TemplateTaskDTO> result = templateTaskService.partialUpdate(templateTaskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateTaskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /template-tasks} : get all the templateTasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of templateTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TemplateTaskDTO>> getAllTemplateTasks(
        TemplateTaskCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TemplateTasks by criteria: {}", criteria);

        Page<TemplateTaskDTO> page = templateTaskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /template-tasks/count} : count all the templateTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTemplateTasks(TemplateTaskCriteria criteria) {
        log.debug("REST request to count TemplateTasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(templateTaskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /template-tasks/:id} : get the "id" templateTask.
     *
     * @param id the id of the templateTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the templateTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TemplateTaskDTO> getTemplateTask(@PathVariable("id") Long id) {
        log.debug("REST request to get TemplateTask : {}", id);
        Optional<TemplateTaskDTO> templateTaskDTO = templateTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(templateTaskDTO);
    }

    /**
     * {@code DELETE  /template-tasks/:id} : delete the "id" templateTask.
     *
     * @param id the id of the templateTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplateTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete TemplateTask : {}", id);
        templateTaskService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
