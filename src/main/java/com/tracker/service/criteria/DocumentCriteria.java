package com.tracker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tracker.domain.Document} entity. This class is used
 * in {@link com.tracker.web.rest.DocumentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /documents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter mimeType;

    private StringFilter url;

    private InstantFilter createdDate;

    private StringFilter createdBy;

    private LongFilter messageId;

    private LongFilter teamId;

    private Boolean distinct;

    public DocumentCriteria() {}

    public DocumentCriteria(DocumentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.mimeType = other.mimeType == null ? null : other.mimeType.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.messageId = other.messageId == null ? null : other.messageId.copy();
        this.teamId = other.teamId == null ? null : other.teamId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DocumentCriteria copy() {
        return new DocumentCriteria(this);
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

    public StringFilter getMimeType() {
        return mimeType;
    }

    public StringFilter mimeType() {
        if (mimeType == null) {
            mimeType = new StringFilter();
        }
        return mimeType;
    }

    public void setMimeType(StringFilter mimeType) {
        this.mimeType = mimeType;
    }

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
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

    public LongFilter getMessageId() {
        return messageId;
    }

    public LongFilter messageId() {
        if (messageId == null) {
            messageId = new LongFilter();
        }
        return messageId;
    }

    public void setMessageId(LongFilter messageId) {
        this.messageId = messageId;
    }

    public LongFilter getTeamId() {
        return teamId;
    }

    public LongFilter teamId() {
        if (teamId == null) {
            teamId = new LongFilter();
        }
        return teamId;
    }

    public void setTeamId(LongFilter teamId) {
        this.teamId = teamId;
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
        final DocumentCriteria that = (DocumentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(mimeType, that.mimeType) &&
            Objects.equals(url, that.url) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(messageId, that.messageId) &&
            Objects.equals(teamId, that.teamId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mimeType, url, createdDate, createdBy, messageId, teamId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (mimeType != null ? "mimeType=" + mimeType + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (messageId != null ? "messageId=" + messageId + ", " : "") +
            (teamId != null ? "teamId=" + teamId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
