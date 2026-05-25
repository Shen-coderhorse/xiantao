package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiantao.common.Result;
import com.xiantao.dto.ShipDTO;
import com.xiantao.entity.Order;
import com.xiantao.service.OrderService;
import com.xiantao.service.UserService;
import com.xiantao.vo.OrderVO;
import com.xiantao.vo.PageVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageVO<OrderVO>> getOrderList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        orderService.page(page, wrapper);
        List<OrderVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageVO.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize()));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> adminShipOrder(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody ShipDTO shipDTO) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error(400, "订单状态不正确，无法发货");
        }
        order.setStatus(2);
        orderService.updateById(order);
        return Result.success("发货成功", null);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setProductId(order.getProductId());
        vo.setProductTitle(order.getProductTitle());
        vo.setProductPrice(order.getProductPrice());
        vo.setSellerId(order.getSellerId());
        vo.setBuyerId(order.getBuyerId());
        vo.setAddressId(order.getAddressId());
        vo.setStatus(order.getStatus());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setCompleteTime(order.getCompleteTime());
        return vo;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}
