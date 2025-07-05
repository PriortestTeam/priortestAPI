
package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Date;

@Getter
@Setter
@Data
@TableName("uitest_git_settings")
public class UITestGitSettings {
    
    @TableId
    private BigInteger id;
    
    private BigInteger roomId;
    
    private String username;
    
    private String passwd;
    
    private String remoteUrl;
    
    private Date createTime;
    
    private Date updateTime;
}
