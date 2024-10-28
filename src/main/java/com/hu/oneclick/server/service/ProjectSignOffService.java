package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.ProjectSignOff;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2022/1/15
 * @since JDK 1.8.0
 */
public interface ProjectSignOffService {

    Resp<List<ProjectSignOff>> getPdf();
}
