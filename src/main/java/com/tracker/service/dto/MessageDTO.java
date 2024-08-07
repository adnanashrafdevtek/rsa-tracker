package com.tracker.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.tracker.domain.Message} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageDTO implements Serializable {

    private Long id;

    private String email;

    @NotNull
    private String message;

    @NotNull
    private Boolean sendToAllTeamMembers;

    private Instant dateSent;

    private UserDTO toUserId;

    private UserDTO fromUserId;

    private TeamDTO team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSendToAllTeamMembers() {
        return sendToAllTeamMembers;
    }

    public void setSendToAllTeamMembers(Boolean sendToAllTeamMembers) {
        this.sendToAllTeamMembers = sendToAllTeamMembers;
    }

    public Instant getDateSent() {
        return dateSent;
    }

    public void setDateSent(Instant dateSent) {
        this.dateSent = dateSent;
    }

    public UserDTO getToUserId() {
        return toUserId;
    }

    public void setToUserId(UserDTO toUserId) {
        this.toUserId = toUserId;
    }

    public UserDTO getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(UserDTO fromUserId) {
        this.fromUserId = fromUserId;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        MessageDTO messageDTO = (MessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", message='" + getMessage() + "'" +
            ", sendToAllTeamMembers='" + getSendToAllTeamMembers() + "'" +
            ", dateSent='" + getDateSent() + "'" +
            ", toUserId=" + getToUserId() +
            ", fromUserId=" + getFromUserId() +
            ", team=" + getTeam() +
            "}";
    }
}
