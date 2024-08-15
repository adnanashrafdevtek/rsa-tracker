package com.tracker.service.mapper;

import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.service.dto.TeamDTO;
import com.tracker.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {
    @Mapping(target = "teamLead", source = "teamLead", qualifiedByName = "userId")
    @Mapping(target = "teamMembers", source = "teamMembers", qualifiedByName = "userIdSet")
    TeamDTO toDto(Team s);

    @Mapping(target = "removeTeamMembers", ignore = true)
    Team toEntity(TeamDTO teamDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
