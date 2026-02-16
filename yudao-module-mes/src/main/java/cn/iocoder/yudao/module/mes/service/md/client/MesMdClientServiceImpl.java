package cn.iocoder.yudao.module.mes.service.md.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mes.controller.admin.md.client.vo.MesMdClientImportExcelVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.client.vo.MesMdClientImportRespVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.client.vo.MesMdClientPageReqVO;
import cn.iocoder.yudao.module.mes.controller.admin.md.client.vo.MesMdClientSaveReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.md.client.MesMdClientDO;
import cn.iocoder.yudao.module.mes.dal.mysql.md.client.MesMdClientMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mes.enums.ErrorCodeConstants.*;

/**
 * MES 客户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MesMdClientServiceImpl implements MesMdClientService {

    @Resource
    private MesMdClientMapper clientMapper;

    @Override
    public Long createClient(MesMdClientSaveReqVO createReqVO) {
        // 校验编码唯一
        validateClientCodeUnique(null, createReqVO.getCode());
        // 校验名称唯一
        validateClientNameUnique(null, createReqVO.getName());
        // 校验简称唯一
        validateClientNicknameUnique(null, createReqVO.getNickname());

        // 插入
        MesMdClientDO client = BeanUtils.toBean(createReqVO, MesMdClientDO.class);
        clientMapper.insert(client);
        // TODO @芋艿：客户创建后自动生成条码，依赖 WM 条码模块
        return client.getId();
    }

    @Override
    public void updateClient(MesMdClientSaveReqVO updateReqVO) {
        // 校验存在
        validateClientExists(updateReqVO.getId());
        // 校验编码唯一
        validateClientCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        // 校验名称唯一
        validateClientNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验简称唯一
        validateClientNicknameUnique(updateReqVO.getId(), updateReqVO.getNickname());

        // 更新
        MesMdClientDO updateObj = BeanUtils.toBean(updateReqVO, MesMdClientDO.class);
        clientMapper.updateById(updateObj);
    }

    @Override
    public void deleteClient(Long id) {
        // 校验存在
        validateClientExists(id);
        // 删除
        clientMapper.deleteById(id);
    }

    private void validateClientExists(Long id) {
        if (clientMapper.selectById(id) == null) {
            throw exception(MD_CLIENT_NOT_EXISTS);
        }
    }

    private void validateClientCodeUnique(Long id, String code) {
        MesMdClientDO client = clientMapper.selectByCode(code);
        if (client == null) {
            return;
        }
        if (ObjUtil.notEqual(client.getId(), id)) {
            throw exception(MD_CLIENT_CODE_DUPLICATE);
        }
    }

    private void validateClientNameUnique(Long id, String name) {
        MesMdClientDO client = clientMapper.selectByName(name);
        if (client == null) {
            return;
        }
        if (ObjUtil.notEqual(client.getId(), id)) {
            throw exception(MD_CLIENT_NAME_DUPLICATE);
        }
    }

    private void validateClientNicknameUnique(Long id, String nickname) {
        if (StrUtil.isEmpty(nickname)) {
            return;
        }
        MesMdClientDO client = clientMapper.selectByNickname(nickname);
        if (client == null) {
            return;
        }
        if (ObjUtil.notEqual(client.getId(), id)) {
            throw exception(MD_CLIENT_NICKNAME_DUPLICATE);
        }
    }

    @Override
    public MesMdClientDO getClient(Long id) {
        return clientMapper.selectById(id);
    }

    @Override
    public PageResult<MesMdClientDO> getClientPage(MesMdClientPageReqVO pageReqVO) {
        return clientMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesMdClientDO> getClientListByStatus(Integer status) {
        return clientMapper.selectListByStatus(status);
    }

    @Override
    public List<MesMdClientDO> getClientList(Collection<Long> ids) {
        return clientMapper.selectByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MesMdClientImportRespVO importClientList(List<MesMdClientImportExcelVO> importClients, boolean updateSupport) {
        // 1. 参数校验
        if (CollUtil.isEmpty(importClients)) {
            throw exception(MD_CLIENT_IMPORT_LIST_IS_EMPTY);
        }

        // 2. 遍历，逐个创建 or 更新
        MesMdClientImportRespVO respVO = MesMdClientImportRespVO.builder()
                .createCodes(new ArrayList<>()).updateCodes(new ArrayList<>())
                .failureCodes(new LinkedHashMap<>()).build();
        AtomicInteger index = new AtomicInteger(1);
        importClients.forEach(importClient -> {
            int currentIndex = index.getAndIncrement();
            // 2.1 校验字段
            String key = StrUtil.blankToDefault(importClient.getCode(), "第 " + currentIndex + " 行");
            if (StrUtil.isBlank(importClient.getCode())) {
                respVO.getFailureCodes().put(key, "客户编码不能为空");
                return;
            }
            if (StrUtil.isBlank(importClient.getName())) {
                respVO.getFailureCodes().put(key, "客户名称不能为空");
                return;
            }
            if (importClient.getType() == null) {
                respVO.getFailureCodes().put(key, "客户类型不能为空");
                return;
            }

            // 2.2 判断：创建 or 更新
            MesMdClientDO existClient = clientMapper.selectByCode(importClient.getCode());
            if (existClient == null) {
                // 2.2.1 创建
                try {
                    validateClientNameUnique(null, importClient.getName());
                    validateClientNicknameUnique(null, importClient.getNickname());
                } catch (ServiceException ex) {
                    respVO.getFailureCodes().put(key, ex.getMessage());
                    return;
                }
                MesMdClientDO client = BeanUtils.toBean(importClient, MesMdClientDO.class);
                clientMapper.insert(client);
                respVO.getCreateCodes().add(importClient.getCode());
            } else if (updateSupport) {
                // 2.2.2 更新
                try {
                    validateClientNameUnique(existClient.getId(), importClient.getName());
                    validateClientNicknameUnique(existClient.getId(), importClient.getNickname());
                } catch (ServiceException ex) {
                    respVO.getFailureCodes().put(key, ex.getMessage());
                    return;
                }
                MesMdClientDO updateObj = BeanUtils.toBean(importClient, MesMdClientDO.class);
                updateObj.setId(existClient.getId());
                clientMapper.updateById(updateObj);
                respVO.getUpdateCodes().add(importClient.getCode());
            } else {
                // 不支持更新
                respVO.getFailureCodes().put(key, "客户编码已存在");
            }
        });
        return respVO;
    }

}
