package com.tracker.service.mapper;

import com.tracker.domain.TemplateChecklist;
import com.tracker.domain.TemplateTask;
import com.tracker.service.dto.TemplateChecklistDTO;
import com.tracker.service.dto.TemplateTaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TemplateChecklist} and its DTO {@link TemplateChecklistDTO}.
 */
@Mapper(componentModel = "spring")
public interface TemplateChecklistMapper extends EntityMapper<TemplateChecklistDTO, TemplateChecklist> {
    @Mapping(target = "task", source = "task", qualifiedByName = "templateTaskId")
    TemplateChecklistDTO toDto(TemplateChecklist s);

    @Named("templateTaskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TemplateTaskDTO toDtoTemplateTaskId(TemplateTask templateTask);
}
