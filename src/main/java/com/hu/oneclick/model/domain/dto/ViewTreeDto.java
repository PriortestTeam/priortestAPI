package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.entity.View;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingyang
 */
public class ViewTreeDto extends View implements Serializable {

    List<ViewTreeDto> childViews;

    public List<ViewTreeDto> getChildViews() {
        return childViews;
    }

    public void setChildViews(List<ViewTreeDto> childViews) {
        this.childViews = childViews;
    }
}
