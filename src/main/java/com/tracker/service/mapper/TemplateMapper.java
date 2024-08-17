package com.tracker.service.mapper;

import com.tracker.domain.Template;
import com.tracker.service.dto.TemplateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Template} and its DTO {@link TemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface TemplateMapper extends EntityMapper<TemplateDTO, Template> {}
