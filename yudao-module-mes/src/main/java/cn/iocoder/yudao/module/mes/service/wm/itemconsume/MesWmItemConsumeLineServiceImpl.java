package cn.iocoder.yudao.module.mes.service.wm.itemconsume;

import cn.iocoder.yudao.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.itemconsume.MesWmItemConsumeLineMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * MES 物料消耗记录行 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmItemConsumeLineServiceImpl implements MesWmItemConsumeLineService {

    @Resource
    private MesWmItemConsumeLineMapper itemConsumeLineMapper;

    @Override
    public void createItemConsumeLineBatch(List<MesWmItemConsumeLineDO> lines) {
        itemConsumeLineMapper.insertBatch(lines);
    }

    @Override
    public List<MesWmItemConsumeLineDO> getItemConsumeLineListByConsumeId(Long consumeId) {
        return itemConsumeLineMapper.selectListByConsumeId(consumeId);
    }

}
