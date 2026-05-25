package com.xiantao.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.dto.OrderCreateDTO;
import com.xiantao.dto.OrderQueryDTO;
import com.xiantao.dto.ShipDTO;
import com.xiantao.entity.Order;
import com.xiantao.entity.Product;
import com.xiantao.entity.User;
import com.xiantao.mapper.OrderMapper;
import com.xiantao.service.AddressService;
import com.xiantao.service.LogisticsService;
import com.xiantao.service.OrderService;
import com.xiantao.service.ProductService;
import com.xiantao.service.UserService;
import com.xiantao.vo.AddressVO;
import com.xiantao.vo.OrderVO;
import com.xiantao.vo.PageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final UserService userService;
    private final AddressService addressService;
    private final LogisticsService logisticsService;

    @Override
    @Transactional
    public OrderVO createOrder(Long buyerId, OrderCreateDTO dto) {
        Product product = productService.getById(dto.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架或已售出");
        }

        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        AddressVO address = addressService.getAddressById(buyerId, dto.getAddressId());
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(product.getId());
        order.setProductTitle(product.getTitle());
        order.setProductPrice(product.getPrice());
        order.setSellerId(product.getSellerId());
        order.setBuyerId(buyerId);
        order.setAddressId(dto.getAddressId());
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());

        this.save(order);

        product.setStatus(0);
        productService.updateById(product);

        return convertToVO(order);
    }

    @Override
    public PageVO<OrderVO> getOrderList(Long userId, OrderQueryDTO query) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (query.getType() != null && query.getType() == 1) {
            wrapper.eq(Order::getBuyerId, userId);
        } else if (query.getType() != null && query.getType() == 2) {
            wrapper.eq(Order::getSellerId, userId);
        } else {
            wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }

        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());
        this.page(page, wrapper);

        List<OrderVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO payOrder(Long userId, Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO completeOrder(Long userId, Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(2);
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);

        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(2);
            productService.updateById(product);
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO cancelOrder(Long userId, Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待付款的订单");
        }

        order.setStatus(3);
        this.updateById(order);

        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(1);
            productService.updateById(product);
        }

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO shipOrder(Long userId, Long id, ShipDTO shipDTO) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getSellerId().equals(userId)) {
            throw new BusinessException("只有卖家可以发货");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，只能对已付款订单发货");
        }

        logisticsService.createLogistics(id, shipDTO);

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO receiveOrder(Long userId, Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(2);
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);

        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(2);
            productService.updateById(product);
        }

        return convertToVO(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return timestamp + random;
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        vo.setStatusText(getStatusText(order.getStatus()));

        Product product = productService.getById(order.getProductId());
        if (product != null && StringUtils.hasText(product.getImages())) {
            vo.setProductImage(product.getImages().split(",")[0]);
        }

        User seller = userService.getById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }

        User buyer = userService.getById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getNickname());
            vo.setBuyerAvatar(buyer.getAvatar());
        }

        if (order.getAddressId() != null) {
            AddressVO address = addressService.getAddressById(order.getBuyerId(), order.getAddressId());
            if (address != null) {
                vo.setReceiverName(address.getReceiverName());
                vo.setReceiverPhone(address.getReceiverPhone());
                vo.setAddress(address.getFullAddress());
            }
        }

        return vo;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "已付款";
            case 2:
                return "已完成";
            case 3:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}
