package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDefectDO;
import jakarta.validation.Valid;

/**
 * MES 来料检验缺陷记录 Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcIqcDefectService {

    /**
     * 创建来料检验缺陷记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIqcDefect(@Valid MesQcIqcDefectSaveReqVO createReqVO);

    /**
     * 更新来料检验缺陷记录
     *
     * @param updateReqVO 更新信息
     */
    void updateIqcDefect(@Valid MesQcIqcDefectSaveReqVO updateReqVO);

    /**
     * 删除来料检验缺陷记录
     *
     * @param id 编号
     */
    void deleteIqcDefect(Long id);

    /**
     * 获得来料检验缺陷记录分页
     *
     * @param pageReqVO 分页查询
     * @return 来料检验缺陷记录分页
     */
    PageResult<MesQcIqcDefectDO> getIqcDefectPage(MesQcIqcDefectPageReqVO pageReqVO);

    /**
     * 根据来料检验单 ID 级联删除所有缺陷记录
     *
     * @param iqcId 来料检验单 ID
     */
    void deleteByIqcId(Long iqcId);

}
