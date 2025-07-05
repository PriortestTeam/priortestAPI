package com.hu.oneclick.model.param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.model.entity.ProjectManage;
import lombok.Data;
import java.io.Serializable;
/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@Data


public class ProjectManageParam implements Serializable {
    private Long roomId;
    public Wrapper<ProjectManage> getQueryCondition() {
        LambdaQueryWrapper<ProjectManage> queryWrapper = new LambdaQueryWrapper();
        if (null != this.roomId) {
            queryWrapper.eq(ProjectManage::getRoomId, this.roomId);
        }
        return queryWrapper;
    }
}
}
}
