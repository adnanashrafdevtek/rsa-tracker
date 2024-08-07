package com.tracker.service.mapper;

import com.tracker.domain.Message;
import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.service.dto.MessageDTO;
import com.tracker.service.dto.TeamDTO;
import com.tracker.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "toUserId", source = "toUserId", qualifiedByName = "userId")
    @Mapping(target = "fromUserId", source = "fromUserId", qualifiedByName = "userId")
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    MessageDTO toDto(Message s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);
}
