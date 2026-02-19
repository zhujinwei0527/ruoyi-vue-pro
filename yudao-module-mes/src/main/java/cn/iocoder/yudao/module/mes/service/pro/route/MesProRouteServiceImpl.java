package cn.iocoder.yudao.module.mes.service.pro.route;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.MesProRoutePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.route.vo.MesProRouteSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProcessDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductDO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.route.MesProRouteProductBomDO;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.MesProRouteMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.MesProRouteProcessMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.MesProRouteProductBomMapper;
import cn.iocoder.yudao.module.mes.dal.mysql.pro.MesProRouteProductMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 工艺路线 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesProRouteServiceImpl implements MesProRouteService {

    @Resource
    private MesProRouteMapper routeMapper;

    // todo @AI：不要直接使用对方模块的 mapper，而是通过 service；
    @Resource
    private MesProRouteProcessMapper routeProcessMapper;
    @Resource
    private MesProRouteProductMapper routeProductMapper;
    @Resource
    private MesProRouteProductBomMapper routeProductBomMapper;

    @Override
    public Long createRoute(MesProRouteSaveReqVO createReqVO) {
        // 1. 校验编码唯一性
        validateRouteCodeUnique(null, createReqVO.getCode());
        // 2. 插入
        MesProRouteDO route = BeanUtils.toBean(createReqVO, MesProRouteDO.class);
        routeMapper.insert(route);
        return route.getId();
    }

    @Override
    public void updateRoute(MesProRouteSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateRouteExists(updateReqVO.getId());
        // 1.2 校验编码唯一性
        validateRouteCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 1.3 启用时的校验
        // TODO @AI：开启禁用，独立接口；保持更新接口的独立；
        if (CommonStatusEnum.ENABLE.getStatus().equals(updateReqVO.getStatus())) {
            validateRouteEnable(updateReqVO.getId());
        }

        // 2. 更新
        MesProRouteDO updateObj = BeanUtils.toBean(updateReqVO, MesProRouteDO.class);
        routeMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoute(Long id) {
        // 1. 校验存在
        validateRouteExists(id);

        // 2.1 级联删除
        routeProcessMapper.deleteByRouteId(id);
        routeProductMapper.deleteByRouteId(id);
        routeProductBomMapper.deleteByRouteId(id);
        // 2.2 删除工艺路线
        routeMapper.deleteById(id);
    }

    private void validateRouteExists(Long id) {
        if (routeMapper.selectById(id) == null) {
            throw exception(PRO_ROUTE_NOT_EXISTS);
        }
    }

    private void validateRouteCodeUnique(Long id, String code) {
        MesProRouteDO route = routeMapper.selectByCode(code);
        if (route == null) {
            return;
        }
        // TODO @AI：ObjUtil notequals；
        if (id == null || !route.getId().equals(id)) {
            throw exception(PRO_ROUTE_CODE_DUPLICATE);
        }
    }

    /**
     * 启用工艺路线时的校验
     */
    private void validateRouteEnable(Long routeId) {
        // 1. 必须有工序
        List<MesProRouteProcessDO> processList = routeProcessMapper.selectListByRouteId(routeId);
        if (processList.isEmpty()) {
            throw exception(PRO_ROUTE_ENABLE_NO_PROCESS);
        }
        // 2. 必须有关键工序
        boolean hasKeyProcess = processList.stream().anyMatch(MesProRouteProcessDO::getKeyFlag);
        // TODO @AI：BooleanUtils.isFalue(hasKeyProcess)
        if (!hasKeyProcess) {
            throw exception(PRO_ROUTE_ENABLE_NO_KEY_PROCESS);
        }
        // 3. 所有产品必须配置了 BOM 消耗
        List<MesProRouteProductDO> productList = routeProductMapper.selectListByRouteId(routeId);
        for (MesProRouteProductDO product : productList) {
            List<MesProRouteProductBomDO> bomList = routeProductBomMapper
                    .selectListByRouteIdAndProductId(routeId, product.getItemId());
            // TODO @AI：CollUtil.isEmpty(bomList)
            if (bomList.isEmpty()) {
                throw exception(PRO_ROUTE_ENABLE_PRODUCT_NO_BOM, product.getItemId());
            }
        }
    }

    @Override
    public MesProRouteDO getRoute(Long id) {
        return routeMapper.selectById(id);
    }

    @Override
    public PageResult<MesProRouteDO> getRoutePage(MesProRoutePageReqVO pageReqVO) {
        return routeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesProRouteDO> getRouteListByStatus(Integer status) {
        return routeMapper.selectListByStatus(status);
    }

}
