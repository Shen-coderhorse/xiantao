package com.xiantao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiantao.common.Result;
import com.xiantao.dto.OrderCreateDTO;
import com.xiantao.dto.OrderQueryDTO;
import com.xiantao.dto.ShipDTO;
import com.xiantao.service.OrderService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.OrderVO;
import com.xiantao.vo.PageVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> createOrder(HttpServletRequest request, @Valid @RequestBody OrderCreateDTO dto) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.createOrder(userId, dto);
        return Result.success("下单成功", vo);
    }

    @GetMapping("/list")
    public Result<PageVO<OrderVO>> getOrderList(HttpServletRequest request, OrderQueryDTO query) {
        Long userId = JwtUtils.getCurrentUserId(request);
        PageVO<OrderVO> page = orderService.getOrderList(userId, query);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.getOrderDetail(userId, id);
        return Result.success(vo);
    }

    @PutMapping("/{id}/pay")
    public Result<OrderVO> payOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.payOrder(userId, id);
        return Result.success("支付成功", vo);
    }

    @PutMapping("/{id}/complete")
    public Result<OrderVO> completeOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.completeOrder(userId, id);
        return Result.success("确认收货成功", vo);
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderVO> cancelOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.cancelOrder(userId, id);
        return Result.success("取消成功", vo);
    }

    @PutMapping("/{id}/ship")
    public Result<OrderVO> shipOrder(HttpServletRequest request, @PathVariable Long id,
            @Valid @RequestBody ShipDTO shipDTO) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.shipOrder(userId, id, shipDTO);
        return Result.success("发货成功", vo);
    }

    @PutMapping("/{id}/receive")
    public Result<OrderVO> receiveOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtils.getCurrentUserId(request);
        OrderVO vo = orderService.receiveOrder(userId, id);
        return Result.success("确认收货成功", vo);
    }
}
