package com.hu.oneclick.server.service.impl;

import com.google.common.base.CaseFormat;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.domain.OneFilter;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.server.service.QueryFilterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class QueryFilterServiceImpl implements QueryFilterService {

    private final ViewDao viewDao;

    public QueryFilterServiceImpl(ViewDao viewDao) {
        this.viewDao = viewDao;
    }

    @Override
    public String mysqlFilterProcess(ViewTreeDto viewTr,String masterId) {
        if (viewTr == null){
            return null;
        }
        List<ViewTreeDto> viewTreeDtoList = viewDao.queryViewTreeById(masterId, viewTr.getId().toString());
        if (viewTreeDtoList == null || viewTreeDtoList.size() <= 0){
            return null;
        }

        StringBuilder rs = new StringBuilder();
        //1 找出自己及所有后代的Filter
        List<View> listView = antiRecursion(viewTreeDtoList.get(0),null);
        //2 取 对象
        for (View view : listView) {
            List<OneFilter> oneFilters = TwoConstant.convertToList(view.getFilter(), OneFilter.class);
            //3 根据字段类型进行sql 拼接
            rs.append(antiOneFilter(oneFilters));
            rs.append(" ");
        }
        return rs.toString();
    }

    /**
     * 反解oneFilters
     */
    private String antiOneFilter(List<OneFilter> oneFilters){
        StringBuilder rs = new StringBuilder();
        if (oneFilters == null || oneFilters.size() <=0){
            return "";
        }
        for (OneFilter oneFilter : oneFilters) {
            if (oneFilter == null){continue;}
            //驼峰转下划线
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, oneFilter.getFieldName());
            //4 取type
            switch (oneFilter.getType()){
                case "fString":
                    if (StringUtils.isEmpty(oneFilter.getTextVal())){continue;}
                    rs.append(oneFilter.getAndOr())
                            .append(" ");
                    String buildFString = fieldName
                            + " like concat('%',"
                            + "'" + oneFilter.getTextVal() + "','%')";
                    rs.append(buildFString);
                    break;
                case "fInteger":
                    rs.append(oneFilter.getAndOr())
                            .append(" ");
                    String buildFInteger = fieldName
                            + " = "
                            + oneFilter.getIntVal();
                    rs.append(buildFInteger);
                    break;
                case "fDateTime":
                    rs.append(oneFilter.getAndOr())
                            .append(" ");
                    String buildFDate = fieldName  + " >= '" + oneFilter.getBeginDate() + "'"
                            + " and " +
                            fieldName  + " <= '" + oneFilter.getEndDate() + "'";
                    rs.append(buildFDate);
                    break;
                default:
                    break;
            }
        }
        return rs.toString();
    }


    /**
     * 反解递归
     * @param viewTr
     * @param listView
     * @return
     */
    private List<View> antiRecursion(ViewTreeDto viewTr, List<View> listView){
        if (viewTr == null){return null;}
        if (listView == null){
            listView = new ArrayList<>();
        }
        View view = new View();
        BeanUtils.copyProperties(viewTr,view);
        listView.add(0,view);

        List<ViewTreeDto> childViews = viewTr.getChildViews();
        if(childViews != null){
            for (ViewTreeDto childView : childViews) {
                List<View> resultViews = antiRecursion(childView, listView);
            }
        }
        return listView;
    }
}
