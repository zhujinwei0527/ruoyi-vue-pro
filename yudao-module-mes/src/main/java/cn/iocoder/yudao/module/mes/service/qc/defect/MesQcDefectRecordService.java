package cn.iocoder.yudao.module.mes.service.qc.defect;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.defect.vo.MesQcDefectRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.defect.MesQcDefectRecordDO;
import jakarta.validation.Valid;

/**
 * MES 质检缺陷记录 Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcDefectRecordService {

    /**
     * 创建质检缺陷记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDefectRecord(@Valid MesQcDefectRecordSaveReqVO createReqVO);

    /**
     * 更新质检缺陷记录
     *
     * @param updateReqVO 更新信息
     */
    void updateDefectRecord(@Valid MesQcDefectRecordSaveReqVO updateReqVO);

    /**
     * 删除质检缺陷记录
     *
     * @param id 编号
     */
    void deleteDefectRecord(Long id);

    /**
     * 获得质检缺陷记录分页
     *
     * @param pageReqVO 分页查询
     * @return 质检缺陷记录分页
     */
    PageResult<MesQcDefectRecordDO> getDefectRecordPage(MesQcDefectRecordPageReqVO pageReqVO);

    /**
     * 根据检验类型和检验单 ID 级联删除所有缺陷记录
     *
     * @param qcType 检验类型
     * @param qcId 检验单 ID
     */
    void deleteByQcTypeAndQcId(Integer qcType, Long qcId);

}
