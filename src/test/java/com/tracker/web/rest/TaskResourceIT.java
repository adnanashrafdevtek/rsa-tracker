package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Task;
import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.domain.enumeration.Priority;
import com.tracker.domain.enumeration.TaskStatus;
import com.tracker.repository.TaskRepository;
import com.tracker.service.dto.TaskDTO;
import com.tracker.service.mapper.TaskMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Priority DEFAULT_PRIORITY = Priority.HIGH;
    private static final Priority UPDATED_PRIORITY = Priority.MEDIUM;

    private static final TaskStatus DEFAULT_STATUS = TaskStatus.ACTIVE;
    private static final TaskStatus UPDATED_STATUS = TaskStatus.COMPLETED;

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DUE_DATE = LocalDate.ofEpochDay(-1L);

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .priority(DEFAULT_PRIORITY)
            .status(DEFAULT_STATUS)
            .dueDate(DEFAULT_DUE_DATE)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTask.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTask.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTask.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTask.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setName(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setDescription(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriorityIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setPriority(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setStatus(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setDueDate(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name equals to DEFAULT_NAME
        defaultTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name is not null
        defaultTaskShouldBeFound("name.specified=true");

        // Get all the taskList where name is null
        defaultTaskShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name contains DEFAULT_NAME
        defaultTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskList where name contains UPDATED_NAME
        defaultTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name does not contain DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskList where name does not contain UPDATED_NAME
        defaultTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskShouldBeFound("description.specified=true");

        // Get all the taskList where description is null
        defaultTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description contains UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description does not contain UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority equals to DEFAULT_PRIORITY
        defaultTaskShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority equals to UPDATED_PRIORITY
        defaultTaskShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultTaskShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the taskList where priority equals to UPDATED_PRIORITY
        defaultTaskShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is not null
        defaultTaskShouldBeFound("priority.specified=true");

        // Get all the taskList where priority is null
        defaultTaskShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status equals to DEFAULT_STATUS
        defaultTaskShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the taskList where status equals to UPDATED_STATUS
        defaultTaskShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTaskShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the taskList where status equals to UPDATED_STATUS
        defaultTaskShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where status is not null
        defaultTaskShouldBeFound("status.specified=true");

        // Get all the taskList where status is null
        defaultTaskShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate equals to DEFAULT_DUE_DATE
        defaultTaskShouldBeFound("dueDate.equals=" + DEFAULT_DUE_DATE);

        // Get all the taskList where dueDate equals to UPDATED_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate in DEFAULT_DUE_DATE or UPDATED_DUE_DATE
        defaultTaskShouldBeFound("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE);

        // Get all the taskList where dueDate equals to UPDATED_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate is not null
        defaultTaskShouldBeFound("dueDate.specified=true");

        // Get all the taskList where dueDate is null
        defaultTaskShouldNotBeFound("dueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate is greater than or equal to DEFAULT_DUE_DATE
        defaultTaskShouldBeFound("dueDate.greaterThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the taskList where dueDate is greater than or equal to UPDATED_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.greaterThanOrEqual=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate is less than or equal to DEFAULT_DUE_DATE
        defaultTaskShouldBeFound("dueDate.lessThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the taskList where dueDate is less than or equal to SMALLER_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.lessThanOrEqual=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate is less than DEFAULT_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.lessThan=" + DEFAULT_DUE_DATE);

        // Get all the taskList where dueDate is less than UPDATED_DUE_DATE
        defaultTaskShouldBeFound("dueDate.lessThan=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByDueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where dueDate is greater than DEFAULT_DUE_DATE
        defaultTaskShouldNotBeFound("dueDate.greaterThan=" + DEFAULT_DUE_DATE);

        // Get all the taskList where dueDate is greater than SMALLER_DUE_DATE
        defaultTaskShouldBeFound("dueDate.greaterThan=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTaskShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the taskList where createdDate equals to UPDATED_CREATED_DATE
        defaultTaskShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTaskShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the taskList where createdDate equals to UPDATED_CREATED_DATE
        defaultTaskShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdDate is not null
        defaultTaskShouldBeFound("createdDate.specified=true");

        // Get all the taskList where createdDate is null
        defaultTaskShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdBy equals to DEFAULT_CREATED_BY
        defaultTaskShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the taskList where createdBy equals to UPDATED_CREATED_BY
        defaultTaskShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTaskShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the taskList where createdBy equals to UPDATED_CREATED_BY
        defaultTaskShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdBy is not null
        defaultTaskShouldBeFound("createdBy.specified=true");

        // Get all the taskList where createdBy is null
        defaultTaskShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdBy contains DEFAULT_CREATED_BY
        defaultTaskShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the taskList where createdBy contains UPDATED_CREATED_BY
        defaultTaskShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTaskShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the taskList where createdBy does not contain UPDATED_CREATED_BY
        defaultTaskShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedDate equals to DEFAULT_MODIFIED_DATE
        defaultTaskShouldBeFound("modifiedDate.equals=" + DEFAULT_MODIFIED_DATE);

        // Get all the taskList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTaskShouldNotBeFound("modifiedDate.equals=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedDate in DEFAULT_MODIFIED_DATE or UPDATED_MODIFIED_DATE
        defaultTaskShouldBeFound("modifiedDate.in=" + DEFAULT_MODIFIED_DATE + "," + UPDATED_MODIFIED_DATE);

        // Get all the taskList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTaskShouldNotBeFound("modifiedDate.in=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedDate is not null
        defaultTaskShouldBeFound("modifiedDate.specified=true");

        // Get all the taskList where modifiedDate is null
        defaultTaskShouldNotBeFound("modifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedBy equals to DEFAULT_MODIFIED_BY
        defaultTaskShouldBeFound("modifiedBy.equals=" + DEFAULT_MODIFIED_BY);

        // Get all the taskList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTaskShouldNotBeFound("modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedBy in DEFAULT_MODIFIED_BY or UPDATED_MODIFIED_BY
        defaultTaskShouldBeFound("modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY);

        // Get all the taskList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTaskShouldNotBeFound("modifiedBy.in=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedBy is not null
        defaultTaskShouldBeFound("modifiedBy.specified=true");

        // Get all the taskList where modifiedBy is null
        defaultTaskShouldNotBeFound("modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedBy contains DEFAULT_MODIFIED_BY
        defaultTaskShouldBeFound("modifiedBy.contains=" + DEFAULT_MODIFIED_BY);

        // Get all the taskList where modifiedBy contains UPDATED_MODIFIED_BY
        defaultTaskShouldNotBeFound("modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where modifiedBy does not contain DEFAULT_MODIFIED_BY
        defaultTaskShouldNotBeFound("modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY);

        // Get all the taskList where modifiedBy does not contain UPDATED_MODIFIED_BY
        defaultTaskShouldBeFound("modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTasksByAssignedToIsEqualToSomething() throws Exception {
        User assignedTo;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            assignedTo = UserResourceIT.createEntity(em);
        } else {
            assignedTo = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(assignedTo);
        em.flush();
        task.setAssignedTo(assignedTo);
        taskRepository.saveAndFlush(task);
        Long assignedToId = assignedTo.getId();
        // Get all the taskList where assignedTo equals to assignedToId
        defaultTaskShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the taskList where assignedTo equals to (assignedToId + 1)
        defaultTaskShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByTeamIsEqualToSomething() throws Exception {
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            team = TeamResourceIT.createEntity(em);
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        em.persist(team);
        em.flush();
        task.setTeam(team);
        taskRepository.saveAndFlush(task);
        Long teamId = team.getId();
        // Get all the taskList where team equals to teamId
        defaultTaskShouldBeFound("teamId.equals=" + teamId);

        // Get all the taskList where team equals to (teamId + 1)
        defaultTaskShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTask.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTask.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTask.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTask.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .description(UPDATED_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTask.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTask.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTask.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTask.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTask.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .priority(UPDATED_PRIORITY)
            .status(UPDATED_STATUS)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTask.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTask.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTask.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTask.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTask.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
