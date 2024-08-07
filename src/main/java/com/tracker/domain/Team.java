package com.tracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private Instant modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User teamLead;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_team__team_members",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "team_members_id")
    )
    private Set<User> teamMembers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    @JsonIgnoreProperties(value = { "assignedTo", "team" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    @JsonIgnoreProperties(value = { "toUserId", "fromUserId", "team", "documents" }, allowSetters = true)
    private Set<Message> messages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    @JsonIgnoreProperties(value = { "message", "team" }, allowSetters = true)
    private Set<Document> documents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Team active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Team createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Team createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getModifiedDate() {
        return this.modifiedDate;
    }

    public Team modifiedDate(Instant modifiedDate) {
        this.setModifiedDate(modifiedDate);
        return this;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public Team modifiedBy(String modifiedBy) {
        this.setModifiedBy(modifiedBy);
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public User getTeamLead() {
        return this.teamLead;
    }

    public void setTeamLead(User user) {
        this.teamLead = user;
    }

    public Team teamLead(User user) {
        this.setTeamLead(user);
        return this;
    }

    public Set<User> getTeamMembers() {
        return this.teamMembers;
    }

    public void setTeamMembers(Set<User> users) {
        this.teamMembers = users;
    }

    public Team teamMembers(Set<User> users) {
        this.setTeamMembers(users);
        return this;
    }

    public Team addTeamMembers(User user) {
        this.teamMembers.add(user);
        return this;
    }

    public Team removeTeamMembers(User user) {
        this.teamMembers.remove(user);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setTeam(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setTeam(this));
        }
        this.tasks = tasks;
    }

    public Team tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Team addTasks(Task task) {
        this.tasks.add(task);
        task.setTeam(this);
        return this;
    }

    public Team removeTasks(Task task) {
        this.tasks.remove(task);
        task.setTeam(null);
        return this;
    }

    public Set<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<Message> messages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setTeam(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setTeam(this));
        }
        this.messages = messages;
    }

    public Team messages(Set<Message> messages) {
        this.setMessages(messages);
        return this;
    }

    public Team addMessages(Message message) {
        this.messages.add(message);
        message.setTeam(this);
        return this;
    }

    public Team removeMessages(Message message) {
        this.messages.remove(message);
        message.setTeam(null);
        return this;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        if (this.documents != null) {
            this.documents.forEach(i -> i.setTeam(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setTeam(this));
        }
        this.documents = documents;
    }

    public Team documents(Set<Document> documents) {
        this.setDocuments(documents);
        return this;
    }

    public Team addDocuments(Document document) {
        this.documents.add(document);
        document.setTeam(this);
        return this;
    }

    public Team removeDocuments(Document document) {
        this.documents.remove(document);
        document.setTeam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return getId() != null && getId().equals(((Team) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", active='" + getActive() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            "}";
    }
}
