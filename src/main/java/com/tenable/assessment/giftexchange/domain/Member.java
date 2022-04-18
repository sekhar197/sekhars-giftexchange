package com.tenable.assessment.giftexchange.domain;

import lombok.Data;

import java.util.Objects;

/**
 * This class can be used joa entity class when connects to actual database
 */
//using lombok annotation for now since this entity is not mapped to any db table
@Data
public class Member {

    //would use UUID for id field but using string to keep it simple
    private String memberId;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return getMemberId().equals(member.getMemberId()) && getName().equals(member.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMemberId(), getName());
    }
}
