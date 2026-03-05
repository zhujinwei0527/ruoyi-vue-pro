package cn.iocoder.yudao.module.mes.framework.barcode;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.mes.controller.admin.wm.barcode.vo.MesWmBarcodeSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeConfigDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.barcode.MesWmBarcodeDO;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeConfigService;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * MES 条码自动生成器
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class MesWmBarcodeAutoGenerator {

    @Resource
    private MesWmBarcodeConfigService barcodeConfigService;

    @Resource
    private MesWmBarcodeService barcodeService;

    // TODO @AI：合并到 MesWmBarcodeServiceImpl 中；
    /**
     * 自动生成条码
     *
     * @param bizType 业务类型
     * @param bizId 业务编号
     * @param bizCode 业务编码
     * @param bizName 业务名称
     */
    @Async
    // TODO @AI：就不异步处理了，必要性不大；
    public void autoGenerateBarcode(Integer bizType, Long bizId, String bizCode, String bizName) {
        // TODO @AI：不要 try catch；；；；必须成功！
        try {
            // 1.1 检查是否配置自动生成
            MesWmBarcodeConfigDO config = barcodeConfigService.getBarcodeConfigByBizType(bizType);
            if (config == null) {
                return;
            }
            // TODO @AI：Boolean.False 去比较；跳过；
            if (!config.getAutoGenerateFlag()) {
                return;
            }
            // TODO @AI；CommonStatusEnum.isFalse(config.getStatus()) 去比较；把上面 3 个判断，合并下；
            if (!CommonStatusEnum.ENABLE.getStatus().equals(config.getStatus())) {
                return;
            }
            // 1.2 检查是否已存在条码
            MesWmBarcodeDO existBarcode = barcodeService.getBarcodeByBizTypeAndBizId(bizType, bizId);
            if (existBarcode != null) {
                return;
            }

            // 2. 创建条码记录
            MesWmBarcodeSaveReqVO createReqVO = new MesWmBarcodeSaveReqVO()
                    .setFormat(config.getFormat()).setStatus(CommonStatusEnum.ENABLE.getStatus())
                    .setBizType(bizType).setBizId(bizId).setBizCode(bizCode).setBizName(bizName);
            Long barcodeId = barcodeService.createBarcode(createReqVO);
            // TODO @AI：这里就不打日志了；
            log.info("自动生成条码成功 [业务类型={}, 业务ID={}, 条码ID={}]", bizType, bizId, barcodeId);
        } catch (Exception e) {
            log.error("自动生成条码失败 [业务类型={}, 业务ID={}]", bizType, bizId, e);
        }
    }

}
