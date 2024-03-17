package com.hu.oneclick.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.relation.domain.Relation;
import java.util.List;

public interface RelationService extends IService<Relation> {

    /**
     * 追加关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationWithAppend(String objectId, String targetId, String category);

    /**
     * 追加关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

    /**
     * 批量追加关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

    /**
     * 批量追加关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

    /**
     * 清空原关系并保存关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationWithClear(String objectId, String targetId, String category);

    /**
     * 清空原关系并保存关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

    /**
     * 清空原关系并批量保存关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

    /**
     * 清空原关系并批量保存关系
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

    /**
     * 根据对象id获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByObjectId(String objectId);

    /**
     * 根据对象id集合获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByObjectIdList(List<String> objectIdList);

    /**
     * 根据对象id和关系分类获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByObjectIdAndCategory(String objectId, String category);

    /**
     * 根据对象id集合和关系分类获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

    /**
     * 根据目标id获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByTargetId(String targetId);

    /**
     * 根据目标id集合获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByTargetIdList(List<String> targetIdList);

    /**
     * 根据目标id和关系分类获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByTargetIdAndCategory(String targetId, String category);

    /**
     * 根据目标id集合和关系分类获取关系列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<Relation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

    /**
     * 根据对象id获取目标id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationTargetIdListByObjectId(String objectId);

    /**
     * 根据对象id集合获取目标id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

    /**
     * 根据对象id和关系分类获取目标id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

    /**
     * 根据对象id集合和关系分类获取目标id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

    /**
     * 根据目标id获取对象id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationObjectIdListByTargetId(String targetId);

    /**
     * 根据目标id集合获取对象id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

    /**
     * 根据目标id和关系分类获取对象id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

    /**
     * 根据目标id集合和关系分类获取对象id列表
     *
     *  @author xiaohai
     * @date 2023/06/05
     */
    List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);

    /**
     * 清空原关系
     * @param testCasesIds
     */
    void removeBatchByTestCaseIds(List<Long> testCasesIds);
}
