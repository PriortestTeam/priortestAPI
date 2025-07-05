package com.hu.oneclick.server.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.param.TestCaseParam;
import java.util.List;
import java.util.Map;
import com.hu.oneclick.model.domain.vo.IssueStatusVo;
import org.springframework.web.multipart.MultipartFile;
import com.github.pagehelper.PageInfo;
/**
 * @author qingyang
 */
  Resp<ImportTestCaseDto> importTestCase(MultipartFile file, String param);
