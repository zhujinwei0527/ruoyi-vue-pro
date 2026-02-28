package cn.iocoder.yudao.module.mes.service.wm.productproduce;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productproduce.vo.line.MesWmProductProduceLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productproduce.MesWmProductProduceLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productproduce.MesWmProductProduceLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_PRODUCE_LINE_NOT_EXISTS;

/**
 * MES 生产入库单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductProduceLineServiceImpl implements MesWmProductProduceLineService {

    @Resource
    private MesWmProductProduceLineMapper produceLineMapper;

    @Resource
    @Lazy
    private MesWmProductProduceService produceService;
    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createProductProduceLine(MesWmProductProduceLineSaveReqVO createReqVO) {
        // 校验父数据存在
        produceService.validateProductProduceExists(createReqVO.getProduceId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());

        // 插入
        MesWmProductProduceLineDO line = BeanUtils.toBean(createReqVO, MesWmProductProduceLineDO.class);
        produceLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductProduceLine(MesWmProductProduceLineSaveReqVO updateReqVO) {
        // 校验存在
        validateProductProduceLineExists(updateReqVO.getId());
        // 校验父数据存在
        produceService.validateProductProduceExists(updateReqVO.getProduceId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());

        // 更新
        MesWmProductProduceLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductProduceLineDO.class);
        produceLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductProduceLine(Long id) {
        // 校验存在
        validateProductProduceLineExists(id);
        // 删除
        produceLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductProduceLineDO getProductProduceLine(Long id) {
        return produceLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductProduceLineDO> getProductProduceLinePage(MesWmProductProduceLinePageReqVO pageReqVO) {
        return produceLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmProductProduceLineDO> getProductProduceLineListByProduceId(Long produceId) {
        return produceLineMapper.selectListByProduceId(produceId);
    }

    @Override
    public void deleteProductProduceLineByProduceId(Long produceId) {
        produceLineMapper.deleteByProduceId(produceId);
    }

    @Override
    public MesWmProductProduceLineDO validateProductProduceLineExists(Long id) {
        MesWmProductProduceLineDO line = produceLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PRODUCT_PRODUCE_LINE_NOT_EXISTS);
        }
        return line;
    }

}
