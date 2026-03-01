package cn.iocoder.yudao.module.mes.service.wm.productrecpt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLinePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.productrecpt.vo.line.MesWmProductRecptLineSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productrecpt.MesWmProductRecptLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productrecpt.MesWmProductRecptLineMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.WM_PRODUCT_RECPT_LINE_NOT_EXISTS;

/**
 * MES 产品收货单行 Service 实现类
 */
@Service
@Validated
public class MesWmProductRecptLineServiceImpl implements MesWmProductRecptLineService {

    @Resource
    private MesWmProductRecptLineMapper productRecptLineMapper;

    @Resource
    @Lazy
    private MesWmProductRecptService productRecptService;

    @Resource
    private MesWmProductRecptDetailService productRecptDetailService;

    @Override
    public Long createProductRecptLine(MesWmProductRecptLineSaveReqVO createReqVO) {
        // 校验父单据存在且为可编辑状态
        productRecptService.validateProductRecptEditable(createReqVO.getRecptId());

        // 新增
        MesWmProductRecptLineDO line = BeanUtils.toBean(createReqVO, MesWmProductRecptLineDO.class);
        productRecptLineMapper.insert(line);
        return line.getId();
    }

    @Override
    public void updateProductRecptLine(MesWmProductRecptLineSaveReqVO updateReqVO) {
        // 校验存在
        MesWmProductRecptLineDO line = validateProductRecptLineExists(updateReqVO.getId());
        // 校验父单据存在且为可编辑状态
        productRecptService.validateProductRecptEditable(line.getRecptId());

        // 更新
        MesWmProductRecptLineDO updateObj = BeanUtils.toBean(updateReqVO, MesWmProductRecptLineDO.class);
        productRecptLineMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductRecptLine(Long id) {
        // 校验存在
        validateProductRecptLineExists(id);

        // 级联删除明细
        productRecptDetailService.deleteProductRecptDetailByLineId(id);
        // 删除
        productRecptLineMapper.deleteById(id);
    }

    @Override
    public MesWmProductRecptLineDO getProductRecptLine(Long id) {
        return productRecptLineMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmProductRecptLineDO> getProductRecptLinePage(MesWmProductRecptLinePageReqVO pageReqVO) {
        return productRecptLineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesWmProductRecptLineDO> getProductRecptLineListByRecptId(Long recptId) {
        return productRecptLineMapper.selectListByRecptId(recptId);
    }

    @Override
    public void deleteProductRecptLineByRecptId(Long recptId) {
        productRecptLineMapper.deleteByRecptId(recptId);
    }

    private MesWmProductRecptLineDO validateProductRecptLineExists(Long id) {
        MesWmProductRecptLineDO line = productRecptLineMapper.selectById(id);
        if (line == null) {
            throw exception(WM_PRODUCT_RECPT_LINE_NOT_EXISTS);
        }
        return line;
    }

}
