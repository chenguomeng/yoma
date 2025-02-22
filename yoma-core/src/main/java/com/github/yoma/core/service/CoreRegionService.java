package com.github.yoma.core.service;

import java.util.List;

import com.github.yoma.core.dao.CoreRegionDao;
import com.github.yoma.core.domain.CoreRegion;
import com.github.yoma.core.dto.CoreRegionQueryDTO;
import com.github.yoma.common.persistence.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageInfo;

/**
 * 行政区域 业务层
 *
 * @author 马世豪
 * @version 2020-04-07
 */
@Service
@Transactional(readOnly = true)
public class CoreRegionService extends CrudService<CoreRegionDao, CoreRegion> {

    @Override
    public CoreRegion get(Long id) {
        return super.get(id);
    }

    public List<CoreRegion> findList(CoreRegionQueryDTO queryDTO) {
        return super.findList(queryDTO);
    }

    public PageInfo<CoreRegion> findPage(CoreRegionQueryDTO queryDTO) {
        return super.findPage(queryDTO);
    }

    @Transactional(readOnly = false)
    public int batchDelete(CoreRegionQueryDTO queryDTO) {
        int count = this.dao.batchDelete(queryDTO);
        return count;
    }

    @Transactional(readOnly = false)
    @Override
    public void save(CoreRegion coreRegion) {
        super.save(coreRegion);
    }

    @Transactional(readOnly = false)
    @Override
    public int delete(CoreRegion coreRegion) {
        return super.delete(coreRegion);
    }

}
