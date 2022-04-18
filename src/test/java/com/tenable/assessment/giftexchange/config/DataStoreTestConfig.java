package com.tenable.assessment.giftexchange.config;

import com.tenable.assessment.giftexchange.domain.GiftExchange;
import com.tenable.assessment.giftexchange.domain.Member;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@TestConfiguration
@ComponentScan("com.tenable.assessment")
public class DataStoreTestConfig {
    @Bean(name = "memberDataSet")
    public Set<Member> memberDataSet(){
        //creating thread safe data store
        return Collections.synchronizedSet(new HashSet<>());

    }

    @Bean(name = "giftExchangeDataSet")
    public Set<GiftExchange> giftExchangeDataSet(){
        //creating thread safe data store
        return Collections.synchronizedSet(new HashSet<>());

    }
}
