package com.xiantao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.dto.RatingDTO;
import com.xiantao.entity.Order;
import com.xiantao.entity.User;
import com.xiantao.entity.UserRating;
import com.xiantao.mapper.UserRatingMapper;
import com.xiantao.service.OrderService;
import com.xiantao.service.UserCreditService;
import com.xiantao.service.UserRatingService;
import com.xiantao.service.UserService;
import com.xiantao.vo.RatingVO;
import com.xiantao.vo.UserCreditVO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRatingServiceImpl extends ServiceImpl<UserRatingMapper, UserRating> implements UserRatingService {

    private final UserService userService;
    private final OrderService orderService;
    private final UserCreditService userCreditService;

    public UserRatingServiceImpl(UserService userService, @Lazy OrderService orderService, UserCreditService userCreditService) {
        this.userService = userService;
        this.orderService = orderService;
        this.userCreditService = userCreditService;
    }

    @Override
    @Transactional
    public RatingVO createRating(Long reviewerId, RatingDTO dto) {
        Order order = orderService.getById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("只能对已完成的订单进行评价");
        }

        if (!order.getBuyerId().equals(reviewerId) && !order.getSellerId().equals(reviewerId)) {
            throw new BusinessException("无权对此订单进行评价");
        }

        if (reviewerId.equals(dto.getRevieweeId())) {
            throw new BusinessException("不能评价自己");
        }

        LambdaQueryWrapper<UserRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRating::getOrderId, dto.getOrderId());
        wrapper.eq(UserRating::getReviewerId, reviewerId);
        wrapper.eq(UserRating::getRevieweeId, dto.getRevieweeId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("已对该用户进行过评价");
        }

        UserRating rating = new UserRating();
        rating.setOrderId(dto.getOrderId());
        rating.setReviewerId(reviewerId);
        rating.setRevieweeId(dto.getRevieweeId());
        rating.setRating(dto.getRating());
        rating.setContent(dto.getContent());
        this.save(rating);

        userCreditService.updateCreditAfterRating(dto.getRevieweeId(), dto.getRating());

        return convertToVO(rating);
    }

    @Override
    public List<RatingVO> getUserRatings(Long userId) {
        LambdaQueryWrapper<UserRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRating::getReviewerId, userId);
        wrapper.orderByDesc(UserRating::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<RatingVO> getUserReceivedRatings(Long userId) {
        LambdaQueryWrapper<UserRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRating::getRevieweeId, userId);
        wrapper.orderByDesc(UserRating::getCreateTime);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public UserCreditVO getUserCredit(Long userId) {
        return userCreditService.getUserCredit(userId);
    }

    @Override
    public void updateCreditAfterOrderComplete(Long orderId) {
        Order order = orderService.getById(orderId);
        if (order != null) {
            userCreditService.updateCreditAfterOrderComplete(order.getBuyerId());
            userCreditService.updateCreditAfterOrderComplete(order.getSellerId());
        }
    }

    @Override
    public void updateCreditAfterOrderCancel(Long orderId) {
        Order order = orderService.getById(orderId);
        if (order != null) {
            userCreditService.updateCreditAfterOrderCancel(order.getBuyerId());
        }
    }

    private RatingVO convertToVO(UserRating rating) {
        RatingVO vo = new RatingVO();
        BeanUtils.copyProperties(rating, vo);

        vo.setRatingText(getRatingText(rating.getRating()));

        User reviewer = userService.getById(rating.getReviewerId());
        if (reviewer != null) {
            vo.setReviewerName(reviewer.getNickname());
            vo.setReviewerAvatar(reviewer.getAvatar());
        }

        User reviewee = userService.getById(rating.getRevieweeId());
        if (reviewee != null) {
            vo.setRevieweeName(reviewee.getNickname());
            vo.setRevieweeAvatar(reviewee.getAvatar());
        }

        return vo;
    }

    private String getRatingText(Integer rating) {
        return switch (rating) {
            case 1 -> "差评";
            case 2 -> "中评";
            case 3 -> "好评";
            default -> "未知";
        };
    }
}
