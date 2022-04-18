package com.tenable.assessment.giftexchange.service.impl;

import com.tenable.assessment.dto.GiftExchangeDto;
import com.tenable.assessment.giftexchange.domain.GiftExchange;
import com.tenable.assessment.giftexchange.domain.Member;
import com.tenable.assessment.giftexchange.service.GiftExchangeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GiftExchangeServiceImpl implements GiftExchangeService {
    private final Set<GiftExchange> giftExchangeDataSet;
    private final Set<Member> memberDataSet;

    public GiftExchangeServiceImpl(Set<GiftExchange> giftExchangeDataSet, Set<Member> memberDataSet){
        this.giftExchangeDataSet = giftExchangeDataSet;
        this.memberDataSet = memberDataSet;
    }
    /**
     * @return
     */
    @Override
    public List<GiftExchangeDto> getRecentGiftExchange() {
        List<GiftExchangeDto> giftExchangeDtoList = new ArrayList<>();
        //get giftYear for first element and return all the giftexchange elements with the gift year
        //for example user can see last years gift exchanges before exchanging for current year
        if(!CollectionUtils.isEmpty(giftExchangeDataSet)) {
            //sort the data by giftYear
            List<GiftExchange> giftExchangeList = giftExchangeDataSet.stream()
                    .sorted((item1, item2)-> item2.getGiftYear()-item1.getGiftYear())
                    .collect(Collectors.toList());
            int latestGiftYear = giftExchangeList.get(0).getGiftYear();
            //get all the gift exchanges for latest year
            giftExchangeList = giftExchangeDataSet.stream()
                    .filter(item-> item.getGiftYear()==latestGiftYear).collect(Collectors.toList());

            for(GiftExchange giftExchange : giftExchangeList){
                giftExchangeDtoList.add(mapToGiftExchangeDto(giftExchange));
            }
        }

        return giftExchangeDtoList;
    }

    /**
     * @return
     */
    @Override
    public List<GiftExchangeDto> giftExchangeShufflePost() {
        //TODO: Considering this method does not take any user input,
        // the members are already stored in datastore and api would use the members from data
        // store rather than taking input from user and perform shuffling and assign gifts
        // the reason for having this implementation is user can pass invalid members or
        // send members in input for which gift exchange would not be possible

        List<GiftExchangeDto> shuffledGiftExchanges = new ArrayList<>();
        int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
        //get gift exchange map with key giverId and giftexchange object as value for current year
        //reshuffle can happen multiple time even after gifts are assigned first time in the year
        Map<String, GiftExchange> giftExchangeMap = new HashMap<>();
        if(!giftExchangeDataSet.isEmpty()){
            giftExchangeMap = giftExchangeDataSet.stream()
                    .filter(item-> item.getGiftYear()==currentYear).
                    collect(Collectors.toMap(GiftExchange::getGiverId, Function.identity()));

        }
        //converting to list to handle indexed based access
        List<Member> memberList = new ArrayList<>(memberDataSet);
        Set<String> receivers = new HashSet<>();
        for(int i=0; i<memberList.size(); i++) {
            Member member = memberList.get(i);

            String receiverId;
            //handle special case to ensure when last member not being left out, forcing the last before member to pick the last member
            if(i==memberList.size()-2 && !receivers.contains(memberList.get(i+1).getMemberId())) {
                receiverId = memberList.get(i+1).getMemberId();
            } else{
                //find receiver for each giver
                //exclude self member and already received for current year and received once in previous 2 years
                List<String> possibleReceivers = memberDataSet.stream().filter(receiver-> !receiver.getMemberId().equals(member.getMemberId())
                                && !receivers.contains(receiver.getMemberId())
                                && !isReceivedOnceInPrevious2Years(member.getMemberId(), receiver.getMemberId(), currentYear))
                        .map(Member::getMemberId)
                        .collect(Collectors.toList());
                //shuffle possible receivers
                Collections.shuffle(possibleReceivers);

                //pick one randomly from shuffled list
                int randomIndex = ThreadLocalRandom.current().nextInt(possibleReceivers.size());
                receiverId  = possibleReceivers.get(randomIndex);
            }

            receivers.add(receiverId);
            GiftExchange giftExchange;
            if(giftExchangeMap.containsKey(member.getMemberId())) {
                giftExchange = giftExchangeMap.get(member.getMemberId());
            } else {
                giftExchange = new GiftExchange();
                giftExchange.setGiverId(member.getMemberId());
                giftExchange.setGiftYear(Integer.valueOf(currentYear));
                giftExchangeDataSet.add(giftExchange);

            }

            giftExchange.setReceiverId(receiverId);
            shuffledGiftExchanges.add(mapToGiftExchangeDto(giftExchange));

        }

        return shuffledGiftExchanges;
    }


    private GiftExchangeDto mapToGiftExchangeDto(GiftExchange giftExchange){
        //would use pojo mapper like Dozer mapper
        GiftExchangeDto giftExchangeDto = new GiftExchangeDto();
        giftExchangeDto.setGiverId(giftExchange.getGiverId());
        giftExchangeDto.setReceiverId(giftExchange.getReceiverId());

        return giftExchangeDto;

    }

    private boolean isReceivedOnceInPrevious2Years(String giverId, String receiverId, int currentYear) {

        //considering gift exchange happens every year,
        // if gift exchange didn't happen in a year the year still be considered as a gift year
        return  !giftExchangeDataSet.isEmpty() && giftExchangeDataSet.stream()
                        .anyMatch(exchange-> (exchange.getGiftYear()==currentYear-1 || exchange.getGiftYear()==currentYear-2)
                        && exchange.getGiverId().equals(giverId)
                        && exchange.getReceiverId().equals(receiverId));

    }
}
