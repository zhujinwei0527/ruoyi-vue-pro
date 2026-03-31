package cn.iocoder.yudao.module.mes.service.wm.barcode;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeConfigDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.barcode.MesWmBarcodeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 条码清单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesWmBarcodeServiceImpl implements MesWmBarcodeService {

    @Resource
    private MesWmBarcodeMapper barcodeMapper;

    @Resource
    private MesWmBarcodeConfigService barcodeConfigService;

    @Override
    public Long createBarcode(MesWmBarcodeSaveReqVO createReqVO) {
        // 1.1 校验条码配置
        MesWmBarcodeConfigDO config = barcodeConfigService.validateBarcodeConfigByBizType(createReqVO.getBizType());
        // 1.2 校验是否已存在条码
        MesWmBarcodeDO existBarcode = barcodeMapper.selectByBizTypeAndBizId(
                createReqVO.getBizType(), createReqVO.getBizId());
        if (existBarcode != null) {
            throw exception(WM_BARCODE_ALREADY_EXISTS);
        }

        // 2.1 生成条码内容（如果前端未传递，则自动生成）
        String content = createReqVO.getContent();
        if (StrUtil.isBlank(content)) {
            content = generateBarcodeContent(config.getContentFormat(), createReqVO.getBizCode());
        }
        // 2.2 校验条码内容唯一性
        MesWmBarcodeDO contentBarcode = barcodeMapper.selectByContent(content);
        if (contentBarcode != null) {
            throw exception(WM_BARCODE_CONTENT_DUPLICATE);
        }

        // 3. 保存条码记录
        MesWmBarcodeDO barcode = BeanUtils.toBean(createReqVO, MesWmBarcodeDO.class).setContent(content)
                .setConfigId(config.getId()).setFormat(config.getFormat());
        barcodeMapper.insert(barcode);
        return barcode.getId();
    }

    @Override
    public String generateBarcodeContent(Integer bizType, String bizCode) {
        // 1.1 校验参数
        if (bizType == null) {
            throw exception(BARCODE_BIZ_TYPE_NOT_EXISTS);
        }
        if (StrUtil.isBlank(bizCode)) {
            throw exception(BARCODE_BIZ_CODE_NOT_EXISTS);
        }
        // 1.2 查询条码配置
        MesWmBarcodeConfigDO config = barcodeConfigService.validateBarcodeConfigByBizType(bizType);

        // 2. 生成条码内容
        return generateBarcodeContent(config.getContentFormat(), bizCode);
    }

    /**
     * 生成条码内容
     *
     * @param contentFormat 内容格式模板
     * @param bizCode 业务编码
     * @return 条码内容
     */
    private String generateBarcodeContent(String contentFormat, String bizCode) {
        if (StrUtil.isBlank(contentFormat)) {
            return bizCode;
        }
        return contentFormat.replace(MesWmBarcodeConfigDO.PLACEHOLDER_BUSINESS_CODE, bizCode);
    }

    @Override
    public void updateBarcode(MesWmBarcodeSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateBarcodeExists(updateReqVO.getId());

        // 2.1 生成条码内容（如果前端未传递，则自动生成）
        String content = updateReqVO.getContent();
        if (StrUtil.isBlank(content)) {
            MesWmBarcodeConfigDO config = barcodeConfigService.validateBarcodeConfigByBizType(updateReqVO.getBizType());
            content = generateBarcodeContent(config.getContentFormat(), updateReqVO.getBizCode());
        }
        // 2.2 校验条码内容唯一性（排除自己）
        if (StrUtil.isNotBlank(content)) {
            MesWmBarcodeDO contentBarcode = barcodeMapper.selectByContent(content);
            if (contentBarcode != null && ObjUtil.notEqual(contentBarcode.getId(), updateReqVO.getId())) {
                throw exception(WM_BARCODE_CONTENT_DUPLICATE);
            }
        }

        // 3. 更新
        MesWmBarcodeDO updateObj = BeanUtils.toBean(updateReqVO, MesWmBarcodeDO.class);
        if (StrUtil.isNotBlank(content)) {
            updateObj.setContent(content);
        }
        barcodeMapper.updateById(updateObj);
    }

    @Override
    public void deleteBarcode(Long id) {
        // 校验存在
        validateBarcodeExists(id);
        // 删除
        barcodeMapper.deleteById(id);
    }

    private MesWmBarcodeDO validateBarcodeExists(Long id) {
        MesWmBarcodeDO barcode = barcodeMapper.selectById(id);
        if (barcode == null) {
            throw exception(WM_BARCODE_NOT_EXISTS);
        }
        return barcode;
    }

    @Override
    public MesWmBarcodeDO getBarcode(Long id) {
        return barcodeMapper.selectById(id);
    }

    @Override
    public PageResult<MesWmBarcodeDO> getBarcodePage(MesWmBarcodePageReqVO pageReqVO) {
        return barcodeMapper.selectPage(pageReqVO);
    }

    @Override
    public MesWmBarcodeDO getBarcodeByBizTypeAndBizId(Integer bizType, Long bizId) {
        return barcodeMapper.selectByBizTypeAndBizId(bizType, bizId);
    }

    @Override
    public void autoGenerateBarcode(Integer bizType, Long bizId, String bizCode, String bizName) {
        // 1.1 检查是否配置自动生成
        MesWmBarcodeConfigDO config = barcodeConfigService.getBarcodeConfigByBizType(bizType);
        if (config == null || Boolean.FALSE.equals(config.getAutoGenerateFlag())
                || CommonStatusEnum.isDisable(config.getStatus())) {
            return;
        }
        // 1.2 检查是否已存在条码
        MesWmBarcodeDO existBarcode = barcodeMapper.selectByBizTypeAndBizId(bizType, bizId);
        if (existBarcode != null) {
            return;
        }

        // 2. 创建条码记录
        MesWmBarcodeSaveReqVO createReqVO = new MesWmBarcodeSaveReqVO()
                .setBizType(bizType).setBizId(bizId).setBizCode(bizCode).setBizName(bizName)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        createBarcode(createReqVO);
    }

    @Override
    public long getBarcodeCountByConfigId(Long configId) {
        return barcodeMapper.selectCountByConfigId(configId);
    }

}
