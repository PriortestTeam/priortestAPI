package com.hu.oneclick.model.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * sys_user_order_record
 *
 * @author
 */
@Schema(description = "com.hu.oneclick.model.domain.SysUserOrderRecord订单记录表");
@Data
public class SysUserOrderRecord implements Serializable {
    /**
     * id
     */
    @Schema(description = "id");
    private Integer id;
    /**
     * 订单id
     */
    @Schema(description = "订单id");
    private Long order_id;
    /**
     * 原价
     */
    @Schema(description = "原价");
    private BigDecimal original_price;
    /**
     * 折扣价
     */
    @Schema(description = "折扣价");
    private BigDecimal discount_price;
    /**
     * 支付状态
     */
    @Schema(description = "支付状态");
    private Boolean status;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间");
    private Date create_time;
    /**
     * 是否删除
     */
    @Schema(description = "是否删除");
    private Boolean is_del;
    /**
     * 支付时间
     */
    @Schema(description = "支付时间");
    private Date payment_time;
    /**
     * 支付方式
     */
    @Schema(description = "支付方式");
    private String payment_type;
    /**
     * 服务周期
     */
    @Schema(description = "服务周期");
    private String service_plan_duration;
    /**
     * 容量大小
     */
    @Schema(description = "容量大小");
    private String data_strorage;
    /**
     * 容量价格
     */
    @Schema(description = "容量价格");
    private BigDecimal data_price;
    /**
     * apiCall
     */
    @Schema(description = "apiCall");
    private String api_call;
    /**
     * apiCall价格
     */
    @Schema(description = "apiCall价格");
    private BigDecimal api_call_price;
    /**
     * 采购模式
     */
    @Schema(description = "采购模式");
    private String sub_scription;
    /**
     * 折扣
     */
    @Schema(description = "折扣");
    private BigDecimal discount;
    /**
     * 实际支付
     */
    @Schema(description = "实际支付");
    private BigDecimal expenditure;
    /**
     * 发票转态
     */
    @Schema(description = "发票转态");
    private Boolean invoice;
    private static final long serialVersionUID = 1L;
}
}
}
