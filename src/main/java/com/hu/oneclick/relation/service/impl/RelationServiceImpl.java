package com.hu.oneclick.relation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.relation.domain.Relation;
import com.hu.oneclick.relation.dao.RelationDao;
import com.hu.oneclick.relation.enums.RelationCategoryEnum;
import com.hu.oneclick.relation.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RelationServiceImpl extends ServiceImpl<RelationDao, Relation> implements RelationService {

    @Resource
    @Autowired
    private RelationDao relationDao;

    @Transactional(rollbackFor = Exception.class)
    public void saveRelation(String objectId, String targetId, String category, String extJson, boolean clear) {
        // 是否需要先删除关系
        if(clear) {
            this.remove(new LambdaQueryWrapper<Relation>().eq(Relation::getObjectId, objectId)
                    .eq(Relation::getCategory, category));
        }
        Relation Relation = new Relation();
        Relation.setObjectId(objectId);
        Relation.setTargetId(targetId);
        Relation.setCategory(category);

        this.save(Relation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRelationBatch(String objectId, List<String> targetIdList, String category, boolean clear) {
        // 是否需要先删除关系
        if(clear) {
            this.remove(new LambdaQueryWrapper<Relation>().eq(Relation::getObjectId, objectId)
                    .eq(Relation::getCategory, category));
        }
        List<Relation> RelationList = CollectionUtil.newArrayList();
        for(int i = 0; i < targetIdList.size(); i++) {
            Relation Relation = new Relation();
            Relation.setObjectId(objectId);
            Relation.setTargetId(targetIdList.get(i));
            Relation.setCategory(category);

            RelationList.add(Relation);
        }
        if(ObjectUtil.isNotEmpty(RelationList)) {
            this.saveBatch(RelationList);
        }
    }

    @Override
    public void saveRelationWithAppend(String objectId, String targetId, String category) {
        this.saveRelation(objectId, targetId, category, null, false);
    }

    @Override
    public void saveRelationWithAppend(String objectId, String targetId, String category, String extJson) {
        this.saveRelation(objectId, targetId, category, extJson, false);
    }

    @Override
    public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category) {
        this.saveRelationBatch(objectId, targetIdList, category, null, false);
    }

    @Override
    public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
        this.saveRelationBatch(objectId, targetIdList, category, extJsonList, false);
    }

    @Override
    public void saveRelationWithClear(String objectId, String targetId, String category) {
        this.saveRelation(objectId, targetId, category, null, true);
    }

    @Override
    public void saveRelationWithClear(String objectId, String targetId, String category, String extJson) {
        this.saveRelation(objectId, targetId, category, extJson, true);
    }

    @Override
    public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category) {
        this.saveRelationBatch(objectId, targetIdList, category, null, true);
    }

    @Override
    public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
        this.saveRelationBatch(objectId, targetIdList, category, extJsonList, true);
    }

    @Override
    public List<Relation> getRelationListByObjectId(String objectId) {
        return this.getRelationListByObjectIdAndCategory(objectId, null);
    }

    @Override
    public List<Relation> getRelationListByObjectIdList(List<String> objectIdList) {
        return this.getRelationListByObjectIdListAndCategory(objectIdList, null);
    }

    @Override
    public List<Relation> getRelationListByObjectIdAndCategory(String objectId, String category) {
        LambdaQueryWrapper<Relation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Relation::getObjectId, objectId);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(Relation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<Relation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category) {
        LambdaQueryWrapper<Relation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Relation::getObjectId, objectIdList);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(Relation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<Relation> getRelationListByTargetId(String targetId) {
        return this.getRelationListByTargetIdAndCategory(targetId, null);
    }

    @Override
    public List<Relation> getRelationListByTargetIdList(List<String> targetIdList) {
        return this.getRelationListByTargetIdListAndCategory(targetIdList, null);
    }

    @Override
    public List<Relation> getRelationListByTargetIdAndCategory(String targetId, String category) {
        LambdaQueryWrapper<Relation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Relation::getTargetId, targetId);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(Relation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<Relation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category) {
        LambdaQueryWrapper<Relation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Relation::getTargetId, targetIdList);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(Relation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectId(String objectId) {
        return this.getRelationTargetIdListByObjectIdAndCategory(objectId, null);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList) {
        return this.getRelationTargetIdListByObjectIdListAndCategory(objectIdList, null);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category) {
        return this.getRelationListByObjectIdAndCategory(objectId, category).stream()
                .map(Relation::getTargetId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category) {
        return this.getRelationListByObjectIdListAndCategory(objectIdList, category).stream()
                .map(Relation::getTargetId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationObjectIdListByTargetId(String targetId) {
        return this.getRelationObjectIdListByTargetIdAndCategory(targetId, null);
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList) {
        return this.getRelationObjectIdListByTargetIdListAndCategory(targetIdList, null);
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category) {
        return this.getRelationListByTargetIdAndCategory(targetId, category).stream()
                .map(Relation::getObjectId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category) {
        return this.getRelationListByTargetIdListAndCategory(targetIdList, category).stream()
                .map(Relation::getObjectId).collect(Collectors.toList());
    }

    @Override
    public List<Relation> getRelationListWithTitleByObjectIdAndCategory(String objectId, String category) {
        return relationDao.getRelationListWithTitleByObjectIdAndCategory(objectId, category);
    }

    @Override
    public int removeBatchByTestCaseIds(List<Long> testCasesIds) {
        return relationDao.delete(new LambdaQueryWrapper<Relation>()
                .in(Relation::getObjectId, testCasesIds)
                .or().in(Relation::getTargetId, testCasesIds));
    }

    /**
     * 根据id、categoty查询relation
     *
     * @param testCaseId
     * @return
     */
    @Override
    public Map<String, Object> getRelationListByObjectIdAndTargetIdAndCategory(Long testCaseId) {

        String[] categorys
                = new String[]{RelationCategoryEnum.ISSUE_TEST_CASE.getValue(), RelationCategoryEnum.TEST_CASE_TO_ISSUE.getValue()};
        List<Relation> relationList = relationDao.getRelationListByObjectIdAndTargetIdAndCategory(testCaseId, categorys);
        Set<String> issueIds = relationList.stream().flatMap(obj -> Stream.of(obj.getTargetId(), obj.getObjectId()))
                .collect(Collectors.toSet());
        issueIds.remove(String.valueOf(testCaseId));
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("testCaseId", testCaseId);
        resultMap.put("issueId", issueIds);
        return resultMap;
    }
}
