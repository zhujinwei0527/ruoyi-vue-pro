package cn.iocoder.yudao.module.mes.service.qc.oqc;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.mes.dal.dataobject.qc.oqc.MesQcOqcDO;
import cn.iocoder.yudao.module.mes.dal.mysql.qc.oqc.MesQcOqcMapper;
import cn.iocoder.yudao.module.mes.enums.MesBizTypeConstants;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcStatusEnum;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.qc.defectrecord.MesQcDefectRecordService;
import cn.iocoder.yudao.module.mes.service.qc.template.MesQcTemplateDetailService;
import cn.iocoder.yudao.module.mes.service.wm.productsales.MesWmProductSalesLineService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.QC_OQC_NOT_EXISTS;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.QC_OQC_NOT_PREPARE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * {@link MesQcOqcServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MesQcOqcServiceImpl.class)
public class MesQcOqcServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MesQcOqcServiceImpl oqcService;

    @Resource
    private MesQcOqcMapper oqcMapper;

    @MockitoBean
    private MesWmProductSalesLineService productSalesLineService;
    @MockitoBean
    private MesQcOqcLineService oqcLineService;
    @MockitoBean
    private MesMdItemService itemService;
    @MockitoBean
    private MesQcTemplateDetailService templateDetailService;
    @MockitoBean
    private MesQcDefectRecordService defectRecordService;
    @MockitoBean
    private AdminUserApi adminUserApi;

    @Test
    public void testFinishOqc_success() {
        // 准备参数
        Long sourceLineId = randomLongId();
        Integer checkResult = 1;

        // mock 数据
        MesQcOqcDO oqc = randomPojo(MesQcOqcDO.class, o -> {
            o.setStatus(MesQcStatusEnum.DRAFT.getStatus());
            o.setSourceDocType(MesBizTypeConstants.WM_PRODUCT_SALES);
            o.setSourceLineId(sourceLineId);
            o.setCheckResult(checkResult);
        });
        oqcMapper.insert(oqc);

        // 调用
        oqcService.finishOqc(oqc.getId());

        // 断言：验证状态更新
        MesQcOqcDO updatedOqc = oqcMapper.selectById(oqc.getId());
        assertEquals(MesQcStatusEnum.FINISHED.getStatus(), updatedOqc.getStatus());

        // 断言：验证回写调用
        verify(productSalesLineService).updateProductSalesLineWhenOqcFinish(eq(sourceLineId), eq(oqc.getId()), eq(checkResult));
    }

    @Test
    public void testFinishOqc_notExists() {
        // 准备参数
        Long oqcId = randomLongId();

        // 调用，并断言异常
        assertServiceException(() -> oqcService.finishOqc(oqcId), QC_OQC_NOT_EXISTS);
    }

    @Test
    public void testFinishOqc_statusNotDraft() {
        // 准备参数
        // mock 数据：状态为已完成
        MesQcOqcDO oqc = randomPojo(MesQcOqcDO.class, o -> {
            o.setStatus(MesQcStatusEnum.FINISHED.getStatus());
        });
        oqcMapper.insert(oqc);

        // 调用，并断言异常
        assertServiceException(() -> oqcService.finishOqc(oqc.getId()), QC_OQC_NOT_PREPARE);
    }

}
