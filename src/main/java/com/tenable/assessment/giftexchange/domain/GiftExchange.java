package com.tenable.assessment.giftexchange.domain;

import lombok.Data;

import java.util.Objects;

/**
 * This class can be used joa entity class when connects to actual database
 */
//using lombok annotation for now since this entity is not mapped to any db table
@Data
public class GiftExchange {
    private String giverId;
    private String receiverId;
    private Integer giftYear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftExchange that = (GiftExchange) o;
        return Objects.equals(this.getGiverId(), that.getGiverId())
                && Objects.equals(this.getReceiverId(), that.getReceiverId())
                && Objects.equals(this.getGiftYear(), that.getGiftYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{getGiverId(), getReceiverId(), getGiftYear()});
    }
}
