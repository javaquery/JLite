package com.javaquery.poi.excel.model;

import com.javaquery.annotations.Exportable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.*;

/**
 * Test model class with various data types to test ExcelWriter's setCellValue method.
 * @author vicky.thakor
 * @since 2025-12-30
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Exportable(key = "name")
    private String name;

    @Exportable(key = "description", isRichText = true)
    private String description;

    @Exportable(key = "price")
    private Double price;

    @Exportable(key = "quantity")
    private Integer quantity;

    @Exportable(key = "discount")
    private Float discount;

    @Exportable(key = "weight")
    private Long weight;

    @Exportable(key = "available")
    private Boolean available;

    @Exportable(key = "manufacturedDate")
    private Date manufacturedDate;

    @Exportable(key = "expiryDate")
    private LocalDateTime expiryDate;

    @Exportable(key = "totalPrice", isFormula = true)
    private String totalPrice; // Formula: price * quantity

    @Exportable(key = "rating")
    private Byte rating;

    @Exportable(key = "views")
    private Short views;
}

