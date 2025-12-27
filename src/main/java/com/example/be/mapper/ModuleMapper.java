package com.example.be.mapper;

import com.example.be.dto.response.ModuleResponse;
import com.example.be.entity.Module;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    ModuleResponse toModuleResponse(Module module);
}
