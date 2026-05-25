package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.ShipDTO;
import com.xiantao.entity.Logistics;
import com.xiantao.vo.LogisticsTrackVO;
import com.xiantao.vo.LogisticsVO;

import java.util.List;

public interface LogisticsService extends IService<Logistics> {

    LogisticsVO createLogistics(Long orderId, ShipDTO dto);

    LogisticsVO getLogisticsByOrderId(Long orderId);

    List<LogisticsTrackVO> getLogisticsTracks(Long logisticsId);

    void simulateLogisticsUpdate(Long orderId);

    LogisticsVO getLogisticsLocation(Long orderId);
}
