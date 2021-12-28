package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.dao.IssueJoinTestCaseDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.CustomFieldData;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.IssueJoinTestCase;
import com.hu.oneclick.model.domain.ModifyRecord;
import com.hu.oneclick.model.domain.dto.IssueDto;
import com.hu.oneclick.server.service.CustomFieldDataService;
import com.hu.oneclick.server.service.IssueService;
import com.hu.oneclick.server.service.ModifyRecordsService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {

    private final static Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueDao issueDao;

    private final JwtUserServiceImpl jwtUserService;

    private final IssueJoinTestCaseDao issueJoinTestCaseDao;

    private final ModifyRecordsService modifyRecordsService;

    private final SysPermissionService sysPermissionService;

    private final QueryFilterService queryFilterService;

    private final CustomFieldDataService customFieldDataService;


    public IssueServiceImpl(IssueDao issueDao, JwtUserServiceImpl jwtUserService, IssueJoinTestCaseDao issueJoinTestCaseDao, ModifyRecordsService modifyRecordsService, SysPermissionService sysPermissionService, QueryFilterService queryFilterService, CustomFieldDataService customFieldDataService) {
        this.issueDao = issueDao;
        this.jwtUserService = jwtUserService;
        this.issueJoinTestCaseDao = issueJoinTestCaseDao;
        this.modifyRecordsService = modifyRecordsService;
        this.sysPermissionService = sysPermissionService;
        this.queryFilterService = queryFilterService;
        this.customFieldDataService = customFieldDataService;
    }


    /**
     * update issue custom
     *
     * @Param: [id]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.Issue>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public Resp<Issue> queryById(String id) {
        String masterId = jwtUserService.getMasterId();
        Issue issue = issueDao.queryById(id, masterId);

        issue.setCustomFieldDatas(customFieldDataService.issueRenderingCustom(id));
        return new Resp.Builder<Issue>().setData(issue).ok();
    }

    @Override
    public Resp<List<Issue>> queryList(IssueDto issue) {
        issue.queryListVerify();
        String masterId = jwtUserService.getMasterId();
        issue.setUserId(masterId);

        issue.setFilter(queryFilterService.mysqlFilterProcess(issue.getViewTreeDto(), masterId));

        List<Issue> select = issueDao.queryList(issue);
        return new Resp.Builder<List<Issue>>().setData(select).total(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> insert(Issue issue) {
        try {
            //验证参数
            issue.verify();
            //验证是否存在
            verifyIsExist(issue.getTitle(), issue.getProjectId());
            issue.setUserId(jwtUserService.getMasterId());
            issue.setAuthor(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            Date date = new Date();
            issue.setCreateTime(date);
            issue.setUpdateTime(date);
            int insertFlag = issueDao.insert(issue);
            if (insertFlag > 0) {
                List<CustomFieldData> customFieldDatas = issue.getCustomFieldDatas();
                insertFlag = customFieldDataService.insertIssueCustomData(customFieldDatas, issue);
            }
            return Result.addResult(insertFlag);
        } catch (BizException e) {
            logger.error("class: IssueServiceImpl#insert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> update(Issue issue) {
        try {
            //验证是否存在
            verifyIsExist(issue.getTitle(), issue.getProjectId());
            issue.setUserId(jwtUserService.getMasterId());
            //新增修改字段记录
            modifyRecord(issue);
            return Result.updateResult(issueDao.update(issue));
        } catch (BizException e) {
            logger.error("class: IssueServiceImpl#update,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> delete(String id) {
        try {
            Issue issue = new Issue();
            issue.setId(id);
            return Result.deleteResult(issueDao.delete(issue));
        } catch (BizException e) {
            logger.error("class: IssueServiceImpl#delete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<List<Issue>> queryBindCaseList(String issueId) {
        List<Issue> select = issueJoinTestCaseDao.queryBindCaseList(issueId);
        return new Resp.Builder<List<Issue>>().setData(select).total(select).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindCaseInsert(IssueJoinTestCase issueJoinTestCase) {
        try {
            return Result.addResult(issueJoinTestCaseDao.insert(issueJoinTestCase));
        } catch (BizException e) {
            logger.error("class: IssueServiceImpl#bindCaseInsert,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> bindCaseDelete(String testCaseId) {
        try {
            return Result.deleteResult(issueJoinTestCaseDao.bindCaseDelete(testCaseId));
        } catch (BizException e) {
            logger.error("class: IssueServiceImpl#bindCaseDelete,error []" + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }


    /**
     * 修改字段，进行记录
     *
     * @param issue
     */
    private void modifyRecord(Issue issue) {
        try {
            Issue query = issueDao.queryById(issue.getId(), issue.getUserId());
            if (query == null) {
                throw new RuntimeException();
            }

            Field[] fields = issue.getClass().getDeclaredFields();

            Field[] fields2 = query.getClass().getDeclaredFields();
            List<ModifyRecord> modifyRecords = new ArrayList<>();
            for (int i = 0, len = fields.length; i < len; i++) {
                String field = fields[i].getName(); //获取字段名

                fields[i].setAccessible(true);
                fields2[i].setAccessible(true);

                if (field.equals("id")
                        || field.equals("projectId")
                        || field.equals("userId")
                        || field.equals("updateTime")
                        || field.equals("createTime")
                        || field.equals("scope")
                        || field.equals("serialVersionUID")
                        || field.equals("description")
                        || fields[i].get(issue) == null
                        || fields[i].get(issue) == "") {
                    continue;
                }

                String after = fields[i].get(issue).toString(); //获取用户需要修改的字段
                String before = fields2[i].get(query) == null || fields2[i].get(query) == ""
                        ? "" : fields2[i].get(query).toString();//获取数据库的原有的字段

                //值不相同
                if (!before.equals(after)) {

                    ModifyRecord mr = new ModifyRecord();
                    mr.setProjectId(query.getProjectId());
                    mr.setUserId(query.getUserId());
                    mr.setScope(OneConstant.SCOPE.ONE_ISSUE);
                    mr.setModifyDate(new Date());
                    mr.setModifyUser(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
                    mr.setBeforeVal(before);
                    mr.setAfterVal(after);
                    mr.setLinkId(query.getId());
                    mr.setModifyField(getCnField(field));
                    modifyRecords.add(mr);
                }
            }
            if (modifyRecords.size() <= 0) {
                return;
            }
            modifyRecordsService.insert(modifyRecords);
        } catch (IllegalAccessException e) {
            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(), "修改字段新增失败！");
        }
    }


    /**
     * 获取字段对应中文字义
     *
     * @param args
     * @return
     */
    private String getCnField(String args) {
        switch (args) {
            case "title":
                return "名称";
            case "author":
                return "创建人";
            case "plannedReleaseDate":
                return "计划发行日期";
            case "status":
                return "状态";
            case "closeDate":
                return "关闭日期";
            case "testCase":
                return "关联测试用例";
            case "testCycle":
                return "关联测试周期";
            case "priority":
                return "优先级";
            case "version":
                return "版本";
            case "env":
                return "环境";
            case "browser":
                return "浏览器";
            case "platform":
                return "平台";
            case "caseCategory":
                return "用例分类";
        }
        return args;
    }

    /**
     * 查重
     */
    private void verifyIsExist(String title, String projectId) {
        if (StringUtils.isEmpty(title)) {
            return;
        }
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setProjectId(projectId);
        issue.setId(null);
        if (issueDao.selectOne(issue) != null) {
            throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), issue.getTitle() + SysConstantEnum.DATE_EXIST.getValue());
        }
    }
}
