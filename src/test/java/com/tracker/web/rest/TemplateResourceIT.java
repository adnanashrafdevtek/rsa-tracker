package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Template;
import com.tracker.domain.TemplateTask;
import com.tracker.repository.TemplateRepository;
import com.tracker.service.dto.TemplateDTO;
import com.tracker.service.mapper.TemplateMapper;
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
 * Integration tests for the {@link TemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TemplateResourceIT {

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

    private static final String ENTITY_API_URL = "/api/templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemplateMockMvc;

    private Template template;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Template createEntity(EntityManager em) {
        Template template = new Template()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
        return template;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Template createUpdatedEntity(EntityManager em) {
        Template template = new Template()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        return template;
    }

    @BeforeEach
    public void initTest() {
        template = createEntity(em);
    }

    @Test
    @Transactional
    void createTemplate() throws Exception {
        int databaseSizeBeforeCreate = templateRepository.findAll().size();
        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);
        restTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateDTO)))
            .andExpect(status().isCreated());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeCreate + 1);
        Template testTemplate = templateList.get(templateList.size() - 1);
        assertThat(testTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTemplate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTemplate.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTemplate.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTemplate.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTemplate.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createTemplateWithExistingId() throws Exception {
        // Create the Template with an existing ID
        template.setId(1L);
        TemplateDTO templateDTO = templateMapper.toDto(template);

        int databaseSizeBeforeCreate = templateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = templateRepository.findAll().size();
        // set the field null
        template.setName(null);

        // Create the Template, which fails.
        TemplateDTO templateDTO = templateMapper.toDto(template);

        restTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateDTO)))
            .andExpect(status().isBadRequest());

        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = templateRepository.findAll().size();
        // set the field null
        template.setDescription(null);

        // Create the Template, which fails.
        TemplateDTO templateDTO = templateMapper.toDto(template);

        restTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateDTO)))
            .andExpect(status().isBadRequest());

        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTemplates() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(template.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getTemplate() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get the template
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, template.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(template.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTemplatesByIdFiltering() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        Long id = template.getId();

        defaultTemplateShouldBeFound("id.equals=" + id);
        defaultTemplateShouldNotBeFound("id.notEquals=" + id);

        defaultTemplateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTemplateShouldNotBeFound("id.greaterThan=" + id);

        defaultTemplateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTemplateShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTemplatesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where name equals to DEFAULT_NAME
        defaultTemplateShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the templateList where name equals to UPDATED_NAME
        defaultTemplateShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplatesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTemplateShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the templateList where name equals to UPDATED_NAME
        defaultTemplateShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplatesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where name is not null
        defaultTemplateShouldBeFound("name.specified=true");

        // Get all the templateList where name is null
        defaultTemplateShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByNameContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where name contains DEFAULT_NAME
        defaultTemplateShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the templateList where name contains UPDATED_NAME
        defaultTemplateShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplatesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where name does not contain DEFAULT_NAME
        defaultTemplateShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the templateList where name does not contain UPDATED_NAME
        defaultTemplateShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTemplatesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where description equals to DEFAULT_DESCRIPTION
        defaultTemplateShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the templateList where description equals to UPDATED_DESCRIPTION
        defaultTemplateShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplatesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTemplateShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the templateList where description equals to UPDATED_DESCRIPTION
        defaultTemplateShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplatesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where description is not null
        defaultTemplateShouldBeFound("description.specified=true");

        // Get all the templateList where description is null
        defaultTemplateShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where description contains DEFAULT_DESCRIPTION
        defaultTemplateShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the templateList where description contains UPDATED_DESCRIPTION
        defaultTemplateShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplatesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where description does not contain DEFAULT_DESCRIPTION
        defaultTemplateShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the templateList where description does not contain UPDATED_DESCRIPTION
        defaultTemplateShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTemplateShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the templateList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTemplateShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the templateList where createdDate equals to UPDATED_CREATED_DATE
        defaultTemplateShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdDate is not null
        defaultTemplateShouldBeFound("createdDate.specified=true");

        // Get all the templateList where createdDate is null
        defaultTemplateShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdBy equals to DEFAULT_CREATED_BY
        defaultTemplateShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the templateList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTemplateShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the templateList where createdBy equals to UPDATED_CREATED_BY
        defaultTemplateShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdBy is not null
        defaultTemplateShouldBeFound("createdBy.specified=true");

        // Get all the templateList where createdBy is null
        defaultTemplateShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdBy contains DEFAULT_CREATED_BY
        defaultTemplateShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the templateList where createdBy contains UPDATED_CREATED_BY
        defaultTemplateShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTemplateShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the templateList where createdBy does not contain UPDATED_CREATED_BY
        defaultTemplateShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedDate equals to DEFAULT_MODIFIED_DATE
        defaultTemplateShouldBeFound("modifiedDate.equals=" + DEFAULT_MODIFIED_DATE);

        // Get all the templateList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateShouldNotBeFound("modifiedDate.equals=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedDate in DEFAULT_MODIFIED_DATE or UPDATED_MODIFIED_DATE
        defaultTemplateShouldBeFound("modifiedDate.in=" + DEFAULT_MODIFIED_DATE + "," + UPDATED_MODIFIED_DATE);

        // Get all the templateList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTemplateShouldNotBeFound("modifiedDate.in=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedDate is not null
        defaultTemplateShouldBeFound("modifiedDate.specified=true");

        // Get all the templateList where modifiedDate is null
        defaultTemplateShouldNotBeFound("modifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedBy equals to DEFAULT_MODIFIED_BY
        defaultTemplateShouldBeFound("modifiedBy.equals=" + DEFAULT_MODIFIED_BY);

        // Get all the templateList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateShouldNotBeFound("modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedBy in DEFAULT_MODIFIED_BY or UPDATED_MODIFIED_BY
        defaultTemplateShouldBeFound("modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY);

        // Get all the templateList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTemplateShouldNotBeFound("modifiedBy.in=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedBy is not null
        defaultTemplateShouldBeFound("modifiedBy.specified=true");

        // Get all the templateList where modifiedBy is null
        defaultTemplateShouldNotBeFound("modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedBy contains DEFAULT_MODIFIED_BY
        defaultTemplateShouldBeFound("modifiedBy.contains=" + DEFAULT_MODIFIED_BY);

        // Get all the templateList where modifiedBy contains UPDATED_MODIFIED_BY
        defaultTemplateShouldNotBeFound("modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        // Get all the templateList where modifiedBy does not contain DEFAULT_MODIFIED_BY
        defaultTemplateShouldNotBeFound("modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY);

        // Get all the templateList where modifiedBy does not contain UPDATED_MODIFIED_BY
        defaultTemplateShouldBeFound("modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTemplatesByTasksIsEqualToSomething() throws Exception {
        TemplateTask tasks;
        if (TestUtil.findAll(em, TemplateTask.class).isEmpty()) {
            templateRepository.saveAndFlush(template);
            tasks = TemplateTaskResourceIT.createEntity(em);
        } else {
            tasks = TestUtil.findAll(em, TemplateTask.class).get(0);
        }
        em.persist(tasks);
        em.flush();
        template.addTasks(tasks);
        templateRepository.saveAndFlush(template);
        Long tasksId = tasks.getId();
        // Get all the templateList where tasks equals to tasksId
        defaultTemplateShouldBeFound("tasksId.equals=" + tasksId);

        // Get all the templateList where tasks equals to (tasksId + 1)
        defaultTemplateShouldNotBeFound("tasksId.equals=" + (tasksId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemplateShouldBeFound(String filter) throws Exception {
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(template.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemplateShouldNotBeFound(String filter) throws Exception {
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemplateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemplate() throws Exception {
        // Get the template
        restTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTemplate() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        int databaseSizeBeforeUpdate = templateRepository.findAll().size();

        // Update the template
        Template updatedTemplate = templateRepository.findById(template.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTemplate are not directly saved in db
        em.detach(updatedTemplate);
        updatedTemplate
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TemplateDTO templateDTO = templateMapper.toDto(updatedTemplate);

        restTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
        Template testTemplate = templateList.get(templateList.size() - 1);
        assertThat(testTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemplate.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplate.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplate.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplate.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, templateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(templateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemplateWithPatch() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        int databaseSizeBeforeUpdate = templateRepository.findAll().size();

        // Update the template using partial update
        Template partialUpdatedTemplate = new Template();
        partialUpdatedTemplate.setId(template.getId());

        partialUpdatedTemplate.createdDate(UPDATED_CREATED_DATE).modifiedDate(UPDATED_MODIFIED_DATE);

        restTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplate))
            )
            .andExpect(status().isOk());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
        Template testTemplate = templateList.get(templateList.size() - 1);
        assertThat(testTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTemplate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTemplate.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplate.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTemplate.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplate.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTemplateWithPatch() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        int databaseSizeBeforeUpdate = templateRepository.findAll().size();

        // Update the template using partial update
        Template partialUpdatedTemplate = new Template();
        partialUpdatedTemplate.setId(template.getId());

        partialUpdatedTemplate
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemplate))
            )
            .andExpect(status().isOk());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
        Template testTemplate = templateList.get(templateList.size() - 1);
        assertThat(testTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTemplate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTemplate.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTemplate.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTemplate.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTemplate.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, templateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemplate() throws Exception {
        int databaseSizeBeforeUpdate = templateRepository.findAll().size();
        template.setId(longCount.incrementAndGet());

        // Create the Template
        TemplateDTO templateDTO = templateMapper.toDto(template);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(templateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Template in the database
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemplate() throws Exception {
        // Initialize the database
        templateRepository.saveAndFlush(template);

        int databaseSizeBeforeDelete = templateRepository.findAll().size();

        // Delete the template
        restTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, template.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Template> templateList = templateRepository.findAll();
        assertThat(templateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
