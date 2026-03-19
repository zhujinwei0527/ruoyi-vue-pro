package cn.iocoder.yudao.module.mes.service.wm.itemconsume;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;

import java.util.List;

/**
 * MES 物料消耗记录行 Service 接口
 *
 * @author 芋道源码
 */
public interface MesWmItemConsumeLineService {

    /**
     * 批量创建消耗行
     *
     * @param lines 消耗行列表
     */
    void createItemConsumeLineBatch(List<MesWmItemConsumeLineDO> lines);

    /**
     * 根据消耗记录编号查询消耗行
     *
     * @param consumeId 消耗记录编号
     * @return 消耗行列表
     */
    List<MesWmItemConsumeLineDO> getItemConsumeLineListByConsumeId(Long consumeId);

}
