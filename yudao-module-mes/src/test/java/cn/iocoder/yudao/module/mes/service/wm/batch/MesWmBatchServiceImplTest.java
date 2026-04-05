package cn.iocoder.yudao.module.mes.service.wm.batch;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.batch.MesWmBatchDO;
import cn.iocoder.yudao.module.mes.dal.mysql.wm.batch.MesWmBatchMapper;
import cn.iocoder.yudao.module.mes.service.md.autocode.MesMdAutoCodeRecordService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemBatchConfigService;
import cn.iocoder.yudao.module.mes.service.md.item.MesMdItemService;
import cn.iocoder.yudao.module.mes.service.wm.barcode.MesWmBarcodeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link MesWmBatchServiceImpl} 的单元测试
 *
 * @author 芋道源码
 */
@Import(MesWmBatchServiceImpl.class)
public class MesWmBatchServiceImplTest extends BaseDbUnitTest {

    @Resource
    private MesWmBatchServiceImpl batchService;

    @Resource
    private MesWmBatchMapper batchMapper;

    @MockitoBean
    private MesMdItemService itemService;
    @MockitoBean
    private MesMdItemBatchConfigService itemBatchConfigService;
    @MockitoBean
    private MesMdAutoCodeRecordService autoCodeRecordService;
    @MockitoBean
    private MesWmBarcodeService barcodeService;

    // ==================== 向前追溯 ====================

    @Test
    public void testGetForwardBatchList_nullCode() {
        // 传入 null code，应返回空列表（不抛异常）
        List<MesWmBatchDO> result = batchService.getForwardBatchList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetForwardBatchList_noResults() {
        // 传入一个不存在的批次号，XML 查询返回空（因为关联表无数据）
        List<MesWmBatchDO> result = batchService.getForwardBatchList("NOT_EXIST_CODE");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== 向后追溯 ====================

    @Test
    public void testGetBackwardBatchList_nullCode() {
        // 传入 null code，应返回空列表（不抛异常）
        List<MesWmBatchDO> result = batchService.getBackwardBatchList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetBackwardBatchList_noResults() {
        // 传入一个不存在的批次号，XML 查询返回空
        List<MesWmBatchDO> result = batchService.getBackwardBatchList("NOT_EXIST_CODE");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== 循环检测 ====================

    @Test
    public void testForwardBatchList_sameCodeDoesNotRecurse() {
        // 验证：传入相同 code 多次调用不会 StackOverflow
        // 由于数据库中没有环路数据，这里主要验证 visited set 的 null + 空数据安全
        List<MesWmBatchDO> result = batchService.getForwardBatchList("SAME_CODE");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // 二次调用同一个 code，不应有任何异常
        List<MesWmBatchDO> result2 = batchService.getForwardBatchList("SAME_CODE");
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }

    @Test
    public void testBackwardBatchList_sameCodeDoesNotRecurse() {
        // 验证向后追溯的 visited set 安全
        List<MesWmBatchDO> result = batchService.getBackwardBatchList("SAME_CODE");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== selectFirst ====================

    @Test
    public void testSelectFirst_noMatch() {
        // 查询不存在的 itemId，应返回 null
        MesWmBatchDO query = MesWmBatchDO.builder().itemId(randomLongId()).build();
        MesWmBatchDO result = batchMapper.selectFirst(query);
        assertNull(result);
    }

    @Test
    public void testSelectFirst_returnsSmallestId() {
        // 准备数据：插入两条相同条件的批次
        Long itemId = randomLongId();
        MesWmBatchDO batch1 = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_001").build();
        MesWmBatchDO batch2 = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_002").build();
        batchMapper.insert(batch1);
        batchMapper.insert(batch2);

        // 查询
        MesWmBatchDO query = MesWmBatchDO.builder().itemId(itemId).build();
        MesWmBatchDO result = batchMapper.selectFirst(query);

        // 断言：返回 ID 最小的
        assertNotNull(result);
        assertEquals(batch1.getId(), result.getId());
        assertEquals("BATCH_001", result.getCode());
    }

    @Test
    public void testSelectFirst_nullFieldMatching() {
        // 准备数据：一条有 vendorId，一条没有 vendorId
        Long itemId = randomLongId();
        Long vendorId = randomLongId();

        MesWmBatchDO batchWithVendor = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_WITH_V").vendorId(vendorId).build();
        MesWmBatchDO batchWithoutVendor = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_NO_V").build();
        batchMapper.insert(batchWithVendor);
        batchMapper.insert(batchWithoutVendor);

        // 查询：vendorId 为 null -> 应该只匹配 vendorId IS NULL 的记录
        MesWmBatchDO query = MesWmBatchDO.builder().itemId(itemId).build();
        MesWmBatchDO result = batchMapper.selectFirst(query);

        assertNotNull(result);
        assertEquals("BATCH_NO_V", result.getCode());
        assertNull(result.getVendorId());
    }

    @Test
    public void testSelectFirst_withVendorId() {
        // 准备数据
        Long itemId = randomLongId();
        Long vendorId = randomLongId();

        MesWmBatchDO batchWithVendor = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_V1").vendorId(vendorId).build();
        MesWmBatchDO batchWithoutVendor = MesWmBatchDO.builder()
                .itemId(itemId).code("BATCH_NV").build();
        batchMapper.insert(batchWithVendor);
        batchMapper.insert(batchWithoutVendor);

        // 查询：指定 vendorId -> 只匹配有 vendorId 的
        MesWmBatchDO query = MesWmBatchDO.builder().itemId(itemId).vendorId(vendorId).build();
        MesWmBatchDO result = batchMapper.selectFirst(query);

        assertNotNull(result);
        assertEquals("BATCH_V1", result.getCode());
        assertEquals(vendorId, result.getVendorId());
    }

    // ==================== selectByCode ====================

    @Test
    public void testSelectByCode() {
        // 准备数据
        MesWmBatchDO batch = MesWmBatchDO.builder()
                .itemId(randomLongId()).code("TEST_CODE_001").build();
        batchMapper.insert(batch);

        // 查询
        MesWmBatchDO result = batchMapper.selectByCode("TEST_CODE_001");
        assertNotNull(result);
        assertEquals(batch.getId(), result.getId());

        // 查询不存在的
        MesWmBatchDO notFound = batchMapper.selectByCode("NOT_EXIST");
        assertNull(notFound);
    }

}
