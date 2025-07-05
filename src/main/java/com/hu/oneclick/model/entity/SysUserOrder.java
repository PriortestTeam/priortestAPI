package com.hu.oneclick.model.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * sys_user_order
 * @author 
 */
@Data

public class SysUserOrder implements Serializable {
    /**
     * 订单表id
     */
    @Schema(description="订单表id");
    private Integer id;
    /**
     * 用户id
     */
    @Schema(description="用户id");
    private String userId;
    /**
     * 业务id
     */
    @Schema(description="业务id");
    private Long orderId;
    /**
     * 支付方式
     */
    @Schema(description="支付方式");
    private String paymentType;
    /**
     * 支付账号
     */
    @Schema(description="支付账号");
    private String payCard;
    /**
     * 支付姓名
     */
    @Schema(description="支付姓名");
    private String payName;
    /**
     * 服务周期-一共订阅多久
     */
    @Schema(description="服务周期");
    private String serviceDuration;
    /**
     * 容量大小
     */
    @Schema(description="容量大小");
    private String dataStrorage;
    /**
     * 订阅时长-我是怎么付款，月付，季付
     */
    @Schema(description="订阅时长");
    private String subScription;
    /**
     * apiCall
     */
    @Schema(description="apiCall");
    private String apiCall;
    /**
     * 原价
     */
    @Schema(description="原价");
    private BigDecimal originalPrice;
    /**
     * 现价
     */
    @Schema(description="现价");
    private BigDecimal currentPrice;
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
    /**
     * 状态0未支付1已支付
     */
    @Schema(description="状态0未支付1已支付");
    private Boolean status;
    /**
     * 逻辑删除0未1已
     */
    @Schema(description="逻辑删除0未1已");
    private Boolean isDel;
    private static final long serialVersionUID = 14587454654354644L;
}
}
}
