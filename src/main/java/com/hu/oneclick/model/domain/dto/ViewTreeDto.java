package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.entity.View;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingyang
 */


public class ViewTreeDto extends View implements Serializable {

    List&lt;ViewTreeDto> childViews;

    public List&lt;ViewTreeDto> getChildViews() {
        return childViews;
    }

    public void setChildViews(List&lt;ViewTreeDto> childViews) {
        this.childViews = childViews;
    }
}
}
