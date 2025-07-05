package com.hu.oneclick.model.domain.dto;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@Data

public class ProjectManageSaveDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING);
    @Schema(description = "主键id");
    private Long id;
    @NotBlank(message = "标题不能为空");
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    private Date planReleaseDate;
    private String description;
    private String reportTo;
    private String testFrame;
    private String projectCategory;
    private String customer;
    private String projectStatus;
    private String remarks;
    private String repoName;
    /**
     * 自定义字段值
     */
    @Schema(description = "自定义字段值");
    private JSONObject customFieldDatas;
}
}
}
