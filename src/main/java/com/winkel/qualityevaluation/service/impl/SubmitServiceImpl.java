package com.winkel.qualityevaluation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winkel.qualityevaluation.dao.SubmitDao;
import com.winkel.qualityevaluation.entity.task.EvaluateSubmit;
import com.winkel.qualityevaluation.service.api.SubmitService;
import com.winkel.qualityevaluation.pojo.dto.CountDTO;
import com.winkel.qualityevaluation.pojo.vo.Index2Vo;
import com.winkel.qualityevaluation.pojo.dto.ScoreDTO;
import com.winkel.qualityevaluation.pojo.vo.ScoreVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmitServiceImpl extends ServiceImpl<SubmitDao, EvaluateSubmit> implements SubmitService {

    @Autowired
    private SubmitDao submitDao;

    @Override
    public List<EvaluateSubmit> getALlSubmitByUserId(String userId) {
        return submitDao.selectAllSubmitByUserId(userId);
    }

    @Override
    public Double getSumByIndex1IdAndCountycode(Integer index1Id, String countyCode) {
        return submitDao.selectSumByIndex1IdAndCountycode(index1Id, countyCode);
    }

    @Override
    public Double getSumByIndex1IdAndCitycode(Integer index1Id, String cityCode) {
        return submitDao.selectSumByIndex1IdAndCitycode(index1Id, cityCode);
    }

    @Override
    public Double getSumByIndex1IdAndProvincecode(Integer index1Id, String provinceCode) {
        return submitDao.selectSumByIndex1IdAndProvincecode(index1Id, provinceCode);
    }

    @Override
    public Double getCountByCountycode(String countyCode) {
        return submitDao.selectCountByCountycode(countyCode);
    }

//    @Override
//    public Double getCountByCitycode(String cityCode) {
//        return submitDao.selectCountByCitycode(cityCode);
//    }
//
//    @Override
//    public Double getCountByProvincecode(String provinceCode) {
//        return submitDao.selectCountByProvincecode(provinceCode);
//    }

    /**
     * desc: 获取县域内各学校在不同督评完成情况下5项一级指标的得分
     * params: [countycode, taskStatus] countycode： taskStatus  taskStatus：
     * return: java.util.List<com.winkel.qualityevaluation.vo.ScoreVo>
     * exception:
     *
     * @param countycode 县的地址码
     * @param taskStatus 督评完成状态
     */
    @Override
    public List<ScoreVo> getIndex1ScoreByCountycode(String countycode, Integer taskStatus) {
        return submitDao.selectIndex1ScoreByCountycode(countycode, taskStatus);
    }

    /**
     * desc: 获取县域内各学校在不同督评完成情况下一级指标的总分，并按降序排序
     * params: [countycode, taskStatus] countycode：县的地址码 taskStatus  taskStatus：督评完成状态
     * return: java.util.List<com.winkel.qualityevaluation.vo.ScoreVo>
     * exception:
     *
     * @param countycode 县的地址码
     * @param taskStatus 督评完成状态
     */
    @Override
    public List<ScoreVo> getTotalScoreByCountycode(String countycode, Integer taskStatus) {
        return submitDao.selectTotalScoreByCountycode(countycode, taskStatus);
    }

    // 以幼儿园某项属性区分评估得分

    @Override
    public List<ScoreVo> getScoreByIsCity(ScoreDTO scoreDTO) {
        return submitDao.selectScoreByIsCity(scoreDTO);
    }

    @Override
    public List<ScoreVo> getIndex1ByIsCity(ScoreDTO scoreDTO) {
        return submitDao.selectIndex1ByIsCity(scoreDTO);
    }

    @Override
    public Double getCountByLocationCodeAndType(CountDTO CountDTO) {
        return submitDao.selectCountByLocationCodeAndType(CountDTO);
    }

    @Override
    public List<Index2Vo> getComplete(Integer taskId) {
        return submitDao.selectComplete(taskId);
    }

    @Override
    public List<Index2Vo> getIndex2ByEvaluateId(Integer evaluateId) {
        return submitDao.listIndex2ByEvaluateId(evaluateId);
    }
}
