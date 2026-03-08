package cn.iocoder.yudao.module.mes.service.wm.wmpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackagePageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.wm.wmpackage.vo.MesWmPackageSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.wmpackage.MesWmPackageDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 装箱单 Service 接口
 */
public interface MesWmPackageService {

    /**
     * 创建装箱单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPackage(@Valid MesWmPackageSaveReqVO createReqVO);

    /**
     * 修改装箱单
     *
     * @param updateReqVO 修改信息
     */
    void updatePackage(@Valid MesWmPackageSaveReqVO updateReqVO);

    /**
     * 删除装箱单
     *
     * @param id 编号
     */
    void deletePackage(Long id);

    /**
     * 获得装箱单
     *
     * @param id 编号
     * @return 装箱单
     */
    MesWmPackageDO getPackage(Long id);

    /**
     * 获得装箱单分页
     *
     * @param pageReqVO 分页查询
     * @return 分页结果
     */
    PageResult<MesWmPackageDO> getPackagePage(MesWmPackagePageReqVO pageReqVO);

    /**
     * 完成装箱单
     *
     * @param id 编号
     */
    void finishPackage(Long id);

    /**
     * 获得装箱单树形结构
     *
     * @return 装箱单列表（树形）
     */
    List<MesWmPackageDO> getPackageTree();

}
