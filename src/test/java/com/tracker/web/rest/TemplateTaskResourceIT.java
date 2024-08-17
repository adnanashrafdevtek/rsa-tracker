package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Template;
import com.tracker.domain.TemplateChecklist;
import com.tracker.domain.TemplateTask;
import com.tracker.repository.TemplateTaskRepository;
import com.tracker.service.dto.TemplateTaskDTO;
import com.tracker.service.mapper.TemplateTaskMapper;
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
 * Integration tests for the {@link TemplateTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemplateTaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/template-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TemplateTaskRepository templateTaskRepository;

    @Autowired
    private TemplateTaskMapper templateTaskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemplateTaskMockMvc;

    private TemplateTask templateTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateTask createEntity(EntityManager em) {
        TemplateTask templateTask = new TemplateTask()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
        return templateTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TemplateTask createUpdatedEntity(EntityManager em) {
        TemplateTask templateTask = new TemplateTask()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        return templateTask;
    }

    @BeforeEach
    public void initTest() {
        templateTask = createEntity(em);
    }

    @Test
    @Transactional
    void createTemplateTask() throws Exception {
        int databaseSizeBeforeCreate = templateTaskRepository.findAll().size();
        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);
        restTemplateTaskMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeCreate + 1);
        TemplateTask testTemplateTask = templateTaskList.get(templateTaskList.size() - 1);
        assertThat(testTemplateTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTemplateTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTemplateTask.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTemplateTask.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTemplateTask.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTemplateTask.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createTemplateTaskWithExistingId() throws Exception {
        // Create the TemplateTask with an existing ID
        templateTask.setId(1L);
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        int databaseSizeBeforeCreate = templateTaskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemplateTaskMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = templateTaskRepository.findAll().size();
        // set the field null
        templateTask.setName(null);

        // Create the TemplateTask, which fails.
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        restTemplateTaskMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTemplateTasks() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(templateTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getTemplateTask() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get the templateTask
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, templateTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(templateTask.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTemplateTasksByIdFiltering() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        Long id = templateTask.getId();

        defaultTemplateTaskShouldBeFound("id.equals=" + id);
        defaultTemplateTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTemplateTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTemplateTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTemplateTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTemplateTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where name equals to DEFAULT_NAME
        defaultTemplateTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the templateTaskList where name equals to UPDATED_NAME
        defaultTemplateTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTemplateTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the templateTaskList where name equals to UPDATED_NAME
        defaultTemplateTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where name is not null
        defaultTemplateTaskShouldBeFound("name.specified=true");

        // Get all the templateTaskList where name is null
        defaultTemplateTaskShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where name contains DEFAULT_NAME
        defaultTemplateTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the templateTaskList where name contains UPDATED_NAME
        defaultTemplateTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where name does not contain DEFAULT_NAME
        defaultTemplateTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the templateTaskList where name does not contain UPDATED_NAME
        defaultTemplateTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where description equals to DEFAULT_DESCRIPTION
        defaultTemplateTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the templateTaskList where description equals to UPDATED_DESCRIPTION
        defaultTemplateTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTemplateTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the templateTaskList where description equals to UPDATED_DESCRIPTION
        defaultTemplateTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where description is not null
        defaultTemplateTaskShouldBeFound("description.specified=true");

        // Get all the templateTaskList where description is null
        defaultTemplateTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where description contains DEFAULT_DESCRIPTION
        defaultTemplateTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the templateTaskList where description contains UPDATED_DESCRIPTION
        defaultTemplateTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where description does not contain DEFAULT_DESCRIPTION
        defaultTemplateTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the templateTaskList where description does not contain UPDATED_DESCRIPTION
        defaultTemplateTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTemplateTaskShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the templateTaskList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateTaskShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTemplateTaskShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the templateTaskList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateTaskShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdDate is not null
        defaultTemplateTaskShouldBeFound("createdDate.specified=true");

        // Get all the templateTaskList where createdDate is null
        defaultTemplateTaskShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdBy equals to DEFAULT_CREATED_BY
        defaultTemplateTaskShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the templateTaskList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateTaskShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTemplateTaskShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the templateTaskList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateTaskShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdBy is not null
        defaultTemplateTaskShouldBeFound("createdBy.specified=true");

        // Get all the templateTaskList where createdBy is null
        defaultTemplateTaskShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdBy contains DEFAULT_CREATED_BY
        defaultTemplateTaskShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the templateTaskList where createdBy contains UPDATED_CREATED_BY
        defaultTemplateTaskShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTemplateTaskShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the templateTaskList where createdBy does not contain UPDATED_CREATED_BY
        defaultTemplateTaskShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedDate equals to DEFAULT_MODIFIED_DATE
        defaultTemplateTaskShouldBeFound("modifiedDate.equals=" + DEFAULT_MODIFIED_DATE);

        // Get all the templateTaskList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateTaskShouldNotBeFound("modifiedDate.equals=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedDate in DEFAULT_MODIFIED_DATE or UPDATED_MODIFIED_DATE
        defaultTemplateTaskShouldBeFound("modifiedDate.in=" + DEFAULT_MODIFIED_DATE + "," + UPDATED_MODIFIED_DATE);

        // Get all the templateTaskList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateTaskShouldNotBeFound("modifiedDate.in=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedDate is not null
        defaultTemplateTaskShouldBeFound("modifiedDate.specified=true");

        // Get all the templateTaskList where modifiedDate is null
        defaultTemplateTaskShouldNotBeFound("modifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedBy equals to DEFAULT_MODIFIED_BY
        defaultTemplateTaskShouldBeFound("modifiedBy.equals=" + DEFAULT_MODIFIED_BY);

        // Get all the templateTaskList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateTaskShouldNotBeFound("modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedBy in DEFAULT_MODIFIED_BY or UPDATED_MODIFIED_BY
        defaultTemplateTaskShouldBeFound("modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY);

        // Get all the templateTaskList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateTaskShouldNotBeFound("modifiedBy.in=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedBy is not null
        defaultTemplateTaskShouldBeFound("modifiedBy.specified=true");

        // Get all the templateTaskList where modifiedBy is null
        defaultTemplateTaskShouldNotBeFound("modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedBy contains DEFAULT_MODIFIED_BY
        defaultTemplateTaskShouldBeFound("modifiedBy.contains=" + DEFAULT_MODIFIED_BY);

        // Get all the templateTaskList where modifiedBy contains UPDATED_MODIFIED_BY
        defaultTemplateTaskShouldNotBeFound("modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        // Get all the templateTaskList where modifiedBy does not contain DEFAULT_MODIFIED_BY
        defaultTemplateTaskShouldNotBeFound("modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY);

        // Get all the templateTaskList where modifiedBy does not contain UPDATED_MODIFIED_BY
        defaultTemplateTaskShouldBeFound("modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplateTasksByTemplateIsEqualToSomething() throws Exception {
        Template template;
        if (TestUtil.findAll(em, Template.class).isEmpty()) {
            templateTaskRepository.saveAndFlush(templateTask);
            template = TemplateResourceIT.createEntity(em);
        } else {
            template = TestUtil.findAll(em, Template.class).get(0);
        }
        em.persist(template);
        em.flush();
        templateTask.setTemplate(template);
        templateTaskRepository.saveAndFlush(templateTask);
        Long templateId = template.getId();
        // Get all the templateTaskList where template equals to templateId
        defaultTemplateTaskShouldBeFound("templateId.equals=" + templateId);

        // Get all the templateTaskList where template equals to (templateId + 1)
        defaultTemplateTaskShouldNotBeFound("templateId.equals=" + (templateId + 1));
    }

    @Test
    @Transactional
    void getAllTemplateTasksByChecklistIsEqualToSomething() throws Exception {
        TemplateChecklist checklist;
        if (TestUtil.findAll(em, TemplateChecklist.class).isEmpty()) {
            templateTaskRepository.saveAndFlush(templateTask);
            checklist = TemplateChecklistResourceIT.createEntity(em);
        } else {
            checklist = TestUtil.findAll(em, TemplateChecklist.class).get(0);
        }
        em.persist(checklist);
        em.flush();
        templateTask.addChecklist(checklist);
        templateTaskRepository.saveAndFlush(templateTask);
        Long checklistId = checklist.getId();
        // Get all the templateTaskList where checklist equals to checklistId
        defaultTemplateTaskShouldBeFound("checklistId.equals=" + checklistId);

        // Get all the templateTaskList where checklist equals to (checklistId + 1)
        defaultTemplateTaskShouldNotBeFound("checklistId.equals=" + (checklistId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemplateTaskShouldBeFound(String filter) throws Exception {
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(templateTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemplateTaskShouldNotBeFound(String filter) throws Exception {
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemplateTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemplateTask() throws Exception {
        // Get the templateTask
        restTemplateTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTemplateTask() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();

        // Update the templateTask
        TemplateTask updatedTemplateTask = templateTaskRepository.findById(templateTask.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTemplateTask are not directly saved in db
        em.detach(updatedTemplateTask);
        updatedTemplateTask
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(updatedTemplateTask);

        restTemplateTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isOk());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
        TemplateTask testTemplateTask = templateTaskList.get(templateTaskList.size() - 1);
        assertThat(testTemplateTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplateTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemplateTask.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplateTask.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplateTask.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateTask.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemplateTaskWithPatch() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();

        // Update the templateTask using partial update
        TemplateTask partialUpdatedTemplateTask = new TemplateTask();
        partialUpdatedTemplateTask.setId(templateTask.getId());

        partialUpdatedTemplateTask.modifiedDate(UPDATED_MODIFIED_DATE).modifiedBy(UPDATED_MODIFIED_BY);

        restTemplateTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplateTask))
            )
            .andExpect(status().isOk());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
        TemplateTask testTemplateTask = templateTaskList.get(templateTaskList.size() - 1);
        assertThat(testTemplateTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTemplateTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTemplateTask.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTemplateTask.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTemplateTask.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateTask.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTemplateTaskWithPatch() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();

        // Update the templateTask using partial update
        TemplateTask partialUpdatedTemplateTask = new TemplateTask();
        partialUpdatedTemplateTask.setId(templateTask.getId());

        partialUpdatedTemplateTask
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTemplateTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplateTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplateTask))
            )
            .andExpect(status().isOk());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
        TemplateTask testTemplateTask = templateTaskList.get(templateTaskList.size() - 1);
        assertThat(testTemplateTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplateTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemplateTask.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplateTask.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplateTask.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplateTask.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, templateTaskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemplateTask() throws Exception {
        int databaseSizeBeforeUpdate = templateTaskRepository.findAll().size();
        templateTask.setId(longCount.incrementAndGet());

        // Create the TemplateTask
        TemplateTaskDTO templateTaskDTO = templateTaskMapper.toDto(templateTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateTaskMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateTaskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TemplateTask in the database
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemplateTask() throws Exception {
        // Initialize the database
        templateTaskRepository.saveAndFlush(templateTask);

        int databaseSizeBeforeDelete = templateTaskRepository.findAll().size();

        // Delete the templateTask
        restTemplateTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, templateTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TemplateTask> templateTaskList = templateTaskRepository.findAll();
        assertThat(templateTaskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
