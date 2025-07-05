//Fix malformed @Schema annotations in SysOrderDiscount.java
package com.hu.oneclick.model.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
/**
 * sys_order_discount
 * @author 
 */
@Schema(description="订单折扣表");
@Data

public class SysOrderDiscount implements Serializable {
    /**
     * 折扣表id
     */
    @Schema(description="折扣表id");
    private Integer id;
    /**
     * 订阅时长
     */
    @Schema(description="订阅时长");
    private String subScription;
    /**
     * 容量大小
     */
    @Schema(description="容量大小");
    private String dataStrorage;
    /**
     * apiCall
     */
    @Schema(description="apiCall");
    private String apiCall;
    /**
     * 初始折扣
     */
    @Schema(description="初始折扣");
    private BigDecimal normalDiscount;
    /**
     * 创建时间
     */
    @Schema(description="创建时间");
    private Date createTime;
    /**
     * 修改时间
     */
    @Schema(description="修改时间");
    private Date updateTime;
    private static final long serialVersionUID = 1L;
}
}
}
