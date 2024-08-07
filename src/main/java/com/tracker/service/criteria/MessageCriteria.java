package com.tracker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.tracker.domain.Message} entity. This class is used
 * in {@link com.tracker.web.rest.MessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter email;

    private StringFilter message;

    private BooleanFilter sendToAllTeamMembers;

    private InstantFilter dateSent;

    private LongFilter toUserIdId;

    private LongFilter fromUserIdId;

    private LongFilter teamId;

    private LongFilter documentsId;

    private Boolean distinct;

    public MessageCriteria() {}

    public MessageCriteria(MessageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.message = other.message == null ? null : other.message.copy();
        this.sendToAllTeamMembers = other.sendToAllTeamMembers == null ? null : other.sendToAllTeamMembers.copy();
        this.dateSent = other.dateSent == null ? null : other.dateSent.copy();
        this.toUserIdId = other.toUserIdId == null ? null : other.toUserIdId.copy();
        this.fromUserIdId = other.fromUserIdId == null ? null : other.fromUserIdId.copy();
        this.teamId = other.teamId == null ? null : other.teamId.copy();
        this.documentsId = other.documentsId == null ? null : other.documentsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MessageCriteria copy() {
        return new MessageCriteria(this);
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

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getMessage() {
        return message;
    }

    public StringFilter message() {
        if (message == null) {
            message = new StringFilter();
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public BooleanFilter getSendToAllTeamMembers() {
        return sendToAllTeamMembers;
    }

    public BooleanFilter sendToAllTeamMembers() {
        if (sendToAllTeamMembers == null) {
            sendToAllTeamMembers = new BooleanFilter();
        }
        return sendToAllTeamMembers;
    }

    public void setSendToAllTeamMembers(BooleanFilter sendToAllTeamMembers) {
        this.sendToAllTeamMembers = sendToAllTeamMembers;
    }

    public InstantFilter getDateSent() {
        return dateSent;
    }

    public InstantFilter dateSent() {
        if (dateSent == null) {
            dateSent = new InstantFilter();
        }
        return dateSent;
    }

    public void setDateSent(InstantFilter dateSent) {
        this.dateSent = dateSent;
    }

    public LongFilter getToUserIdId() {
        return toUserIdId;
    }

    public LongFilter toUserIdId() {
        if (toUserIdId == null) {
            toUserIdId = new LongFilter();
        }
        return toUserIdId;
    }

    public void setToUserIdId(LongFilter toUserIdId) {
        this.toUserIdId = toUserIdId;
    }

    public LongFilter getFromUserIdId() {
        return fromUserIdId;
    }

    public LongFilter fromUserIdId() {
        if (fromUserIdId == null) {
            fromUserIdId = new LongFilter();
        }
        return fromUserIdId;
    }

    public void setFromUserIdId(LongFilter fromUserIdId) {
        this.fromUserIdId = fromUserIdId;
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
        final MessageCriteria that = (MessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(email, that.email) &&
            Objects.equals(message, that.message) &&
            Objects.equals(sendToAllTeamMembers, that.sendToAllTeamMembers) &&
            Objects.equals(dateSent, that.dateSent) &&
            Objects.equals(toUserIdId, that.toUserIdId) &&
            Objects.equals(fromUserIdId, that.fromUserIdId) &&
            Objects.equals(teamId, that.teamId) &&
            Objects.equals(documentsId, that.documentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, message, sendToAllTeamMembers, dateSent, toUserIdId, fromUserIdId, teamId, documentsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (message != null ? "message=" + message + ", " : "") +
            (sendToAllTeamMembers != null ? "sendToAllTeamMembers=" + sendToAllTeamMembers + ", " : "") +
            (dateSent != null ? "dateSent=" + dateSent + ", " : "") +
            (toUserIdId != null ? "toUserIdId=" + toUserIdId + ", " : "") +
            (fromUserIdId != null ? "fromUserIdId=" + fromUserIdId + ", " : "") +
            (teamId != null ? "teamId=" + teamId + ", " : "") +
            (documentsId != null ? "documentsId=" + documentsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
