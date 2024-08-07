package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Document;
import com.tracker.domain.Message;
import com.tracker.domain.Team;
import com.tracker.repository.DocumentRepository;
import com.tracker.service.dto.DocumentDTO;
import com.tracker.service.mapper.DocumentMapper;
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
 * Integration tests for the {@link DocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocumentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocumentMockMvc;

    private Document document;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createEntity(EntityManager em) {
        Document document = new Document()
            .name(DEFAULT_NAME)
            .mimeType(DEFAULT_MIME_TYPE)
            .url(DEFAULT_URL)
            .createdDate(DEFAULT_CREATED_DATE)
            .createdBy(DEFAULT_CREATED_BY);
        return document;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createUpdatedEntity(EntityManager em) {
        Document document = new Document()
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .url(UPDATED_URL)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY);
        return document;
    }

    @BeforeEach
    public void initTest() {
        document = createEntity(em);
    }

    @Test
    @Transactional
    void createDocument() throws Exception {
        int databaseSizeBeforeCreate = documentRepository.findAll().size();
        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);
        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isCreated());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeCreate + 1);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDocument.getMimeType()).isEqualTo(DEFAULT_MIME_TYPE);
        assertThat(testDocument.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDocument.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDocument.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void createDocumentWithExistingId() throws Exception {
        // Create the Document with an existing ID
        document.setId(1L);
        DocumentDTO documentDTO = documentMapper.toDto(document);

        int databaseSizeBeforeCreate = documentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = documentRepository.findAll().size();
        // set the field null
        document.setName(null);

        // Create the Document, which fails.
        DocumentDTO documentDTO = documentMapper.toDto(document);

        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isBadRequest());

        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMimeTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = documentRepository.findAll().size();
        // set the field null
        document.setMimeType(null);

        // Create the Document, which fails.
        DocumentDTO documentDTO = documentMapper.toDto(document);

        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isBadRequest());

        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDocuments() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get the document
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(document.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getDocumentsByIdFiltering() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        Long id = document.getId();

        defaultDocumentShouldBeFound("id.equals=" + id);
        defaultDocumentShouldNotBeFound("id.notEquals=" + id);

        defaultDocumentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDocumentShouldNotBeFound("id.greaterThan=" + id);

        defaultDocumentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDocumentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDocumentsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where name equals to DEFAULT_NAME
        defaultDocumentShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the documentList where name equals to UPDATED_NAME
        defaultDocumentShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDocumentsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDocumentShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the documentList where name equals to UPDATED_NAME
        defaultDocumentShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDocumentsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where name is not null
        defaultDocumentShouldBeFound("name.specified=true");

        // Get all the documentList where name is null
        defaultDocumentShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByNameContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where name contains DEFAULT_NAME
        defaultDocumentShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the documentList where name contains UPDATED_NAME
        defaultDocumentShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDocumentsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where name does not contain DEFAULT_NAME
        defaultDocumentShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the documentList where name does not contain UPDATED_NAME
        defaultDocumentShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDocumentsByMimeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where mimeType equals to DEFAULT_MIME_TYPE
        defaultDocumentShouldBeFound("mimeType.equals=" + DEFAULT_MIME_TYPE);

        // Get all the documentList where mimeType equals to UPDATED_MIME_TYPE
        defaultDocumentShouldNotBeFound("mimeType.equals=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByMimeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where mimeType in DEFAULT_MIME_TYPE or UPDATED_MIME_TYPE
        defaultDocumentShouldBeFound("mimeType.in=" + DEFAULT_MIME_TYPE + "," + UPDATED_MIME_TYPE);

        // Get all the documentList where mimeType equals to UPDATED_MIME_TYPE
        defaultDocumentShouldNotBeFound("mimeType.in=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByMimeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where mimeType is not null
        defaultDocumentShouldBeFound("mimeType.specified=true");

        // Get all the documentList where mimeType is null
        defaultDocumentShouldNotBeFound("mimeType.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByMimeTypeContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where mimeType contains DEFAULT_MIME_TYPE
        defaultDocumentShouldBeFound("mimeType.contains=" + DEFAULT_MIME_TYPE);

        // Get all the documentList where mimeType contains UPDATED_MIME_TYPE
        defaultDocumentShouldNotBeFound("mimeType.contains=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByMimeTypeNotContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where mimeType does not contain DEFAULT_MIME_TYPE
        defaultDocumentShouldNotBeFound("mimeType.doesNotContain=" + DEFAULT_MIME_TYPE);

        // Get all the documentList where mimeType does not contain UPDATED_MIME_TYPE
        defaultDocumentShouldBeFound("mimeType.doesNotContain=" + UPDATED_MIME_TYPE);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where url equals to DEFAULT_URL
        defaultDocumentShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the documentList where url equals to UPDATED_URL
        defaultDocumentShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where url in DEFAULT_URL or UPDATED_URL
        defaultDocumentShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the documentList where url equals to UPDATED_URL
        defaultDocumentShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where url is not null
        defaultDocumentShouldBeFound("url.specified=true");

        // Get all the documentList where url is null
        defaultDocumentShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where url contains DEFAULT_URL
        defaultDocumentShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the documentList where url contains UPDATED_URL
        defaultDocumentShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllDocumentsByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where url does not contain DEFAULT_URL
        defaultDocumentShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the documentList where url does not contain UPDATED_URL
        defaultDocumentShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdDate equals to DEFAULT_CREATED_DATE
        defaultDocumentShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the documentList where createdDate equals to UPDATED_CREATED_DATE
        defaultDocumentShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultDocumentShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the documentList where createdDate equals to UPDATED_CREATED_DATE
        defaultDocumentShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdDate is not null
        defaultDocumentShouldBeFound("createdDate.specified=true");

        // Get all the documentList where createdDate is null
        defaultDocumentShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdBy equals to DEFAULT_CREATED_BY
        defaultDocumentShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the documentList where createdBy equals to UPDATED_CREATED_BY
        defaultDocumentShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultDocumentShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the documentList where createdBy equals to UPDATED_CREATED_BY
        defaultDocumentShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdBy is not null
        defaultDocumentShouldBeFound("createdBy.specified=true");

        // Get all the documentList where createdBy is null
        defaultDocumentShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdBy contains DEFAULT_CREATED_BY
        defaultDocumentShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the documentList where createdBy contains UPDATED_CREATED_BY
        defaultDocumentShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDocumentsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        // Get all the documentList where createdBy does not contain DEFAULT_CREATED_BY
        defaultDocumentShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the documentList where createdBy does not contain UPDATED_CREATED_BY
        defaultDocumentShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDocumentsByMessageIsEqualToSomething() throws Exception {
        Message message;
        if (TestUtil.findAll(em, Message.class).isEmpty()) {
            documentRepository.saveAndFlush(document);
            message = MessageResourceIT.createEntity(em);
        } else {
            message = TestUtil.findAll(em, Message.class).get(0);
        }
        em.persist(message);
        em.flush();
        document.setMessage(message);
        documentRepository.saveAndFlush(document);
        Long messageId = message.getId();
        // Get all the documentList where message equals to messageId
        defaultDocumentShouldBeFound("messageId.equals=" + messageId);

        // Get all the documentList where message equals to (messageId + 1)
        defaultDocumentShouldNotBeFound("messageId.equals=" + (messageId + 1));
    }

    @Test
    @Transactional
    void getAllDocumentsByTeamIsEqualToSomething() throws Exception {
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            documentRepository.saveAndFlush(document);
            team = TeamResourceIT.createEntity(em);
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        em.persist(team);
        em.flush();
        document.setTeam(team);
        documentRepository.saveAndFlush(document);
        Long teamId = team.getId();
        // Get all the documentList where team equals to teamId
        defaultDocumentShouldBeFound("teamId.equals=" + teamId);

        // Get all the documentList where team equals to (teamId + 1)
        defaultDocumentShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDocumentShouldBeFound(String filter) throws Exception {
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));

        // Check, that the count call also returns 1
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDocumentShouldNotBeFound(String filter) throws Exception {
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDocument() throws Exception {
        // Get the document
        restDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document
        Document updatedDocument = documentRepository.findById(document.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDocument are not directly saved in db
        em.detach(updatedDocument);
        updatedDocument
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .url(UPDATED_URL)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY);
        DocumentDTO documentDTO = documentMapper.toDto(updatedDocument);

        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDocument.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testDocument.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDocument.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void putNonExistingDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY);

        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDocument.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testDocument.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDocument.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .url(UPDATED_URL)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY);

        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDocument.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testDocument.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDocument.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDocument.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, documentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(documentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDocument() throws Exception {
        // Initialize the database
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeDelete = documentRepository.findAll().size();

        // Delete the document
        restDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, document.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
