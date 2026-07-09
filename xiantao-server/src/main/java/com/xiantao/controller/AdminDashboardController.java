package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiantao.common.Result;
import com.xiantao.entity.Order;
import com.xiantao.entity.Product;
import com.xiantao.entity.TransactionRecord;
import com.xiantao.entity.User;
import com.xiantao.service.OrderService;
import com.xiantao.service.ProductService;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final TransactionRecordService transactionRecordService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("userCount", userService.count());
        stats.put("productCount", productService.count());
        stats.put("orderCount", orderService.count());

        // 计算已完成订单的交易总额
        LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Order::getStatus, 2);
        List<Order> completedOrders = orderService.list(completedWrapper);
        double totalAmount = completedOrders.stream()
                .mapToDouble(o -> o.getProductPrice().doubleValue())
                .sum();
        stats.put("totalAmount", String.format("%.2f", totalAmount));

        // 订单状态分布
        Map<String, Long> orderStatusDist = new LinkedHashMap<>();
        orderStatusDist.put("待付款", orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0)));
        orderStatusDist.put("待发货", orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1)));
        orderStatusDist.put("已完成", orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)));
        orderStatusDist.put("已取消", orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)));
        stats.put("orderStatusDist", orderStatusDist);

        return Result.success(stats);
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        Map<String, Object> trend = new HashMap<>();

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Order::getCreateTime, weekAgo);
        wrapper.orderByAsc(Order::getCreateTime);
        List<Order> orders = orderService.list(wrapper);

        // 按日期分组统计
        Map<String, Double> dailyAmount = new LinkedHashMap<>();
        Map<String, Integer> dailyCount = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");

        // 初始化最近7天
        for (int i = 6; i >= 0; i--) {
            String dateKey = LocalDate.now().minusDays(i).format(fmt);
            dailyAmount.put(dateKey, 0.0);
            dailyCount.put(dateKey, 0);
        }

        for (Order order : orders) {
            String dateKey = order.getCreateTime().format(fmt);
            if (dailyAmount.containsKey(dateKey)) {
                dailyAmount.put(dateKey, dailyAmount.get(dateKey) + order.getProductPrice().doubleValue());
                dailyCount.put(dateKey, dailyCount.get(dateKey) + 1);
            }
        }

        trend.put("dates", new ArrayList<>(dailyAmount.keySet()));
        trend.put("amounts", new ArrayList<>(dailyAmount.values()));
        trend.put("counts", new ArrayList<>(dailyCount.values()));

        return Result.success(trend);
    }
}
