package cn.iocoder.yudao.module.mes.service.wm.returnissue;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.returnissue.MesWmReturnIssueLineDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.returnissue.MesWmReturnIssueLineMapper;
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
 * {@link MesWmReturnIssueLineServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(MesWmReturnIssueLineServiceImpl.class)
public class MesWmReturnIssueLineServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MesWmReturnIssueLineServiceImpl returnIssueLineService;

    @Resource
    private MesWmReturnIssueLineMapper returnIssueLineMapper;

    @MockitoBean
    private MesWmReturnIssueService issueService;

    @MockitoBean
    private MesMdItemService itemService;

    @Test
    public void testUpdateReturnIssueLineWhenRqcFinish_pass() {
        // 准备参数
        Integer checkResult = MesQcCheckResultEnum.PASS.getType();

        // mock 数据
        MesWmReturnIssueLineDO lineDO = randomPojo(MesWmReturnIssueLineDO.class);
        returnIssueLineMapper.insert(lineDO);

        // 调用
        returnIssueLineService.updateReturnIssueLineWhenRqcFinish(lineDO.getId(), checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmReturnIssueLineDO updateLine = returnIssueLineMapper.selectById(lineDO.getId());
        assertEquals(MesWmQualityStatusEnum.PASS.getStatus(), updateLine.getQualityStatus());
    }

    @Test
    public void testUpdateReturnIssueLineWhenRqcFinish_fail() {
        // 准备参数
        Integer checkResult = MesQcCheckResultEnum.FAIL.getType();

        // mock 数据
        MesWmReturnIssueLineDO lineDO = randomPojo(MesWmReturnIssueLineDO.class);
        returnIssueLineMapper.insert(lineDO);

        // 调用
        returnIssueLineService.updateReturnIssueLineWhenRqcFinish(lineDO.getId(), checkResult);

        // 断言：验证 mapper 更新参数正确
        MesWmReturnIssueLineDO updateLine = returnIssueLineMapper.selectById(lineDO.getId());
        assertEquals(MesWmQualityStatusEnum.FAIL.getStatus(), updateLine.getQualityStatus());
    }

}
