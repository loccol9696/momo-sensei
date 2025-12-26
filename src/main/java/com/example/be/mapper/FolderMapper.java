package com.example.be.mapper;

import com.example.be.dto.response.FolderResponse;
import com.example.be.entity.Folder;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FolderMapper {
    FolderResponse toFolderResponse(Folder folder);
}
