package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.TemplateChecklist;
import com.tracker.domain.TemplateTask;
import com.tracker.repository.TemplateChecklistRepository;
import com.tracker.service.dto.TemplateChecklistDTO;
import com.tracker.service.mapper.TemplateChecklistMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TemplateChecklistResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemplateChecklistResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/template-checklists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TemplateChecklistRepository templateChecklistRepository;

    @Autowired
    private TemplateChecklistMapper templateChecklistMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemplateChecklistMockMvc;

    private TemplateChecklist templateChecklist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateChecklist createEntity(EntityManager em) {
        TemplateChecklist templateChecklist = new TemplateChecklist()
            .name(DEFAULT_NAME)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
        return templateChecklist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateChecklist createUpdatedEntity(EntityManager em) {
        TemplateChecklist templateChecklist = new TemplateChecklist()
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        return templateChecklist;
    }

    @BeforeEach
    public void initTest() {
        templateChecklist = createEntity(em);
    }

    @Test
    @Transactional
    void createTemplateChecklist() throws Exception {
        int databaseSizeBeforeCreate = templateChecklistRepository.findAll().size();
        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);
        restTemplateChecklistMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeCreate + 1);
        TemplateChecklist testTemplateChecklist = templateChecklistList.get(templateChecklistList.size() - 1);
        assertThat(testTemplateChecklist.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTemplateChecklist.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTemplateChecklist.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTemplateChecklist.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTemplateChecklist.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createTemplateChecklistWithExistingId() throws Exception {
        // Create the TemplateChecklist with an existing ID
        templateChecklist.setId(1L);
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        int databaseSizeBeforeCreate = templateChecklistRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemplateChecklistMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = templateChecklistRepository.findAll().size();
        // set the field null
        templateChecklist.setName(null);

        // Create the TemplateChecklist, which fails.
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        restTemplateChecklistMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTemplateChecklists() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(templateChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getTemplateChecklist() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get the templateChecklist
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL_ID, templateChecklist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(templateChecklist.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTemplateChecklistsByIdFiltering() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        Long id = templateChecklist.getId();

        defaultTemplateChecklistShouldBeFound("id.equals=" + id);
        defaultTemplateChecklistShouldNotBeFound("id.notEquals=" + id);

        defaultTemplateChecklistShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTemplateChecklistShouldNotBeFound("id.greaterThan=" + id);

        defaultTemplateChecklistShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTemplateChecklistShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where name equals to DEFAULT_NAME
        defaultTemplateChecklistShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the templateChecklistList where name equals to UPDATED_NAME
        defaultTemplateChecklistShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTemplateChecklistShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the templateChecklistList where name equals to UPDATED_NAME
        defaultTemplateChecklistShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where name is not null
        defaultTemplateChecklistShouldBeFound("name.specified=true");

        // Get all the templateChecklistList where name is null
        defaultTemplateChecklistShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByNameContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where name contains DEFAULT_NAME
        defaultTemplateChecklistShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the templateChecklistList where name contains UPDATED_NAME
        defaultTemplateChecklistShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where name does not contain DEFAULT_NAME
        defaultTemplateChecklistShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the templateChecklistList where name does not contain UPDATED_NAME
        defaultTemplateChecklistShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTemplateChecklistShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the templateChecklistList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateChecklistShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTemplateChecklistShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the templateChecklistList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateChecklistShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdDate is not null
        defaultTemplateChecklistShouldBeFound("createdDate.specified=true");

        // Get all the templateChecklistList where createdDate is null
        defaultTemplateChecklistShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdBy equals to DEFAULT_CREATED_BY
        defaultTemplateChecklistShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the templateChecklistList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateChecklistShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTemplateChecklistShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the templateChecklistList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateChecklistShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdBy is not null
        defaultTemplateChecklistShouldBeFound("createdBy.specified=true");

        // Get all the templateChecklistList where createdBy is null
        defaultTemplateChecklistShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdBy contains DEFAULT_CREATED_BY
        defaultTemplateChecklistShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the templateChecklistList where createdBy contains UPDATED_CREATED_BY
        defaultTemplateChecklistShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTemplateChecklistShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the templateChecklistList where createdBy does not contain UPDATED_CREATED_BY
        defaultTemplateChecklistShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedDate equals to DEFAULT_MODIFIED_DATE
        defaultTemplateChecklistShouldBeFound("modifiedDate.equals=" + DEFAULT_MODIFIED_DATE);

        // Get all the templateChecklistList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateChecklistShouldNotBeFound("modifiedDate.equals=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedDate in DEFAULT_MODIFIED_DATE or UPDATED_MODIFIED_DATE
        defaultTemplateChecklistShouldBeFound("modifiedDate.in=" + DEFAULT_MODIFIED_DATE + "," + UPDATED_MODIFIED_DATE);

        // Get all the templateChecklistList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateChecklistShouldNotBeFound("modifiedDate.in=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedDate is not null
        defaultTemplateChecklistShouldBeFound("modifiedDate.specified=true");

        // Get all the templateChecklistList where modifiedDate is null
        defaultTemplateChecklistShouldNotBeFound("modifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedBy equals to DEFAULT_MODIFIED_BY
        defaultTemplateChecklistShouldBeFound("modifiedBy.equals=" + DEFAULT_MODIFIED_BY);

        // Get all the templateChecklistList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateChecklistShouldNotBeFound("modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedBy in DEFAULT_MODIFIED_BY or UPDATED_MODIFIED_BY
        defaultTemplateChecklistShouldBeFound("modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY);

        // Get all the templateChecklistList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateChecklistShouldNotBeFound("modifiedBy.in=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedBy is not null
        defaultTemplateChecklistShouldBeFound("modifiedBy.specified=true");

        // Get all the templateChecklistList where modifiedBy is null
        defaultTemplateChecklistShouldNotBeFound("modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedBy contains DEFAULT_MODIFIED_BY
        defaultTemplateChecklistShouldBeFound("modifiedBy.contains=" + DEFAULT_MODIFIED_BY);

        // Get all the templateChecklistList where modifiedBy contains UPDATED_MODIFIED_BY
        defaultTemplateChecklistShouldNotBeFound("modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        // Get all the templateChecklistList where modifiedBy does not contain DEFAULT_MODIFIED_BY
        defaultTemplateChecklistShouldNotBeFound("modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY);

        // Get all the templateChecklistList where modifiedBy does not contain UPDATED_MODIFIED_BY
        defaultTemplateChecklistShouldBeFound("modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateChecklistsByTaskIsEqualToSomething() throws Exception {
        TemplateTask task;
        if (TestUtil.findAll(em, TemplateTask.class).isEmpty()) {
            templateChecklistRepository.saveAndFlush(templateChecklist);
            task = TemplateTaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, TemplateTask.class).get(0);
        }
        em.persist(task);
        em.flush();
        templateChecklist.setTask(task);
        templateChecklistRepository.saveAndFlush(templateChecklist);
        Long taskId = task.getId();
        // Get all the templateChecklistList where task equals to taskId
        defaultTemplateChecklistShouldBeFound("taskId.equals=" + taskId);

        // Get all the templateChecklistList where task equals to (taskId + 1)
        defaultTemplateChecklistShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemplateChecklistShouldBeFound(String filter) throws Exception {
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(templateChecklist.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemplateChecklistShouldNotBeFound(String filter) throws Exception {
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemplateChecklistMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemplateChecklist() throws Exception {
        // Get the templateChecklist
        restTemplateChecklistMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTemplateChecklist() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();

        // Update the templateChecklist
        TemplateChecklist updatedTemplateChecklist = templateChecklistRepository.findById(templateChecklist.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTemplateChecklist are not directly saved in db
        em.detach(updatedTemplateChecklist);
        updatedTemplateChecklist
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(updatedTemplateChecklist);

        restTemplateChecklistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateChecklistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isOk());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
        TemplateChecklist testTemplateChecklist = templateChecklistList.get(templateChecklistList.size() - 1);
        assertThat(testTemplateChecklist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplateChecklist.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplateChecklist.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplateChecklist.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateChecklist.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateChecklistDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemplateChecklistWithPatch() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();

        // Update the templateChecklist using partial update
        TemplateChecklist partialUpdatedTemplateChecklist = new TemplateChecklist();
        partialUpdatedTemplateChecklist.setId(templateChecklist.getId());

        partialUpdatedTemplateChecklist
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTemplateChecklistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateChecklist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplateChecklist))
            )
            .andExpect(status().isOk());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
        TemplateChecklist testTemplateChecklist = templateChecklistList.get(templateChecklistList.size() - 1);
        assertThat(testTemplateChecklist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplateChecklist.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplateChecklist.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplateChecklist.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateChecklist.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTemplateChecklistWithPatch() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();

        // Update the templateChecklist using partial update
        TemplateChecklist partialUpdatedTemplateChecklist = new TemplateChecklist();
        partialUpdatedTemplateChecklist.setId(templateChecklist.getId());

        partialUpdatedTemplateChecklist
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTemplateChecklistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateChecklist.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplateChecklist))
            )
            .andExpect(status().isOk());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
        TemplateChecklist testTemplateChecklist = templateChecklistList.get(templateChecklistList.size() - 1);
        assertThat(testTemplateChecklist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplateChecklist.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplateChecklist.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplateChecklist.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateChecklist.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, templateChecklistDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemplateChecklist() throws Exception {
        int databaseSizeBeforeUpdate = templateChecklistRepository.findAll().size();
        templateChecklist.setId(longCount.incrementAndGet());

        // Create the TemplateChecklist
        TemplateChecklistDTO templateChecklistDTO = templateChecklistMapper.toDto(templateChecklist);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateChecklistMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateChecklistDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateChecklist in the database
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemplateChecklist() throws Exception {
        // Initialize the database
        templateChecklistRepository.saveAndFlush(templateChecklist);

        int databaseSizeBeforeDelete = templateChecklistRepository.findAll().size();

        // Delete the templateChecklist
        restTemplateChecklistMockMvc
            .perform(delete(ENTITY_API_URL_ID, templateChecklist.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TemplateChecklist> templateChecklistList = templateChecklistRepository.findAll();
        assertThat(templateChecklistList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
