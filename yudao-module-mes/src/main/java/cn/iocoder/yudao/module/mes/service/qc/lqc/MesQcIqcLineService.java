package cn.iocoder.yudao.module.mes.service.qc.lqc;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.qc.iqc.vo.line.MesQcIqcLinePageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.lqc.MesQcIqcLineDO;

import java.util.List;

/**
 * MES 来料检验单行 Service 接口
 *
 * @author 芋道源码
 */
public interface MesQcIqcLineService {

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

    /**
     * 根据来料检验单 ID 查询所有行
     *
     * @param iqcId 来料检验单 ID
     * @return 行列表
     */
    List<MesQcIqcLineDO> selectListByIqcId(Long iqcId);

    /**
     * 从模板指标自动生成检验行
     *
     * @param iqcId 来料检验单 ID
     * @param templateId 模板 ID
     */
    void createLinesFromTemplate(Long iqcId, Long templateId);

    /**
     * 批量更新行的缺陷统计数量
     *
     * @param lines 待更新的行列表（只需设置 id、criticalQuantity、majorQuantity、minorQuantity）
     */
    void batchUpdateDefectStats(List<MesQcIqcLineDO> lines);

    /**
     * 根据来料检验单 ID 级联删除所有行
     *
     * @param iqcId 来料检验单 ID
     */
    void deleteByIqcId(Long iqcId);

}
