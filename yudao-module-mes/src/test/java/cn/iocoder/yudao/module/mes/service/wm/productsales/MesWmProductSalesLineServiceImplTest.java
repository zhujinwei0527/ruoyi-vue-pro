package cn.iocoder.yudao.module.mes.service.wm.productsales;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.productsales.MesWmProductSalesLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.productsales.MesWmProductSalesLineMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcCheckResultEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;

import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link MesWmProductSalesLineServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MesWmProductSalesLineServiceImpl.class)
public class MesWmProductSalesLineServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MesWmProductSalesLineServiceImpl productSalesLineService;

    @Resource
    private MesWmProductSalesLineMapper productSalesLineMapper;

    @MockitoBean
    private MesWmProductSalesService productSalesService;

    @MockitoBean
    private MesWmProductSalesDetailService productSalesDetailService;

    @MockitoBean
    private MesMdItemService itemService;

    @Test
    public void testUpdateProductSalesLineWhenOqcFinish_pass() {
        // 准备参数
        Long oqcId = randomLongId();
        Integer checkResult = MesQcCheckResultEnum.PASS.getType();

        // mock 数据
        MesWmProductSalesLineDO lineDO = randomPojo(MesWmProductSalesLineDO.class);
        productSalesLineMapper.insert(lineDO);

        // 调用
        productSalesLineService.updateProductSalesLineWhenOqcFinish(lineDO.getId(), oqcId, checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmProductSalesLineDO updateLine = productSalesLineMapper.selectById(lineDO.getId());
        assertEquals(oqcId, updateLine.getOqcId());
        assertEquals(MesWmQualityStatusEnum.PASS.getStatus(), updateLine.getQualityStatus());
    }

    @Test
    public void testUpdateProductSalesLineWhenOqcFinish_fail() {
        // 准备参数
        Long oqcId = randomLongId();
        Integer checkResult = MesQcCheckResultEnum.FAIL.getType();

        // mock 数据
        MesWmProductSalesLineDO lineDO = randomPojo(MesWmProductSalesLineDO.class);
        productSalesLineMapper.insert(lineDO);

        // 调用
        productSalesLineService.updateProductSalesLineWhenOqcFinish(lineDO.getId(), oqcId, checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmProductSalesLineDO updateLine = productSalesLineMapper.selectById(lineDO.getId());
        assertEquals(oqcId, updateLine.getOqcId());
        assertEquals(MesWmQualityStatusEnum.FAIL.getStatus(), updateLine.getQualityStatus());
    }

}
