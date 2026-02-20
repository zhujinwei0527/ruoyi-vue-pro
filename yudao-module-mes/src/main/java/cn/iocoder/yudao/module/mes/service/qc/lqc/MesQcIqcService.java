package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.*;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.defect.MesQcIqcDefectSaveReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.line.MesQcIqcLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcDefectDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;
import jakarta.validation.Valid;

// TODO @AI：和别的模块一样，拆分成多个 service
/**
 * MES 来料检验单（IQC） Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcIqcService {

    // ========== 来料检验主表 ==========

    /**
     * 创建来料检验单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIqc(@Valid MesQcIqcSaveReqVO createReqVO);

    /**
     * 更新来料检验单
     *
     * @param updateReqVO 更新信息
     */
    void updateIqc(@Valid MesQcIqcSaveReqVO updateReqVO);

    /**
     * 完成来料检验单
     *
     * @param id 编号
     */
    void completeIqc(Long id);

    /**
     * 删除来料检验单
     *
     * @param id 编号
     */
    void deleteIqc(Long id);

    /**
     * 获得来料检验单
     *
     * @param id 编号
     * @return 来料检验单
     */
    MesQcIqcDO getIqc(Long id);

    /**
     * 获得来料检验单分页
     *
     * @param pageReqVO 分页查询
     * @return 来料检验单分页
     */
    PageResult<MesQcIqcDO> getIqcPage(MesQcIqcPageReqVO pageReqVO);

    // ========== 来料检验行（只读） ==========

    /**
     * 获得来料检验行
     *
     * @param id 编号
     * @return 来料检验行
     */
    MesQcIqcLineDO getIqcLine(Long id);

    /**
     * 获得来料检验行分页
     *
     * @param pageReqVO 分页查询
     * @return 来料检验行分页
     */
    PageResult<MesQcIqcLineDO> getIqcLinePage(MesQcIqcLinePageReqVO pageReqVO);

    // ========== 来料检验缺陷记录 ==========

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

}
