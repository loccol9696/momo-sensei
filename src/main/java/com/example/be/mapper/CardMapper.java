package com.example.be.mapper;

import com.example.be.dto.response.CardResponse;
import com.example.be.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "isStarred", ignore = true)
    CardResponse toCardResponse(Card card);
    List<CardResponse> toCardResponseList(List<Card> cards);
}
