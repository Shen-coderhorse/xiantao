package com.xiantao.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
import com.xiantao.service.TransactionRecordService;
import com.xiantao.service.UserRatingService;
import com.xiantao.service.UserService;
import com.xiantao.vo.AddressVO;
import com.xiantao.vo.OrderVO;
import com.xiantao.vo.PageVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final UserService userService;
    private final AddressService addressService;
    private final LogisticsService logisticsService;
    private final TransactionRecordService transactionRecordService;
    private final UserRatingService userRatingService;

    @Override
    @Transactional
    public OrderVO createOrder(Long buyerId, OrderCreateDTO dto) {
        log.info("用户{}创建订单，商品ID: {}", buyerId, dto.getProductId());

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

        log.info("订单创建成功，订单号: {}", order.getOrderNo());
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
                .map(this::convertToVOLight)
                .collect(Collectors.toList());

        if (!voList.isEmpty()) {
            fillBatchRelations(voList, page.getRecords());
        }

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
        log.info("用户{}支付订单: {}", userId, id);

        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，只能对待付款订单进行支付");
        }

        transactionRecordService.createPayment(id, userId);

        order = this.getById(id);
        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO completeOrder(Long userId, Long id) {
        log.info("用户{}确认收货，订单: {}", userId, id);

        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，只能对已付款订单进行确认收货");
        }

        transactionRecordService.releasePayment(id, userId);

        order = this.getById(id);

        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(2);
            productService.updateById(product);
        }

        userRatingService.updateCreditAfterOrderComplete(id);

        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO cancelOrder(Long userId, Long id) {
        log.info("用户{}取消订单: {}", userId, id);

        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("只能取消待付款或待发货的订单");
        }

        int previousStatus = order.getStatus();

        // 如果订单已付款，先退款
        if (previousStatus == 1) {
            order.setStatus(3);
            this.updateById(order);
            transactionRecordService.refundPayment(id, userId);
        } else {
            order.setStatus(3);
            this.updateById(order);
        }

        // 商品恢复为在售状态
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(1);
            productService.updateById(product);
        }

        // 更新买家信用（取消交易扣分）
        userRatingService.updateCreditAfterOrderCancel(id);

        log.info("订单取消成功: {}", order.getOrderNo());
        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO shipOrder(Long userId, Long id, ShipDTO shipDTO) {
        log.info("用户{}发货，订单: {}", userId, id);

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

        log.info("订单发货成功: {}", order.getOrderNo());
        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO receiveOrder(Long userId, Long id) {
        log.info("用户{}确认收货，订单: {}", userId, id);

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

        transactionRecordService.releasePayment(id, userId);

        order = this.getById(id);

        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(2);
            productService.updateById(product);
        }

        userRatingService.updateCreditAfterOrderComplete(id);

        return convertToVO(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return timestamp + random;
    }

    private OrderVO convertToVOLight(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusText(getStatusText(order.getStatus()));
        return vo;
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

    private void fillBatchRelations(List<OrderVO> voList, List<Order> orders) {
        Set<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (!productIds.isEmpty()) {
            Map<Long, Product> productMap = productService.listByIds(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            for (OrderVO vo : voList) {
                Product product = productMap.get(vo.getProductId());
                if (product != null && StringUtils.hasText(product.getImages())) {
                    vo.setProductImage(product.getImages().split(",")[0]);
                }
            }
        }

        Set<Long> userIds = orders.stream()
                .flatMap(o -> List.of(o.getSellerId(), o.getBuyerId()).stream())
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            Map<Long, User> userMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            for (OrderVO vo : voList) {
                User seller = userMap.get(vo.getSellerId());
                if (seller != null) {
                    vo.setSellerName(seller.getNickname());
                    vo.setSellerAvatar(seller.getAvatar());
                }
                User buyer = userMap.get(vo.getBuyerId());
                if (buyer != null) {
                    vo.setBuyerName(buyer.getNickname());
                    vo.setBuyerAvatar(buyer.getAvatar());
                }
            }
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "未知状态";
        };
    }
}
