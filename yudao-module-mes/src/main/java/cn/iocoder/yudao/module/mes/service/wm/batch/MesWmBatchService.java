package cn.iocoder.yudao.module.mes.service.wm.batch;

import cn.iocoder.yudao.module.mes.controller.admin.wm.batch.vo.MesWmBatchGenerateReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;

/**
 * 批次管理 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmBatchService {

    /**
     * 获取或生成批次编码
     * <p>
     * 根据物料批次配置，查询或生成批次记录
     *
     * @param reqVO 批次参数（包含 itemId 及其他可选属性）
     * @return 批次记录（如果物料未启用批次管理则返回 null）
     */
    MesWmBatchDO getOrGenerateBatchCode(MesWmBatchGenerateReqVO reqVO);

}
