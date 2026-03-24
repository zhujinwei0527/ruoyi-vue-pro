package cn.iocoder.yudao.module.mes.service.wm.returnsales;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnsales.MesWmReturnSalesLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnsales.MesWmReturnSalesLineMapper;
import cn.iocoder.yudao.module.mes.enums.qc.MesQcCheckResultEnum;
import cn.iocoder.yudao.module.mes.enums.wm.MesWmQualityStatusEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;

import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link MesWmReturnSalesLineServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MesWmReturnSalesLineServiceImpl.class)
public class MesWmReturnSalesLineServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MesWmReturnSalesLineServiceImpl returnSalesLineService;

    @Resource
    private MesWmReturnSalesLineMapper returnSalesLineMapper;

    @MockitoBean
    private MesWmReturnSalesService returnSalesService;

    @MockitoBean
    private MesMdItemService itemService;

    @Test
    public void testUpdateReturnSalesLineWhenRqcFinish_pass() {
        // 准备参数
        Integer checkResult = MesQcCheckResultEnum.PASS.getType();

        // mock 数据
        MesWmReturnSalesLineDO lineDO = randomPojo(MesWmReturnSalesLineDO.class);
        returnSalesLineMapper.insert(lineDO);

        // 调用
        returnSalesLineService.updateReturnSalesLineWhenRqcFinish(lineDO.getId(), checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmReturnSalesLineDO updateLine = returnSalesLineMapper.selectById(lineDO.getId());
        assertEquals(MesWmQualityStatusEnum.PASS.getStatus(), updateLine.getQualityStatus());
    }

    @Test
    public void testUpdateReturnSalesLineWhenRqcFinish_fail() {
        // 准备参数
        Integer checkResult = MesQcCheckResultEnum.FAIL.getType();

        // mock 数据
        MesWmReturnSalesLineDO lineDO = randomPojo(MesWmReturnSalesLineDO.class);
        returnSalesLineMapper.insert(lineDO);

        // 调用
        returnSalesLineService.updateReturnSalesLineWhenRqcFinish(lineDO.getId(), checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmReturnSalesLineDO updateLine = returnSalesLineMapper.selectById(lineDO.getId());
        assertEquals(MesWmQualityStatusEnum.FAIL.getStatus(), updateLine.getQualityStatus());
    }

}
