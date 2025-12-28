package com.example.be.mapper;

import com.example.be.dto.response.ModuleResponse;
import com.example.be.entity.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    @Mapping(target = "ownerId", source = "user.id")
    @Mapping(target = "ownerName", source = "user.fullName")
    ModuleResponse toModuleResponse(Module module);
}
