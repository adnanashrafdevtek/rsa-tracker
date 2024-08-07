package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Document;
import com.tracker.domain.Message;
import com.tracker.domain.Task;
import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.repository.TeamRepository;
import com.tracker.service.TeamService;
import com.tracker.service.dto.TeamDTO;
import com.tracker.service.mapper.TeamMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeamRepository teamRepository;

    @Mock
    private TeamRepository teamRepositoryMock;

    @Autowired
    private TeamMapper teamMapper;

    @Mock
    private TeamService teamServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamMockMvc;

    private Team team;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createEntity(EntityManager em) {
        Team team = new Team()
            .name(DEFAULT_NAME)
            .active(DEFAULT_ACTIVE)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .modifiedBy(DEFAULT_MODIFIED_BY);
        return team;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createUpdatedEntity(EntityManager em) {
        Team team = new Team()
            .name(UPDATED_NAME)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        return team;
    }

    @BeforeEach
    public void initTest() {
        team = createEntity(em);
    }

    @Test
    @Transactional
    void createTeam() throws Exception {
        int databaseSizeBeforeCreate = teamRepository.findAll().size();
        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);
        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isCreated());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeCreate + 1);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTeam.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTeam.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTeam.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTeam.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTeam.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createTeamWithExistingId() throws Exception {
        // Create the Team with an existing ID
        team.setId(1L);
        TeamDTO teamDTO = teamMapper.toDto(team);

        int databaseSizeBeforeCreate = teamRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = teamRepository.findAll().size();
        // set the field null
        team.setName(null);

        // Create the Team, which fails.
        TeamDTO teamDTO = teamMapper.toDto(team);

        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isBadRequest());

        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = teamRepository.findAll().size();
        // set the field null
        team.setActive(null);

        // Create the Team, which fails.
        TeamDTO teamDTO = teamMapper.toDto(team);

        restTeamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isBadRequest());

        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeams() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsEnabled() throws Exception {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teamRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get the team
        restTeamMockMvc
            .perform(get(ENTITY_API_URL_ID, team.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(team.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedBy").value(DEFAULT_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getTeamsByIdFiltering() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        Long id = team.getId();

        defaultTeamShouldBeFound("id.equals=" + id);
        defaultTeamShouldNotBeFound("id.notEquals=" + id);

        defaultTeamShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeamShouldNotBeFound("id.greaterThan=" + id);

        defaultTeamShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeamShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name equals to DEFAULT_NAME
        defaultTeamShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the teamList where name equals to UPDATED_NAME
        defaultTeamShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTeamShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the teamList where name equals to UPDATED_NAME
        defaultTeamShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name is not null
        defaultTeamShouldBeFound("name.specified=true");

        // Get all the teamList where name is null
        defaultTeamShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByNameContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name contains DEFAULT_NAME
        defaultTeamShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the teamList where name contains UPDATED_NAME
        defaultTeamShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where name does not contain DEFAULT_NAME
        defaultTeamShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the teamList where name does not contain UPDATED_NAME
        defaultTeamShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where active equals to DEFAULT_ACTIVE
        defaultTeamShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the teamList where active equals to UPDATED_ACTIVE
        defaultTeamShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTeamsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultTeamShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the teamList where active equals to UPDATED_ACTIVE
        defaultTeamShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTeamsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where active is not null
        defaultTeamShouldBeFound("active.specified=true");

        // Get all the teamList where active is null
        defaultTeamShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTeamShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the teamList where createdDate equals to UPDATED_CREATED_DATE
        defaultTeamShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTeamShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the teamList where createdDate equals to UPDATED_CREATED_DATE
        defaultTeamShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdDate is not null
        defaultTeamShouldBeFound("createdDate.specified=true");

        // Get all the teamList where createdDate is null
        defaultTeamShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdBy equals to DEFAULT_CREATED_BY
        defaultTeamShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the teamList where createdBy equals to UPDATED_CREATED_BY
        defaultTeamShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTeamShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the teamList where createdBy equals to UPDATED_CREATED_BY
        defaultTeamShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdBy is not null
        defaultTeamShouldBeFound("createdBy.specified=true");

        // Get all the teamList where createdBy is null
        defaultTeamShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdBy contains DEFAULT_CREATED_BY
        defaultTeamShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the teamList where createdBy contains UPDATED_CREATED_BY
        defaultTeamShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTeamShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the teamList where createdBy does not contain UPDATED_CREATED_BY
        defaultTeamShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedDate equals to DEFAULT_MODIFIED_DATE
        defaultTeamShouldBeFound("modifiedDate.equals=" + DEFAULT_MODIFIED_DATE);

        // Get all the teamList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTeamShouldNotBeFound("modifiedDate.equals=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedDate in DEFAULT_MODIFIED_DATE or UPDATED_MODIFIED_DATE
        defaultTeamShouldBeFound("modifiedDate.in=" + DEFAULT_MODIFIED_DATE + "," + UPDATED_MODIFIED_DATE);

        // Get all the teamList where modifiedDate equals to UPDATED_MODIFIED_DATE
        defaultTeamShouldNotBeFound("modifiedDate.in=" + UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedDate is not null
        defaultTeamShouldBeFound("modifiedDate.specified=true");

        // Get all the teamList where modifiedDate is null
        defaultTeamShouldNotBeFound("modifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedBy equals to DEFAULT_MODIFIED_BY
        defaultTeamShouldBeFound("modifiedBy.equals=" + DEFAULT_MODIFIED_BY);

        // Get all the teamList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTeamShouldNotBeFound("modifiedBy.equals=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedBy in DEFAULT_MODIFIED_BY or UPDATED_MODIFIED_BY
        defaultTeamShouldBeFound("modifiedBy.in=" + DEFAULT_MODIFIED_BY + "," + UPDATED_MODIFIED_BY);

        // Get all the teamList where modifiedBy equals to UPDATED_MODIFIED_BY
        defaultTeamShouldNotBeFound("modifiedBy.in=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedBy is not null
        defaultTeamShouldBeFound("modifiedBy.specified=true");

        // Get all the teamList where modifiedBy is null
        defaultTeamShouldNotBeFound("modifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedByContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedBy contains DEFAULT_MODIFIED_BY
        defaultTeamShouldBeFound("modifiedBy.contains=" + DEFAULT_MODIFIED_BY);

        // Get all the teamList where modifiedBy contains UPDATED_MODIFIED_BY
        defaultTeamShouldNotBeFound("modifiedBy.contains=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        // Get all the teamList where modifiedBy does not contain DEFAULT_MODIFIED_BY
        defaultTeamShouldNotBeFound("modifiedBy.doesNotContain=" + DEFAULT_MODIFIED_BY);

        // Get all the teamList where modifiedBy does not contain UPDATED_MODIFIED_BY
        defaultTeamShouldBeFound("modifiedBy.doesNotContain=" + UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeamsByTeamLeadIsEqualToSomething() throws Exception {
        User teamLead;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            teamRepository.saveAndFlush(team);
            teamLead = UserResourceIT.createEntity(em);
        } else {
            teamLead = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(teamLead);
        em.flush();
        team.setTeamLead(teamLead);
        teamRepository.saveAndFlush(team);
        Long teamLeadId = teamLead.getId();
        // Get all the teamList where teamLead equals to teamLeadId
        defaultTeamShouldBeFound("teamLeadId.equals=" + teamLeadId);

        // Get all the teamList where teamLead equals to (teamLeadId + 1)
        defaultTeamShouldNotBeFound("teamLeadId.equals=" + (teamLeadId + 1));
    }

    @Test
    @Transactional
    void getAllTeamsByTeamMembersIsEqualToSomething() throws Exception {
        User teamMembers;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            teamRepository.saveAndFlush(team);
            teamMembers = UserResourceIT.createEntity(em);
        } else {
            teamMembers = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(teamMembers);
        em.flush();
        team.addTeamMembers(teamMembers);
        teamRepository.saveAndFlush(team);
        Long teamMembersId = teamMembers.getId();
        // Get all the teamList where teamMembers equals to teamMembersId
        defaultTeamShouldBeFound("teamMembersId.equals=" + teamMembersId);

        // Get all the teamList where teamMembers equals to (teamMembersId + 1)
        defaultTeamShouldNotBeFound("teamMembersId.equals=" + (teamMembersId + 1));
    }

    @Test
    @Transactional
    void getAllTeamsByTasksIsEqualToSomething() throws Exception {
        Task tasks;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            teamRepository.saveAndFlush(team);
            tasks = TaskResourceIT.createEntity(em);
        } else {
            tasks = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(tasks);
        em.flush();
        team.addTasks(tasks);
        teamRepository.saveAndFlush(team);
        Long tasksId = tasks.getId();
        // Get all the teamList where tasks equals to tasksId
        defaultTeamShouldBeFound("tasksId.equals=" + tasksId);

        // Get all the teamList where tasks equals to (tasksId + 1)
        defaultTeamShouldNotBeFound("tasksId.equals=" + (tasksId + 1));
    }

    @Test
    @Transactional
    void getAllTeamsByMessagesIsEqualToSomething() throws Exception {
        Message messages;
        if (TestUtil.findAll(em, Message.class).isEmpty()) {
            teamRepository.saveAndFlush(team);
            messages = MessageResourceIT.createEntity(em);
        } else {
            messages = TestUtil.findAll(em, Message.class).get(0);
        }
        em.persist(messages);
        em.flush();
        team.addMessages(messages);
        teamRepository.saveAndFlush(team);
        Long messagesId = messages.getId();
        // Get all the teamList where messages equals to messagesId
        defaultTeamShouldBeFound("messagesId.equals=" + messagesId);

        // Get all the teamList where messages equals to (messagesId + 1)
        defaultTeamShouldNotBeFound("messagesId.equals=" + (messagesId + 1));
    }

    @Test
    @Transactional
    void getAllTeamsByDocumentsIsEqualToSomething() throws Exception {
        Document documents;
        if (TestUtil.findAll(em, Document.class).isEmpty()) {
            teamRepository.saveAndFlush(team);
            documents = DocumentResourceIT.createEntity(em);
        } else {
            documents = TestUtil.findAll(em, Document.class).get(0);
        }
        em.persist(documents);
        em.flush();
        team.addDocuments(documents);
        teamRepository.saveAndFlush(team);
        Long documentsId = documents.getId();
        // Get all the teamList where documents equals to documentsId
        defaultTeamShouldBeFound("documentsId.equals=" + documentsId);

        // Get all the teamList where documents equals to (documentsId + 1)
        defaultTeamShouldNotBeFound("documentsId.equals=" + (documentsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamShouldBeFound(String filter) throws Exception {
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedBy").value(hasItem(DEFAULT_MODIFIED_BY)));

        // Check, that the count call also returns 1
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeamShouldNotBeFound(String filter) throws Exception {
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeamMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeam() throws Exception {
        // Get the team
        restTeamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team
        Team updatedTeam = teamRepository.findById(team.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeam are not directly saved in db
        em.detach(updatedTeam);
        updatedTeam
            .name(UPDATED_NAME)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);
        TeamDTO teamDTO = teamMapper.toDto(updatedTeam);

        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamDTO))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTeam.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTeam.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeam.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTeam.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTeam.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam.createdDate(UPDATED_CREATED_DATE);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTeam.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTeam.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeam.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTeam.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testTeam.getModifiedBy()).isEqualTo(DEFAULT_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeUpdate = teamRepository.findAll().size();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam
            .name(UPDATED_NAME)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .modifiedBy(UPDATED_MODIFIED_BY);

        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeam))
            )
            .andExpect(status().isOk());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
        Team testTeam = teamList.get(teamList.size() - 1);
        assertThat(testTeam.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTeam.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTeam.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeam.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTeam.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testTeam.getModifiedBy()).isEqualTo(UPDATED_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeam() throws Exception {
        int databaseSizeBeforeUpdate = teamRepository.findAll().size();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Team in the database
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeam() throws Exception {
        // Initialize the database
        teamRepository.saveAndFlush(team);

        int databaseSizeBeforeDelete = teamRepository.findAll().size();

        // Delete the team
        restTeamMockMvc
            .perform(delete(ENTITY_API_URL_ID, team.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Team> teamList = teamRepository.findAll();
        assertThat(teamList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
