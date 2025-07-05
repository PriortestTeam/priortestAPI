package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.ViewTreeDto;

/**
 * @author qingyang
 */
public interface QueryFilterService {

    String mysqlFilterProcess(ViewTreeDto viewTr,String masterId);

}
