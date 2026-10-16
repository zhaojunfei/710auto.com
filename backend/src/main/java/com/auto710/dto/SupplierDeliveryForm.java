package com.auto710.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商提交送货单的表单
 */
@Data
@Schema(description = "供应商送货单提交表单")
public class SupplierDeliveryForm {

    @Schema(description = "收货门店ID")
    private Long storeId;

    @Schema(description = "预计到货日期 yyyy-MM-dd")
    private String expectDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "送货明细")
    private List<Item> items;

    @Data
    @Schema(description = "送货明细行")
    public static class Item {

        @Schema(description = "配件ID（可为空，表示新配件）")
        private Long partId;

        @Schema(description = "配件名称")
        private String partName;

        @Schema(description = "规格型号")
        private String spec;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "送货数量")
        private Integer quantity;

        @Schema(description = "供货单价")
        private BigDecimal unitPrice;

        @Schema(description = "备注")
        private String remark;
    }
}
