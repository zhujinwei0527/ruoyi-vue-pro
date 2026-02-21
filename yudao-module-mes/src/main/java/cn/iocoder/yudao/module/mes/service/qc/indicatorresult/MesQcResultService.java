package cn.iocoder.yudao.module.mes.service.qc.indicatorresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultDetailRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.indicatorresult.vo.MesQcResultSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.indicatorresult.MesQcResultDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 检验结果 Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcResultService {

    /**
     * 创建检验结果（含明细）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createResult(@Valid MesQcResultSaveReqVO createReqVO);

    /**
     * 更新检验结果（含明细）
     *
     * @param updateReqVO 更新信息
     */
    void updateResult(@Valid MesQcResultSaveReqVO updateReqVO);

    /**
     * 删除检验结果（级联删除明细）
     *
     * @param id 编号
     */
    void deleteResult(Long id);

    /**
     * 获得检验结果
     *
     * @param id 编号
     * @return 检验结果
     */
    MesQcResultDO getResult(Long id);

    /**
     * 获得检验结果分页
     *
     * @param pageReqVO 分页查询
     * @return 检验结果分页
     */
    PageResult<MesQcResultDO> getResultPage(MesQcResultPageReqVO pageReqVO);

    /**
     * 获取检验结果明细列表（含已填值，组装 IQC line + indicator 信息）
     *
     * @param resultId 检验结果 ID
     * @param qcId     质检单 ID
     * @param qcType   质检类型
     * @return 明细列表
     */
    List<MesQcResultDetailRespVO> getResultDetailList(Long resultId, Long qcId, Integer qcType);

    /**
     * 获取空值检测项模板（新建结果时用）
     *
     * @param qcId   质检单 ID
     * @param qcType 质检类型
     * @return 空值明细列表
     */
    List<MesQcResultDetailRespVO> getDetailTemplate(Long qcId, Integer qcType);

}
