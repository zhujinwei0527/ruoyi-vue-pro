package cn.iocoder.yudao.module.mes.service.pro.andon;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordHandleReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.pro.andon.MesProAndonRecordDO;
import jakarta.validation.Valid;

/**
 * MES 安灯呼叫记录 Service 接口
 *
 * @author 芋道源码
 */
public interface MesProAndonRecordService {

    /**
     * 创建安灯呼叫记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAndonRecord(@Valid MesProAndonRecordSaveReqVO createReqVO);

    /**
     * 删除安灯呼叫记录
     *
     * @param id 编号
     */
    void deleteAndonRecord(Long id);

    /**
     * 处置安灯呼叫记录
     *
     * @param handleReqVO 处置信息
     */
    void handleAndonRecord(@Valid MesProAndonRecordHandleReqVO handleReqVO);

    /**
     * 获得安灯呼叫记录
     *
     * @param id 编号
     * @return 安灯呼叫记录
     */
    MesProAndonRecordDO getAndonRecord(Long id);

    /**
     * 获得安灯呼叫记录分页
     *
     * @param pageReqVO 分页查询
     * @return 安灯呼叫记录分页
     */
    PageResult<MesProAndonRecordDO> getAndonRecordPage(MesProAndonRecordPageReqVO pageReqVO);

}
