package com.tenable.assessment.giftexchange.controller;

import com.tenable.assessment.api.GiftExchangeApi;
import com.tenable.assessment.dto.GiftExchangeDto;
import com.tenable.assessment.giftexchange.service.impl.GiftExchangeServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GiftExchangeController implements GiftExchangeApi {
    private final GiftExchangeServiceImpl giftExchangeService;

    public GiftExchangeController(GiftExchangeServiceImpl giftExchangeService){

        this.giftExchangeService = giftExchangeService;
    }
    /**
     * @return
     */
    @Override
    public ResponseEntity<List<GiftExchangeDto>> giftExchangeGet() {
        return ResponseEntity.ok(giftExchangeService.getRecentGiftExchange());
    }


    /**
     *
     * @return
     */
    @Override
    public ResponseEntity<List<GiftExchangeDto>> giftExchangeShufflePost() {
        //TODO: Considering this enpoint does not take any request body even though this is POST,
        // the members are already stored in datastore and api would use the members from data
        // store rather than taking input from user and perform shuffling and assign gifts
        // the reason for having this implementation is user can pass invalid members or
        // send members in input for which gift exchange would not be possible

        return ResponseEntity.ok(giftExchangeService.giftExchangeShufflePost());
    }
}
