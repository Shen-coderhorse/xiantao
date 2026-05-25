package com.xiantao.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.dto.ShipDTO;
import com.xiantao.entity.Logistics;
import com.xiantao.entity.LogisticsTrack;
import com.xiantao.mapper.LogisticsMapper;
import com.xiantao.mapper.LogisticsTrackMapper;
import com.xiantao.service.LogisticsService;
import com.xiantao.vo.LogisticsTrackVO;
import com.xiantao.vo.LogisticsVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl extends ServiceImpl<LogisticsMapper, Logistics> implements LogisticsService {

    private final LogisticsTrackMapper logisticsTrackMapper;

    @Override
    @Transactional
    public LogisticsVO createLogistics(Long orderId, ShipDTO dto) {
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该订单已发货");
        }

        Logistics logistics = new Logistics();
        logistics.setOrderId(orderId);
        logistics.setCompanyCode(dto.getCompanyCode());
        logistics.setCompanyName(dto.getCompanyName());
        logistics.setTrackingNo(dto.getTrackingNo());
        logistics.setStatus(1);
        logistics.setShipTime(LocalDateTime.now());
        logistics.setCurrentLocation("发货地");
        logistics.setLatitude(new BigDecimal("39.9042"));
        logistics.setLongitude(new BigDecimal("116.4074"));
        this.save(logistics);

        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logistics.getId());
        track.setLocation("发货地");
        track.setDescription("卖家已发货，等待揽件");
        track.setTrackTime(LocalDateTime.now());
        logisticsTrackMapper.insert(track);

        return convertToVO(logistics);
    }

    @Override
    public LogisticsVO getLogisticsByOrderId(Long orderId) {
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics logistics = this.getOne(wrapper);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }
        LogisticsVO vo = convertToVO(logistics);

        List<LogisticsTrack> tracks = getTracksByLogisticsId(logistics.getId());
        vo.setTracks(tracks.stream().map(this::convertTrackToVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public List<LogisticsTrackVO> getLogisticsTracks(Long logisticsId) {
        List<LogisticsTrack> tracks = getTracksByLogisticsId(logisticsId);
        return tracks.stream().map(this::convertTrackToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void simulateLogisticsUpdate(Long orderId) {
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics logistics = this.getOne(wrapper);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }

        String[] locations = { "北京转运中心", "上海转运中心", "广州转运中心", "深圳转运中心", "成都转运中心", "武汉转运中心" };
        String[] descriptions = {
                "包裹已到达转运中心",
                "包裹正在发往下一个转运中心",
                "快递员已揽件",
                "包裹到达目的地营业部",
                "快递员正在派送"
        };

        Random random = new Random();
        String location = locations[random.nextInt(locations.length)];
        String description = descriptions[random.nextInt(descriptions.length)];

        BigDecimal baseLat = logistics.getLatitude() != null ? logistics.getLatitude() : new BigDecimal("39.9042");
        BigDecimal baseLng = logistics.getLongitude() != null ? logistics.getLongitude() : new BigDecimal("116.4074");
        BigDecimal newLat = baseLat.add(new BigDecimal((random.nextDouble() - 0.5) * 2));
        BigDecimal newLng = baseLng.add(new BigDecimal((random.nextDouble() - 0.5) * 2));

        logistics.setCurrentLocation(location);
        logistics.setLatitude(newLat);
        logistics.setLongitude(newLng);
        logistics.setStatus(Math.min(3, logistics.getStatus() + 1));
        this.updateById(logistics);

        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logistics.getId());
        track.setLocation(location);
        track.setDescription(description);
        track.setTrackTime(LocalDateTime.now());
        logisticsTrackMapper.insert(track);
    }

    @Override
    public LogisticsVO getLogisticsLocation(Long orderId) {
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics logistics = this.getOne(wrapper);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }
        LogisticsVO vo = new LogisticsVO();
        vo.setId(logistics.getId());
        vo.setOrderId(logistics.getOrderId());
        vo.setLatitude(logistics.getLatitude());
        vo.setLongitude(logistics.getLongitude());
        vo.setCurrentLocation(logistics.getCurrentLocation());
        vo.setStatus(logistics.getStatus());
        vo.setStatusText(getStatusText(logistics.getStatus()));
        vo.setCompanyName(logistics.getCompanyName());
        vo.setTrackingNo(logistics.getTrackingNo());
        return vo;
    }

    private List<LogisticsTrack> getTracksByLogisticsId(Long logisticsId) {
        LambdaQueryWrapper<LogisticsTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTrack::getLogisticsId, logisticsId);
        wrapper.orderByDesc(LogisticsTrack::getTrackTime);
        return logisticsTrackMapper.selectList(wrapper);
    }

    private LogisticsVO convertToVO(Logistics logistics) {
        LogisticsVO vo = new LogisticsVO();
        BeanUtils.copyProperties(logistics, vo);
        vo.setStatusText(getStatusText(logistics.getStatus()));
        return vo;
    }

    private LogisticsTrackVO convertTrackToVO(LogisticsTrack track) {
        LogisticsTrackVO vo = new LogisticsTrackVO();
        BeanUtils.copyProperties(track, vo);
        return vo;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待发货";
            case 1 -> "已发货";
            case 2 -> "运输中";
            case 3 -> "派送中";
            case 4 -> "已签收";
            default -> "未知状态";
        };
    }
}
