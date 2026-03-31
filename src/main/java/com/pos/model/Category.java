package com.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
    private int categoryId;
    private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
