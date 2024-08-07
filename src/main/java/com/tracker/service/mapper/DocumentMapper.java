package com.tracker.service.mapper;

import com.tracker.domain.Document;
import com.tracker.domain.Message;
import com.tracker.domain.Team;
import com.tracker.service.dto.DocumentDTO;
import com.tracker.service.dto.MessageDTO;
import com.tracker.service.dto.TeamDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Document} and its DTO {@link DocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DocumentMapper extends EntityMapper<DocumentDTO, Document> {
    @Mapping(target = "message", source = "message", qualifiedByName = "messageId")
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    DocumentDTO toDto(Document s);

    @Named("messageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MessageDTO toDtoMessageId(Message message);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);
}
