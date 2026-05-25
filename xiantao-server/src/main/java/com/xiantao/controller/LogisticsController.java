package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.entity.Order;
import com.xiantao.service.LogisticsService;
import com.xiantao.service.OrderService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.LogisticsTrackVO;
import com.xiantao.vo.LogisticsVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public Result<LogisticsVO> getLogistics(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            return Result.error("无权查看此订单物流");
        }
        LogisticsVO vo = logisticsService.getLogisticsByOrderId(orderId);
        return Result.success(vo);
    }

    @GetMapping("/{orderId}/track")
    public Result<List<LogisticsTrackVO>> getTracks(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            return Result.error("无权查看此订单物流");
        }
        LogisticsVO logisticsVO = logisticsService.getLogisticsByOrderId(orderId);
        List<LogisticsTrackVO> tracks = logisticsService.getLogisticsTracks(logisticsVO.getId());
        return Result.success(tracks);
    }

    @GetMapping("/{orderId}/location")
    public Result<LogisticsVO> getLocation(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            return Result.error("无权查看此订单物流");
        }
        LogisticsVO vo = logisticsService.getLogisticsLocation(orderId);
        return Result.success(vo);
    }

    @PostMapping("/simulate/{orderId}")
    public Result<Void> simulateUpdate(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getSellerId().equals(userId)) {
            return Result.error("只有卖家可以模拟物流更新");
        }
        logisticsService.simulateLogisticsUpdate(orderId);
        return Result.success(null);
    }
}
