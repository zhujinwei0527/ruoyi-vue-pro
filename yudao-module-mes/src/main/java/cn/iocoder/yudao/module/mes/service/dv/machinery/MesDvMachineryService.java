package cn.iocoder.yudao.module.mes.service.dv.machinery;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.dv.machinery.vo.MesDvMachineryPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.dv.machinery.vo.MesDvMachinerySaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import jakarta.validation.Valid;

/**
 * MES 设备台账 Service 接口
 *
 * @author 芋道源码
 */
public interface MesDvMachineryService {

    /**
     * 创建设备
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMachinery(@Valid MesDvMachinerySaveReqVO createReqVO);

    /**
     * 更新设备
     *
     * @param updateReqVO 更新信息
     */
    void updateMachinery(@Valid MesDvMachinerySaveReqVO updateReqVO);

    /**
     * 删除设备
     *
     * @param id 编号
     */
    void deleteMachinery(Long id);

    /**
     * 获得设备
     *
     * @param id 编号
     * @return 设备
     */
    MesDvMachineryDO getMachinery(Long id);

    /**
     * 获得设备分页
     *
     * @param pageReqVO 分页查询
     * @return 设备分页
     */
    PageResult<MesDvMachineryDO> getMachineryPage(MesDvMachineryPageReqVO pageReqVO);

    /**
     * 获得指定设备类型下的设备数量
     *
     * @param machineryTypeId 设备类型编号
     * @return 设备数量
     */
    Long getMachineryCountByMachineryTypeId(Long machineryTypeId);

}
