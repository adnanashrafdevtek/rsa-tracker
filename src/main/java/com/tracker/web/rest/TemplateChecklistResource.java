package com.tracker.web.rest;

import com.tracker.repository.TemplateChecklistRepository;
import com.tracker.service.TemplateChecklistQueryService;
import com.tracker.service.TemplateChecklistService;
import com.tracker.service.criteria.TemplateChecklistCriteria;
import com.tracker.service.dto.TemplateChecklistDTO;
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
 * REST controller for managing {@link com.tracker.domain.TemplateChecklist}.
 */
@RestController
@RequestMapping("/api/template-checklists")
public class TemplateChecklistResource {

    private final Logger log = LoggerFactory.getLogger(TemplateChecklistResource.class);

    private static final String ENTITY_NAME = "templateChecklist";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemplateChecklistService templateChecklistService;

    private final TemplateChecklistRepository templateChecklistRepository;

    private final TemplateChecklistQueryService templateChecklistQueryService;

    public TemplateChecklistResource(
        TemplateChecklistService templateChecklistService,
        TemplateChecklistRepository templateChecklistRepository,
        TemplateChecklistQueryService templateChecklistQueryService
    ) {
        this.templateChecklistService = templateChecklistService;
        this.templateChecklistRepository = templateChecklistRepository;
        this.templateChecklistQueryService = templateChecklistQueryService;
    }

    /**
     * {@code POST  /template-checklists} : Create a new templateChecklist.
     *
     * @param templateChecklistDTO the templateChecklistDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new templateChecklistDTO, or with status {@code 400 (Bad Request)} if the templateChecklist has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TemplateChecklistDTO> createTemplateChecklist(@Valid @RequestBody TemplateChecklistDTO templateChecklistDTO)
        throws URISyntaxException {
        log.debug("REST request to save TemplateChecklist : {}", templateChecklistDTO);
        if (templateChecklistDTO.getId() != null) {
            throw new BadRequestAlertException("A new templateChecklist cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TemplateChecklistDTO result = templateChecklistService.save(templateChecklistDTO);
        return ResponseEntity
            .created(new URI("/api/template-checklists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /template-checklists/:id} : Updates an existing templateChecklist.
     *
     * @param id the id of the templateChecklistDTO to save.
     * @param templateChecklistDTO the templateChecklistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateChecklistDTO,
     * or with status {@code 400 (Bad Request)} if the templateChecklistDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the templateChecklistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TemplateChecklistDTO> updateTemplateChecklist(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TemplateChecklistDTO templateChecklistDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TemplateChecklist : {}, {}", id, templateChecklistDTO);
        if (templateChecklistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateChecklistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateChecklistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TemplateChecklistDTO result = templateChecklistService.update(templateChecklistDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateChecklistDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /template-checklists/:id} : Partial updates given fields of an existing templateChecklist, field will ignore if it is null
     *
     * @param id the id of the templateChecklistDTO to save.
     * @param templateChecklistDTO the templateChecklistDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated templateChecklistDTO,
     * or with status {@code 400 (Bad Request)} if the templateChecklistDTO is not valid,
     * or with status {@code 404 (Not Found)} if the templateChecklistDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the templateChecklistDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TemplateChecklistDTO> partialUpdateTemplateChecklist(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TemplateChecklistDTO templateChecklistDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TemplateChecklist partially : {}, {}", id, templateChecklistDTO);
        if (templateChecklistDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, templateChecklistDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!templateChecklistRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TemplateChecklistDTO> result = templateChecklistService.partialUpdate(templateChecklistDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, templateChecklistDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /template-checklists} : get all the templateChecklists.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of templateChecklists in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TemplateChecklistDTO>> getAllTemplateChecklists(
        TemplateChecklistCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TemplateChecklists by criteria: {}", criteria);

        Page<TemplateChecklistDTO> page = templateChecklistQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /template-checklists/count} : count all the templateChecklists.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTemplateChecklists(TemplateChecklistCriteria criteria) {
        log.debug("REST request to count TemplateChecklists by criteria: {}", criteria);
        return ResponseEntity.ok().body(templateChecklistQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /template-checklists/:id} : get the "id" templateChecklist.
     *
     * @param id the id of the templateChecklistDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the templateChecklistDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TemplateChecklistDTO> getTemplateChecklist(@PathVariable("id") Long id) {
        log.debug("REST request to get TemplateChecklist : {}", id);
        Optional<TemplateChecklistDTO> templateChecklistDTO = templateChecklistService.findOne(id);
        return ResponseUtil.wrapOrNotFound(templateChecklistDTO);
    }

    /**
     * {@code DELETE  /template-checklists/:id} : delete the "id" templateChecklist.
     *
     * @param id the id of the templateChecklistDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplateChecklist(@PathVariable("id") Long id) {
        log.debug("REST request to delete TemplateChecklist : {}", id);
        templateChecklistService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
