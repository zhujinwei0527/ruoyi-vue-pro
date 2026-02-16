package cn.iocoder.yudao.module.mes.service.md.item;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.bom.MesMdProductBomPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.item.vo.bom.MesMdProductBomSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.item.MesMdProductBomDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.item.MesMdProductBomMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 产品BOM Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdProductBomServiceImpl implements MesMdProductBomService {

    @Resource
    private MesMdProductBomMapper productBomMapper;

    @Resource
    @Lazy // 避免循环依赖
    private MesMdItemService itemService;

    // TODO @AI:1.1 1.2 1.3 如果都是校验；
    @Override
    public Long createProductBom(MesMdProductBomSaveReqVO createReqVO) {
        // 1. 校验物料产品存在
        validateItemExists(createReqVO.getItemId());
        // 2. 校验BOM物料存在
        validateItemExists(createReqVO.getBomItemId());
        // 3. 校验不能自引用
        if (createReqVO.getItemId().equals(createReqVO.getBomItemId())) {
            throw exception(MD_PRODUCT_BOM_SELF_REFERENCE);
        }
        // 4. 校验不能形成闭环
        if (hasCycle(createReqVO.getItemId(), createReqVO.getBomItemId())) {
            throw exception(MD_PRODUCT_BOM_CIRCULAR);
        }

        // 插入
        MesMdProductBomDO productBom = BeanUtils.toBean(createReqVO, MesMdProductBomDO.class);
        productBomMapper.insert(productBom);
        return productBom.getId();
    }

    // TODO @AI:1.1 1.2 1.3 如果都是校验；
    @Override
    public void updateProductBom(MesMdProductBomSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateProductBomExists(updateReqVO.getId());
        // 2. 校验物料产品存在
        validateItemExists(updateReqVO.getItemId());
        // 3. 校验BOM物料存在
        validateItemExists(updateReqVO.getBomItemId());
        // 4. 校验不能自引用
        if (updateReqVO.getItemId().equals(updateReqVO.getBomItemId())) {
            throw exception(MD_PRODUCT_BOM_SELF_REFERENCE);
        }

        // 更新
        MesMdProductBomDO updateObj = BeanUtils.toBean(updateReqVO, MesMdProductBomDO.class);
        productBomMapper.updateById(updateObj);
    }

    @Override
    public void deleteProductBom(Long id) {
        // 校验存在
        validateProductBomExists(id);
        // 删除
        productBomMapper.deleteById(id);
    }

    private void validateProductBomExists(Long id) {
        if (productBomMapper.selectById(id) == null) {
            throw exception(MD_PRODUCT_BOM_NOT_EXISTS);
        }
    }

    private void validateItemExists(Long itemId) {
        if (itemService.getItem(itemId) == null) {
            throw exception(MD_ITEM_NOT_EXISTS);
        }
    }

    // TODO @AI：看看有没 hutool 工具类里，有没 dfs 检测的？
    /**
     * 检测新增边 (itemId -> bomItemId) 后，BOM 图是否存在闭环
     *
     * @param itemId 父物料编号
     * @param bomItemId BOM子物料编号
     * @return 是否存在闭环
     */
    private boolean hasCycle(Long itemId, Long bomItemId) {
        // 获取所有已有的 BOM 记录
        List<MesMdProductBomDO> allBoms = productBomMapper.selectAll();
        // 构建邻接表：parent -> Set<child>
        Map<Long, Set<Long>> graph = new HashMap<>();
        for (MesMdProductBomDO bom : allBoms) {
            graph.computeIfAbsent(bom.getItemId(), k -> new HashSet<>()).add(bom.getBomItemId());
        }
        // 添加待新增的边
        graph.computeIfAbsent(itemId, k -> new HashSet<>()).add(bomItemId);
        // DFS 检测环
        Set<Long> visited = new HashSet<>();
        Set<Long> inStack = new HashSet<>();
        for (Long node : graph.keySet()) {
            if (dfs(node, graph, visited, inStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfs(Long node, Map<Long, Set<Long>> graph, Set<Long> visited, Set<Long> inStack) {
        if (inStack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }
        visited.add(node);
        inStack.add(node);
        Set<Long> neighbors = graph.getOrDefault(node, Collections.emptySet());
        for (Long neighbor : neighbors) {
            if (dfs(neighbor, graph, visited, inStack)) {
                return true;
            }
        }
        inStack.remove(node);
        return false;
    }

    @Override
    public MesMdProductBomDO getProductBom(Long id) {
        return productBomMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdProductBomDO> getProductBomPage(MesMdProductBomPageReqVO pageReqVO) {
        return productBomMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesMdProductBomDO> getProductBomListByItemId(Long itemId) {
        return productBomMapper.selectByItemId(itemId);
    }

    @Override
    public void deleteProductBomByItemId(Long itemId) {
        productBomMapper.deleteByItemId(itemId);
    }

}
