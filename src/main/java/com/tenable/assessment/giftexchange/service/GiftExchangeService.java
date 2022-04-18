package com.tenable.assessment.giftexchange.service;

import com.tenable.assessment.dto.GiftExchangeDto;

import java.util.List;

public interface GiftExchangeService {

    List<GiftExchangeDto> getRecentGiftExchange();
    List<GiftExchangeDto> giftExchangeShufflePost();
}
