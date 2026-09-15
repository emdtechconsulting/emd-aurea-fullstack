package com.emdtech.aurea.dto;

public record CategoryResponse(

        Long id,
        String name,
        Boolean active,
        Integer displayOrder

) {
}
