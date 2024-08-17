package com.tracker.service.mapper;

import com.tracker.domain.Template;
import com.tracker.domain.TemplateTask;
import com.tracker.service.dto.TemplateDTO;
import com.tracker.service.dto.TemplateTaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TemplateTask} and its DTO {@link TemplateTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TemplateTaskMapper extends EntityMapper<TemplateTaskDTO, TemplateTask> {
    @Mapping(target = "template", source = "template", qualifiedByName = "templateId")
    TemplateTaskDTO toDto(TemplateTask s);

    @Named("templateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TemplateDTO toDtoTemplateId(Template template);
}
