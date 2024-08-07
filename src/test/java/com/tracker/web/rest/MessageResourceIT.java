package com.tracker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tracker.IntegrationTest;
import com.tracker.domain.Document;
import com.tracker.domain.Message;
import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.repository.MessageRepository;
import com.tracker.service.dto.MessageDTO;
import com.tracker.service.mapper.MessageMapper;
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
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MessageResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SEND_TO_ALL_TEAM_MEMBERS = false;
    private static final Boolean UPDATED_SEND_TO_ALL_TEAM_MEMBERS = true;

    private static final Instant DEFAULT_DATE_SENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_SENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageMockMvc;

    private Message message;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .email(DEFAULT_EMAIL)
            .message(DEFAULT_MESSAGE)
            .sendToAllTeamMembers(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS)
            .dateSent(DEFAULT_DATE_SENT);
        return message;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager em) {
        Message message = new Message()
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .sendToAllTeamMembers(UPDATED_SEND_TO_ALL_TEAM_MEMBERS)
            .dateSent(UPDATED_DATE_SENT);
        return message;
    }

    @BeforeEach
    public void initTest() {
        message = createEntity(em);
    }

    @Test
    @Transactional
    void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();
        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testMessage.getSendToAllTeamMembers()).isEqualTo(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS);
        assertThat(testMessage.getDateSent()).isEqualTo(DEFAULT_DATE_SENT);
    }

    @Test
    @Transactional
    void createMessageWithExistingId() throws Exception {
        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = messageRepository.findAll().size();
        // set the field null
        message.setMessage(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSendToAllTeamMembersIsRequired() throws Exception {
        int databaseSizeBeforeTest = messageRepository.findAll().size();
        // set the field null
        message.setSendToAllTeamMembers(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].sendToAllTeamMembers").value(hasItem(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS.booleanValue())))
            .andExpect(jsonPath("$.[*].dateSent").value(hasItem(DEFAULT_DATE_SENT.toString())));
    }

    @Test
    @Transactional
    void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.sendToAllTeamMembers").value(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS.booleanValue()))
            .andExpect(jsonPath("$.dateSent").value(DEFAULT_DATE_SENT.toString()));
    }

    @Test
    @Transactional
    void getMessagesByIdFiltering() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        Long id = message.getId();

        defaultMessageShouldBeFound("id.equals=" + id);
        defaultMessageShouldNotBeFound("id.notEquals=" + id);

        defaultMessageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.greaterThan=" + id);

        defaultMessageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMessageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMessagesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where email equals to DEFAULT_EMAIL
        defaultMessageShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the messageList where email equals to UPDATED_EMAIL
        defaultMessageShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMessagesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultMessageShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the messageList where email equals to UPDATED_EMAIL
        defaultMessageShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMessagesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where email is not null
        defaultMessageShouldBeFound("email.specified=true");

        // Get all the messageList where email is null
        defaultMessageShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByEmailContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where email contains DEFAULT_EMAIL
        defaultMessageShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the messageList where email contains UPDATED_EMAIL
        defaultMessageShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMessagesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where email does not contain DEFAULT_EMAIL
        defaultMessageShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the messageList where email does not contain UPDATED_EMAIL
        defaultMessageShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where message equals to DEFAULT_MESSAGE
        defaultMessageShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the messageList where message equals to UPDATED_MESSAGE
        defaultMessageShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultMessageShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the messageList where message equals to UPDATED_MESSAGE
        defaultMessageShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where message is not null
        defaultMessageShouldBeFound("message.specified=true");

        // Get all the messageList where message is null
        defaultMessageShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByMessageContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where message contains DEFAULT_MESSAGE
        defaultMessageShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the messageList where message contains UPDATED_MESSAGE
        defaultMessageShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllMessagesByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where message does not contain DEFAULT_MESSAGE
        defaultMessageShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the messageList where message does not contain UPDATED_MESSAGE
        defaultMessageShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllMessagesBySendToAllTeamMembersIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sendToAllTeamMembers equals to DEFAULT_SEND_TO_ALL_TEAM_MEMBERS
        defaultMessageShouldBeFound("sendToAllTeamMembers.equals=" + DEFAULT_SEND_TO_ALL_TEAM_MEMBERS);

        // Get all the messageList where sendToAllTeamMembers equals to UPDATED_SEND_TO_ALL_TEAM_MEMBERS
        defaultMessageShouldNotBeFound("sendToAllTeamMembers.equals=" + UPDATED_SEND_TO_ALL_TEAM_MEMBERS);
    }

    @Test
    @Transactional
    void getAllMessagesBySendToAllTeamMembersIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sendToAllTeamMembers in DEFAULT_SEND_TO_ALL_TEAM_MEMBERS or UPDATED_SEND_TO_ALL_TEAM_MEMBERS
        defaultMessageShouldBeFound("sendToAllTeamMembers.in=" + DEFAULT_SEND_TO_ALL_TEAM_MEMBERS + "," + UPDATED_SEND_TO_ALL_TEAM_MEMBERS);

        // Get all the messageList where sendToAllTeamMembers equals to UPDATED_SEND_TO_ALL_TEAM_MEMBERS
        defaultMessageShouldNotBeFound("sendToAllTeamMembers.in=" + UPDATED_SEND_TO_ALL_TEAM_MEMBERS);
    }

    @Test
    @Transactional
    void getAllMessagesBySendToAllTeamMembersIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where sendToAllTeamMembers is not null
        defaultMessageShouldBeFound("sendToAllTeamMembers.specified=true");

        // Get all the messageList where sendToAllTeamMembers is null
        defaultMessageShouldNotBeFound("sendToAllTeamMembers.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByDateSentIsEqualToSomething() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where dateSent equals to DEFAULT_DATE_SENT
        defaultMessageShouldBeFound("dateSent.equals=" + DEFAULT_DATE_SENT);

        // Get all the messageList where dateSent equals to UPDATED_DATE_SENT
        defaultMessageShouldNotBeFound("dateSent.equals=" + UPDATED_DATE_SENT);
    }

    @Test
    @Transactional
    void getAllMessagesByDateSentIsInShouldWork() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where dateSent in DEFAULT_DATE_SENT or UPDATED_DATE_SENT
        defaultMessageShouldBeFound("dateSent.in=" + DEFAULT_DATE_SENT + "," + UPDATED_DATE_SENT);

        // Get all the messageList where dateSent equals to UPDATED_DATE_SENT
        defaultMessageShouldNotBeFound("dateSent.in=" + UPDATED_DATE_SENT);
    }

    @Test
    @Transactional
    void getAllMessagesByDateSentIsNullOrNotNull() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList where dateSent is not null
        defaultMessageShouldBeFound("dateSent.specified=true");

        // Get all the messageList where dateSent is null
        defaultMessageShouldNotBeFound("dateSent.specified=false");
    }

    @Test
    @Transactional
    void getAllMessagesByToUserIdIsEqualToSomething() throws Exception {
        User toUserId;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            toUserId = UserResourceIT.createEntity(em);
        } else {
            toUserId = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(toUserId);
        em.flush();
        message.setToUserId(toUserId);
        messageRepository.saveAndFlush(message);
        Long toUserIdId = toUserId.getId();
        // Get all the messageList where toUserId equals to toUserIdId
        defaultMessageShouldBeFound("toUserIdId.equals=" + toUserIdId);

        // Get all the messageList where toUserId equals to (toUserIdId + 1)
        defaultMessageShouldNotBeFound("toUserIdId.equals=" + (toUserIdId + 1));
    }

    @Test
    @Transactional
    void getAllMessagesByFromUserIdIsEqualToSomething() throws Exception {
        User fromUserId;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            fromUserId = UserResourceIT.createEntity(em);
        } else {
            fromUserId = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(fromUserId);
        em.flush();
        message.setFromUserId(fromUserId);
        messageRepository.saveAndFlush(message);
        Long fromUserIdId = fromUserId.getId();
        // Get all the messageList where fromUserId equals to fromUserIdId
        defaultMessageShouldBeFound("fromUserIdId.equals=" + fromUserIdId);

        // Get all the messageList where fromUserId equals to (fromUserIdId + 1)
        defaultMessageShouldNotBeFound("fromUserIdId.equals=" + (fromUserIdId + 1));
    }

    @Test
    @Transactional
    void getAllMessagesByTeamIsEqualToSomething() throws Exception {
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            team = TeamResourceIT.createEntity(em);
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        em.persist(team);
        em.flush();
        message.setTeam(team);
        messageRepository.saveAndFlush(message);
        Long teamId = team.getId();
        // Get all the messageList where team equals to teamId
        defaultMessageShouldBeFound("teamId.equals=" + teamId);

        // Get all the messageList where team equals to (teamId + 1)
        defaultMessageShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    @Test
    @Transactional
    void getAllMessagesByDocumentsIsEqualToSomething() throws Exception {
        Document documents;
        if (TestUtil.findAll(em, Document.class).isEmpty()) {
            messageRepository.saveAndFlush(message);
            documents = DocumentResourceIT.createEntity(em);
        } else {
            documents = TestUtil.findAll(em, Document.class).get(0);
        }
        em.persist(documents);
        em.flush();
        message.addDocuments(documents);
        messageRepository.saveAndFlush(message);
        Long documentsId = documents.getId();
        // Get all the messageList where documents equals to documentsId
        defaultMessageShouldBeFound("documentsId.equals=" + documentsId);

        // Get all the messageList where documents equals to (documentsId + 1)
        defaultMessageShouldNotBeFound("documentsId.equals=" + (documentsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageShouldBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].sendToAllTeamMembers").value(hasItem(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS.booleanValue())))
            .andExpect(jsonPath("$.[*].dateSent").value(hasItem(DEFAULT_DATE_SENT.toString())));

        // Check, that the count call also returns 1
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMessageShouldNotBeFound(String filter) throws Exception {
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .sendToAllTeamMembers(UPDATED_SEND_TO_ALL_TEAM_MEMBERS)
            .dateSent(UPDATED_DATE_SENT);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMessage.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testMessage.getSendToAllTeamMembers()).isEqualTo(UPDATED_SEND_TO_ALL_TEAM_MEMBERS);
        assertThat(testMessage.getDateSent()).isEqualTo(UPDATED_DATE_SENT);
    }

    @Test
    @Transactional
    void putNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.email(UPDATED_EMAIL);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testMessage.getSendToAllTeamMembers()).isEqualTo(DEFAULT_SEND_TO_ALL_TEAM_MEMBERS);
        assertThat(testMessage.getDateSent()).isEqualTo(DEFAULT_DATE_SENT);
    }

    @Test
    @Transactional
    void fullUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .sendToAllTeamMembers(UPDATED_SEND_TO_ALL_TEAM_MEMBERS)
            .dateSent(UPDATED_DATE_SENT);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testMessage.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testMessage.getSendToAllTeamMembers()).isEqualTo(UPDATED_SEND_TO_ALL_TEAM_MEMBERS);
        assertThat(testMessage.getDateSent()).isEqualTo(UPDATED_DATE_SENT);
    }

    @Test
    @Transactional
    void patchNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, messageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(messageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeDelete = messageRepository.findAll().size();

        // Delete the message
        restMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, message.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
