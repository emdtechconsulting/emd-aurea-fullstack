package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(
        name = "PagedOrderResponse",
        description = "Respuesta paginada utilizada para consultar pedidos"
)
public class PagedOrderResponse {

    @Schema(
            description = "Pedidos contenidos en la página actual"
    )
    private List<OrderResponse> content;


    @Schema(
            description = "Número de página actual. La primera página es 0.",
            example = "0"
    )
    private int page;


    @Schema(
            description = "Cantidad máxima de pedidos por página",
            example = "10"
    )
    private int size;


    @Schema(
            description = "Cantidad total de pedidos encontrados",
            example = "125"
    )
    private long totalElements;


    @Schema(
            description = "Cantidad total de páginas disponibles",
            example = "13"
    )
    private int totalPages;


    @Schema(
            description = "Indica si esta es la primera página",
            example = "true"
    )
    private boolean first;


    @Schema(
            description = "Indica si esta es la última página",
            example = "false"
    )
    private boolean last;


    public List<OrderResponse> getContent() {
        return content;
    }

    public void setContent(
            List<OrderResponse> content) {

        this.content = content;
    }


    public int getPage() {
        return page;
    }

    public void setPage(
            int page) {

        this.page = page;
    }


    public int getSize() {
        return size;
    }

    public void setSize(
            int size) {

        this.size = size;
    }


    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(
            long totalElements) {

        this.totalElements =
                totalElements;
    }


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(
            int totalPages) {

        this.totalPages =
                totalPages;
    }


    public boolean isFirst() {
        return first;
    }

    public void setFirst(
            boolean first) {

        this.first = first;
    }


    public boolean isLast() {
        return last;
    }

    public void setLast(
            boolean last) {

        this.last = last;
    }
}