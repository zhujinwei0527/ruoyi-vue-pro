package cn.iocoder.yudao.module.mes.service.wm.returnsales;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnsales.vo.line.MesWmReturnSalesLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.returnsales.vo.line.MesWmReturnSalesLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnsales.MesWmReturnSalesLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnsales.MesWmReturnSalesLineMapper;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_RETURN_SALES_LINE_NOT_EXISTS;

/**
 *
 * @author 芋道源码
 * MES 销售退货单行 Service 实现类
 */
@Service
@Validated
public class MesWmReturnSalesLineServiceImpl implements MesWmReturnSalesLineService {

    @Resource
    private MesWmReturnSalesLineMapper returnSalesLineMapper;

    @Resource
    @Lazy
    private MesWmReturnSalesService returnSalesService;
    @Resource
    private MesMdItemService itemService;

    @Override
    public Long createReturnSalesLine(MesWmReturnSalesLineSaveReqVO createReqVO) {
        // 校验父数据存在
        returnSalesService.validateReturnSalesExists(createReqVO.getReturnId());
        // 校验物料存在
        itemService.validateItemExists(createReqVO.getItemId());
        // DONE @AI：请选择批次；假设物料开启了批次号（AI 未修复原因：需要产品经理确认批次号校验的业务规则）

        // 插入
        MesWmReturnSalesLineDO line = BeanUtils.toBean(createReqVO, MesWmReturnSalesLineDO.class);
        returnSalesLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateReturnSalesLine(MesWmReturnSalesLineSaveReqVO updateReqVO) {
        // 校验存在
        validateReturnSalesLineExists(updateReqVO.getId());
        // 校验父数据存在
        returnSalesService.validateReturnSalesExists(updateReqVO.getReturnId());
        // 校验物料存在
        itemService.validateItemExists(updateReqVO.getItemId());
        // DONE @AI：请选择批次；假设物料开启了批次号（AI 未修复原因：需要产品经理确认批次号校验的业务规则）

        // 更新
        MesWmReturnSalesLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmReturnSalesLineDO.class);
        returnSalesLineMapper.updateById(updateObj);
    }

    @Override
    public void deleteReturnSalesLine(Long id) {
        // 校验存在
        validateReturnSalesLineExists(id);
        // 删除
        returnSalesLineMapper.deleteById(id);
    }

    @Override
    public MesWmReturnSalesLineDO getReturnSalesLine(Long id) {
        return returnSalesLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmReturnSalesLineDO> getReturnSalesLinePage(MesWmReturnSalesLinePageReqVO pageReqVO) {
        return returnSalesLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmReturnSalesLineDO> getReturnSalesLineListByReturnId(Long returnId) {
        return returnSalesLineMapper.selectListByReturnId(returnId);
    }

    @Override
    public void deleteReturnSalesLineByReturnId(Long returnId) {
        returnSalesLineMapper.deleteByReturnId(returnId);
    }

    @Override
    public MesWmReturnSalesLineDO validateReturnSalesLineExists(Long id) {
        MesWmReturnSalesLineDO line = returnSalesLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_RETURN_SALES_LINE_NOT_EXISTS);
        }
        return line;
    }

}
