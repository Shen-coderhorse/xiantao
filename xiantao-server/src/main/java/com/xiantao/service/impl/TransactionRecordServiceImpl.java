package com.xiantao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.entity.Order;
import com.xiantao.entity.TransactionRecord;
import com.xiantao.entity.User;
import com.xiantao.mapper.TransactionRecordMapper;
import com.xiantao.service.OrderService;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.service.UserService;
import com.xiantao.vo.TransactionRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionRecordServiceImpl extends ServiceImpl<TransactionRecordMapper, TransactionRecord> implements TransactionRecordService {

    private final OrderService orderService;
    private final UserService userService;

    public TransactionRecordServiceImpl(@Lazy OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TransactionRecordVO createPayment(Long orderId, Long buyerId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，只能对待付款订单进行支付");
        }

        User buyer = userService.getById(buyerId);
        if (buyer.getBalance().compareTo(order.getProductPrice()) < 0) {
            throw new BusinessException("余额不足，请先充值");
        }

        buyer.setBalance(buyer.getBalance().subtract(order.getProductPrice()));
        userService.updateById(buyer);

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderService.updateById(order);

        TransactionRecord record = new TransactionRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setTransactionType(1);
        record.setAmount(order.getProductPrice());
        record.setFromUserId(buyerId);
        record.setToUserId(0L);
        record.setStatus(1);
        record.setRemark("买家付款，资金进入平台托管");
        record.setCreateTime(LocalDateTime.now());
        this.save(record);

        TransactionRecord escrowRecord = new TransactionRecord();
        escrowRecord.setOrderId(orderId);
        escrowRecord.setOrderNo(order.getOrderNo());
        escrowRecord.setTransactionType(2);
        escrowRecord.setAmount(order.getProductPrice());
        escrowRecord.setFromUserId(buyerId);
        escrowRecord.setToUserId(0L);
        escrowRecord.setStatus(1);
        escrowRecord.setRemark("资金进入平台托管账户");
        escrowRecord.setCreateTime(LocalDateTime.now());
        this.save(escrowRecord);

        return convertToVO(record);
    }

    @Override
    @Transactional
    public TransactionRecordVO releasePayment(Long orderId, Long buyerId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，只能对已付款订单进行确认收货");
        }

        User seller = userService.getById(order.getSellerId());
        seller.setBalance(seller.getBalance().add(order.getProductPrice()));
        userService.updateById(seller);

        order.setStatus(2);
        order.setCompleteTime(LocalDateTime.now());
        orderService.updateById(order);

        TransactionRecord record = new TransactionRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setTransactionType(3);
        record.setAmount(order.getProductPrice());
        record.setFromUserId(0L);
        record.setToUserId(order.getSellerId());
        record.setStatus(1);
        record.setRemark("买家确认收货，资金解冻转入卖家账户");
        record.setCreateTime(LocalDateTime.now());
        this.save(record);

        return convertToVO(record);
    }

    @Override
    @Transactional
    public TransactionRecordVO refundPayment(Long orderId, Long userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("订单未取消，无法退款");
        }

        User buyer = userService.getById(order.getBuyerId());
        buyer.setBalance(buyer.getBalance().add(order.getProductPrice()));
        userService.updateById(buyer);

        TransactionRecord record = new TransactionRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setTransactionType(4);
        record.setAmount(order.getProductPrice());
        record.setFromUserId(0L);
        record.setToUserId(order.getBuyerId());
        record.setStatus(1);
        record.setRemark("订单已取消，资金退还买家");
        record.setCreateTime(LocalDateTime.now());
        this.save(record);

        return convertToVO(record);
    }

    @Override
    public List<TransactionRecordVO> getUserTransactions(Long userId) {
        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(TransactionRecord::getFromUserId, userId)
                .or().eq(TransactionRecord::getToUserId, userId));
        wrapper.orderByDesc(TransactionRecord::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionRecordVO> getOrderTransactions(Long orderId) {
        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionRecord::getOrderId, orderId);
        wrapper.orderByAsc(TransactionRecord::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private TransactionRecordVO convertToVO(TransactionRecord record) {
        TransactionRecordVO vo = new TransactionRecordVO();
        BeanUtils.copyProperties(record, vo);

        vo.setTransactionTypeText(getTransactionTypeText(record.getTransactionType()));
        vo.setStatusText(getStatusText(record.getStatus()));

        if (record.getFromUserId() != null && record.getFromUserId() > 0) {
            User fromUser = userService.getById(record.getFromUserId());
            if (fromUser != null) {
                vo.setFromUserName(fromUser.getNickname());
            }
        } else {
            vo.setFromUserName("平台托管");
        }

        if (record.getToUserId() != null && record.getToUserId() > 0) {
            User toUser = userService.getById(record.getToUserId());
            if (toUser != null) {
                vo.setToUserName(toUser.getNickname());
            }
        } else {
            vo.setToUserName("平台托管");
        }

        return vo;
    }

    private String getTransactionTypeText(Integer type) {
        return switch (type) {
            case 1 -> "付款";
            case 2 -> "托管";
            case 3 -> "解冻";
            case 4 -> "退款";
            default -> "未知";
        };
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "处理中";
            case 1 -> "成功";
            case 2 -> "失败";
            default -> "未知状态";
        };
    }
}
