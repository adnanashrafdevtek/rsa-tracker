package com.tracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull
    @Column(name = "send_to_all_team_members", nullable = false)
    private Boolean sendToAllTeamMembers;

    @Column(name = "date_sent")
    private Instant dateSent;

    @ManyToOne(fetch = FetchType.LAZY)
    private User toUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "teamLead", "teamMembers", "tasks", "messages", "documents" }, allowSetters = true)
    private Team team;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    @JsonIgnoreProperties(value = { "message", "team" }, allowSetters = true)
    private Set<Document> documents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public Message email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return this.message;
    }

    public Message message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSendToAllTeamMembers() {
        return this.sendToAllTeamMembers;
    }

    public Message sendToAllTeamMembers(Boolean sendToAllTeamMembers) {
        this.setSendToAllTeamMembers(sendToAllTeamMembers);
        return this;
    }

    public void setSendToAllTeamMembers(Boolean sendToAllTeamMembers) {
        this.sendToAllTeamMembers = sendToAllTeamMembers;
    }

    public Instant getDateSent() {
        return this.dateSent;
    }

    public Message dateSent(Instant dateSent) {
        this.setDateSent(dateSent);
        return this;
    }

    public void setDateSent(Instant dateSent) {
        this.dateSent = dateSent;
    }

    public User getToUserId() {
        return this.toUserId;
    }

    public void setToUserId(User user) {
        this.toUserId = user;
    }

    public Message toUserId(User user) {
        this.setToUserId(user);
        return this;
    }

    public User getFromUserId() {
        return this.fromUserId;
    }

    public void setFromUserId(User user) {
        this.fromUserId = user;
    }

    public Message fromUserId(User user) {
        this.setFromUserId(user);
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Message team(Team team) {
        this.setTeam(team);
        return this;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        if (this.documents != null) {
            this.documents.forEach(i -> i.setMessage(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setMessage(this));
        }
        this.documents = documents;
    }

    public Message documents(Set<Document> documents) {
        this.setDocuments(documents);
        return this;
    }

    public Message addDocuments(Document document) {
        this.documents.add(document);
        document.setMessage(this);
        return this;
    }

    public Message removeDocuments(Document document) {
        this.documents.remove(document);
        document.setMessage(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", message='" + getMessage() + "'" +
            ", sendToAllTeamMembers='" + getSendToAllTeamMembers() + "'" +
            ", dateSent='" + getDateSent() + "'" +
            "}";
    }
}
