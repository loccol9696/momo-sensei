package com.example.be.mapper;

import com.example.be.dto.response.CardResponse;
import com.example.be.entity.Card;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardResponse toCardResponse(Card card);
    List<CardResponse> toCardResponseList(List<Card> cards);
}
