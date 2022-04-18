package com.tenable.assessment.giftexchange.config;

import com.tenable.assessment.giftexchange.domain.GiftExchange;
import com.tenable.assessment.giftexchange.domain.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class DataStoreConfig {

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
