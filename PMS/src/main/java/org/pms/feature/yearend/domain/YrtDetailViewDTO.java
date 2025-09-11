package org.pms.feature.yearend.domain;

import lombok.Data;

@Data
public class YrtDetailViewDTO {
    private String category;         // YRT_ITEM.CATEGORY
    private String itemName;         // YRT_ITEM.ITEM_NAME
    private int amount;       // YRT_DETAIL.AMOUNT
    private int expectedAmount; // YRT_DETAIL.EXPECTED_AMOUNT
}
