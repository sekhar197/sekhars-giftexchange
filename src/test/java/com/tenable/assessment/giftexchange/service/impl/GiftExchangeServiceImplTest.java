package com.tenable.assessment.giftexchange.service.impl;

import com.tenable.assessment.dto.GiftExchangeDto;
import com.tenable.assessment.giftexchange.config.DataStoreTestConfig;
import com.tenable.assessment.giftexchange.domain.GiftExchange;
import com.tenable.assessment.giftexchange.domain.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(DataStoreTestConfig.class)
public class GiftExchangeServiceImplTest {
    @Autowired
    private Set<GiftExchange> giftExchangeDataSet;
    @Autowired
    private Set<Member> memberDataSet;

    @Autowired
    GiftExchangeServiceImpl giftExchangeService;


    @Test
    @Order(2)
    public void testAddMembersAndExchangeGifts() {
        //building 4 members
        for(int i=0; i<4 ; i++) {
            memberDataSet.add(buildMemberData("member"+i, "name"+i));
        }
        List<GiftExchangeDto> giftExchangeDtoList =  giftExchangeService.giftExchangeShufflePost();
        Set<String> receivers = new HashSet<>();

        assertEquals(memberDataSet.size(), giftExchangeDtoList.size());
        giftExchangeDtoList.stream().forEach(giftExchangeDto -> {
            //assert member not received more than one gift
            assertTrue(!receivers.contains(giftExchangeDto.getReceiverId()));
            //assert member not giving gift to self
            assertTrue(!giftExchangeDto.getGiverId().equals(giftExchangeDto.getReceiverId()));
            receivers.add(giftExchangeDto.getReceiverId());
        });
    }

    @Test
    @Order(3)
    public void testExchangeGiftsWithSamePersonInPrevious2Years() {
        //the memberDataSet will be empty when this individual test ran
        if(memberDataSet.isEmpty()){
            //building 4 members
            for(int i=0; i<4 ; i++) {
                memberDataSet.add(buildMemberData("member"+i, "name"+i));
            }
        }
        //At this point memberDataSet has 4 members
        //add data into giftExchangeDataSet for previous 2 years
        //removing 4th member to build test data for 3 gift exchanges
        int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
        //member0 gave gifts to member1 and member3 in past 2 years, so member0 has to give gift to member2 for current year
        // which makes member0 gave gifts to all other members only once in 3 years
        giftExchangeDataSet.add(buildGiftExchange("member0", "member1", currentYear-1));
        giftExchangeDataSet.add(buildGiftExchange("member0", "member3", currentYear-2));

        //member1 gave gifts to member0 and member2 in past 2 years, so member1 has to give gift to member3 for current year
        // which makes member1 gave gifts to all other members only once in 3 years
        giftExchangeDataSet.add(buildGiftExchange("member1", "member0", currentYear-1));
        giftExchangeDataSet.add(buildGiftExchange("member1", "member2", currentYear-2));

        //member2 gave gifts to member3 and member0 in past 2 years, so member2 has to give gift to member1 for current year
        // which makes member2 gave gifts to all other members only once in 3 years
        giftExchangeDataSet.add(buildGiftExchange("member2", "member3", currentYear-1));
        giftExchangeDataSet.add(buildGiftExchange("member2", "member0", currentYear-2));

        //member3 gave gifts to member2 and member1 in past 2 years, so member3 has to give gift to member0 for current year
        // which makes member3 gave gifts to all other members only once in 3 years
        giftExchangeDataSet.add(buildGiftExchange("member3", "member2", currentYear-1));
        giftExchangeDataSet.add(buildGiftExchange("member3", "member1", currentYear-2));

        //building assertion map
        Map<String, String> assertionMap = new HashMap<>();
        assertionMap.put("member0", "member2");
        assertionMap.put("member1", "member3");
        assertionMap.put("member2", "member1");
        assertionMap.put("member3", "member0");
        //the result for current year would not change no matter how many times gifts are shuffled
        //lets reshuffle 5 time and see result is same
        for(int i=0; i< 5; i++) {
            List<GiftExchangeDto> giftExchangeDtoList =  giftExchangeService.giftExchangeShufflePost();
            Set<String> receivers = new HashSet<>();

            assertEquals(memberDataSet.size(), giftExchangeDtoList.size());
            giftExchangeDtoList.stream().forEach(giftExchangeDto -> {
                assertTrue(!receivers.contains(giftExchangeDto.getReceiverId()));
                assertTrue(!giftExchangeDto.getGiverId().equals(giftExchangeDto.getReceiverId()));
                receivers.add(giftExchangeDto.getReceiverId());
                assertEquals(assertionMap.get(giftExchangeDto.getGiverId()), giftExchangeDto.getReceiverId());
            });
        }

    }

    /**
     * This test is to ensure no matter how many times the gifts are shuffled,
     * the gift exchanges will be overridden but not new gift exchange records added fot any specific year
     */
    @Test
    @Order(4)
    public void testReShuffleExchangeGifts() {
        //the memberDataSet will be empty when this individual test ran
        if(memberDataSet.isEmpty()){
            //building 4 members
            for(int i=0; i<4 ; i++) {
                memberDataSet.add(buildMemberData("member"+i, "name"+i));
            }
        }
        int currentYear =  Calendar.getInstance().get(Calendar.YEAR);
        giftExchangeService.giftExchangeShufflePost();

        //assert gift exchanges count is same as members count
        assertEquals(memberDataSet.size(), giftExchangeDataSet.stream()
                .filter(giftExchange -> giftExchange.getGiftYear()==currentYear)
                .count());

        //reshuffle again
        giftExchangeService.giftExchangeShufflePost();

        //assert gift exchanges count is same as members count
        assertEquals(memberDataSet.size(), giftExchangeDataSet.stream()
                .filter(giftExchange -> giftExchange.getGiftYear()==currentYear)
                .count());

    }

    @Test
    @Order(5)
    public void testGetRecentGiftExchange() {
        if(memberDataSet.isEmpty()){
            //building 4 members
            for(int i=0; i<4 ; i++) {
                memberDataSet.add(buildMemberData("member"+i, "name"+i));
            }
        }
        //shuffle gift exchange
        Map<String, String> giftExchangeMap =
                giftExchangeService.giftExchangeShufflePost().stream()
                        .collect(Collectors.toMap(GiftExchangeDto::getGiverId, GiftExchangeDto::getReceiverId));

        //get recent exchange data after shuffling
        List<GiftExchangeDto> recentGiftExchangeList= giftExchangeService.getRecentGiftExchange();

        assertEquals(memberDataSet.size(), recentGiftExchangeList.size());
        recentGiftExchangeList.stream()
                        .forEach(giftExchangeDto -> {
                            //assert shuffled exchange with recent exchange
                            assertTrue(giftExchangeDto.getReceiverId().equals(giftExchangeMap.get(giftExchangeDto.getGiverId())));
                        });

    }


    private Member buildMemberData(String memberId, String name) {
        Member member = new Member();
        member.setMemberId(memberId);
        member.setName(name);

        return member;
    }


    private GiftExchange buildGiftExchange(String giverId, String receiverId, int year) {
        GiftExchange giftExchange = new GiftExchange();
        giftExchange.setGiftYear(year);
        giftExchange.setGiverId(giverId);
        giftExchange.setReceiverId(receiverId);

        return giftExchange;
    }





}
