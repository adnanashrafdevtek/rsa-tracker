package com.tracker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tracker.domain.Team} entity. This class is used
 * in {@link com.tracker.web.rest.TeamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teams?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BooleanFilter active;

    private InstantFilter createdDate;

    private StringFilter createdBy;

    private InstantFilter modifiedDate;

    private StringFilter modifiedBy;

    private LongFilter teamLeadId;

    private LongFilter teamMembersId;

    private LongFilter tasksId;

    private LongFilter messagesId;

    private LongFilter documentsId;

    private Boolean distinct;

    public TeamCriteria() {}

    public TeamCriteria(TeamCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.modifiedDate = other.modifiedDate == null ? null : other.modifiedDate.copy();
        this.modifiedBy = other.modifiedBy == null ? null : other.modifiedBy.copy();
        this.teamLeadId = other.teamLeadId == null ? null : other.teamLeadId.copy();
        this.teamMembersId = other.teamMembersId == null ? null : other.teamMembersId.copy();
        this.tasksId = other.tasksId == null ? null : other.tasksId.copy();
        this.messagesId = other.messagesId == null ? null : other.messagesId.copy();
        this.documentsId = other.documentsId == null ? null : other.documentsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TeamCriteria copy() {
        return new TeamCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getModifiedDate() {
        return modifiedDate;
    }

    public InstantFilter modifiedDate() {
        if (modifiedDate == null) {
            modifiedDate = new InstantFilter();
        }
        return modifiedDate;
    }

    public void setModifiedDate(InstantFilter modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public StringFilter getModifiedBy() {
        return modifiedBy;
    }

    public StringFilter modifiedBy() {
        if (modifiedBy == null) {
            modifiedBy = new StringFilter();
        }
        return modifiedBy;
    }

    public void setModifiedBy(StringFilter modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LongFilter getTeamLeadId() {
        return teamLeadId;
    }

    public LongFilter teamLeadId() {
        if (teamLeadId == null) {
            teamLeadId = new LongFilter();
        }
        return teamLeadId;
    }

    public void setTeamLeadId(LongFilter teamLeadId) {
        this.teamLeadId = teamLeadId;
    }

    public LongFilter getTeamMembersId() {
        return teamMembersId;
    }

    public LongFilter teamMembersId() {
        if (teamMembersId == null) {
            teamMembersId = new LongFilter();
        }
        return teamMembersId;
    }

    public void setTeamMembersId(LongFilter teamMembersId) {
        this.teamMembersId = teamMembersId;
    }

    public LongFilter getTasksId() {
        return tasksId;
    }

    public LongFilter tasksId() {
        if (tasksId == null) {
            tasksId = new LongFilter();
        }
        return tasksId;
    }

    public void setTasksId(LongFilter tasksId) {
        this.tasksId = tasksId;
    }

    public LongFilter getMessagesId() {
        return messagesId;
    }

    public LongFilter messagesId() {
        if (messagesId == null) {
            messagesId = new LongFilter();
        }
        return messagesId;
    }

    public void setMessagesId(LongFilter messagesId) {
        this.messagesId = messagesId;
    }

    public LongFilter getDocumentsId() {
        return documentsId;
    }

    public LongFilter documentsId() {
        if (documentsId == null) {
            documentsId = new LongFilter();
        }
        return documentsId;
    }

    public void setDocumentsId(LongFilter documentsId) {
        this.documentsId = documentsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TeamCriteria that = (TeamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(modifiedDate, that.modifiedDate) &&
            Objects.equals(modifiedBy, that.modifiedBy) &&
            Objects.equals(teamLeadId, that.teamLeadId) &&
            Objects.equals(teamMembersId, that.teamMembersId) &&
            Objects.equals(tasksId, that.tasksId) &&
            Objects.equals(messagesId, that.messagesId) &&
            Objects.equals(documentsId, that.documentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            active,
            createdDate,
            createdBy,
            modifiedDate,
            modifiedBy,
            teamLeadId,
            teamMembersId,
            tasksId,
            messagesId,
            documentsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (modifiedDate != null ? "modifiedDate=" + modifiedDate + ", " : "") +
            (modifiedBy != null ? "modifiedBy=" + modifiedBy + ", " : "") +
            (teamLeadId != null ? "teamLeadId=" + teamLeadId + ", " : "") +
            (teamMembersId != null ? "teamMembersId=" + teamMembersId + ", " : "") +
            (tasksId != null ? "tasksId=" + tasksId + ", " : "") +
            (messagesId != null ? "messagesId=" + messagesId + ", " : "") +
            (documentsId != null ? "documentsId=" + documentsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
