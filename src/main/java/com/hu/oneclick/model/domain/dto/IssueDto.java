package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.entity.Issue;

/**
 * @author qingyang
 */
public class IssueDto extends Issue {

    private ViewTreeDto viewTreeDto;

    private String filter;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public ViewTreeDto getViewTreeDto() {
        return viewTreeDto;
    }

    public void setViewTreeDto(ViewTreeDto viewTreeDto) {
        this.viewTreeDto = viewTreeDto;
    }

}
