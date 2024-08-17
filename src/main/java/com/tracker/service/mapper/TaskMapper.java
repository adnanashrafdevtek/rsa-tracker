package com.tracker.service.mapper;

import com.tracker.domain.Task;
import com.tracker.domain.Team;
import com.tracker.domain.User;
import com.tracker.service.dto.TaskDTO;
import com.tracker.service.dto.TeamDTO;
import com.tracker.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "userId")
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    TaskDTO toDto(Task s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);
}
