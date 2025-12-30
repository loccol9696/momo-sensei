package com.example.be.mapper;

import com.example.be.dto.response.CardResponse;
import com.example.be.entity.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardResponse toCardResponse(Card card);
}
