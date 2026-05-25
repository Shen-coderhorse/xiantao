package com.xiantao.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.entity.UserCredit;
import com.xiantao.mapper.UserCreditMapper;
import com.xiantao.service.UserCreditService;
import com.xiantao.vo.UserCreditVO;

@Service
public class UserCreditServiceImpl extends ServiceImpl<UserCreditMapper, UserCredit> implements UserCreditService {

    @Override
    public UserCreditVO getUserCredit(Long userId) {
        LambdaQueryWrapper<UserCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCredit::getUserId, userId);
        UserCredit credit = this.getOne(wrapper);
        if (credit == null) {
            initUserCredit(userId);
            credit = this.getOne(wrapper);
        }
        return convertToVO(credit);
    }

    @Override
    @Transactional
    public void initUserCredit(Long userId) {
        LambdaQueryWrapper<UserCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCredit::getUserId, userId);
        if (this.count(wrapper) > 0) {
            return;
        }
        UserCredit credit = new UserCredit();
        credit.setUserId(userId);
        credit.setCreditScore(500);
        credit.setTotalTransactions(0);
        credit.setCompletedTransactions(0);
        credit.setCancelledTransactions(0);
        credit.setGoodRatingCount(0);
        credit.setMediumRatingCount(0);
        credit.setBadRatingCount(0);
        credit.setViolationCount(0);
        this.save(credit);
    }

    @Override
    @Transactional
    public void updateCreditAfterOrderComplete(Long userId) {
        UserCredit credit = getUserCreditByUserId(userId);
        credit.setTotalTransactions(credit.getTotalTransactions() + 1);
        credit.setCompletedTransactions(credit.getCompletedTransactions() + 1);
        int score = credit.getCreditScore() + 10;
        credit.setCreditScore(Math.min(850, score));
        this.updateById(credit);
    }

    @Override
    @Transactional
    public void updateCreditAfterOrderCancel(Long userId) {
        UserCredit credit = getUserCreditByUserId(userId);
        credit.setCancelledTransactions(credit.getCancelledTransactions() + 1);
        int score = credit.getCreditScore() - 10;
        credit.setCreditScore(Math.max(300, score));
        this.updateById(credit);
    }

    @Override
    @Transactional
    public void updateCreditAfterRating(Long userId, Integer rating) {
        UserCredit credit = getUserCreditByUserId(userId);
        switch (rating) {
            case 3 -> {
                credit.setGoodRatingCount(credit.getGoodRatingCount() + 1);
                credit.setCreditScore(Math.min(850, credit.getCreditScore() + 5));
            }
            case 2 -> credit.setMediumRatingCount(credit.getMediumRatingCount() + 1);
            case 1 -> {
                credit.setBadRatingCount(credit.getBadRatingCount() + 1);
                credit.setCreditScore(Math.max(300, credit.getCreditScore() - 20));
            }
        }
        this.updateById(credit);
    }

    private UserCredit getUserCreditByUserId(Long userId) {
        LambdaQueryWrapper<UserCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCredit::getUserId, userId);
        UserCredit credit = this.getOne(wrapper);
        if (credit == null) {
            initUserCredit(userId);
            credit = this.getOne(wrapper);
        }
        return credit;
    }

    private UserCreditVO convertToVO(UserCredit credit) {
        UserCreditVO vo = new UserCreditVO();
        BeanUtils.copyProperties(credit, vo);

        vo.setCreditLevel(getCreditLevel(credit.getCreditScore()));
        vo.setCreditLevelColor(getCreditLevelColor(credit.getCreditScore()));
        return vo;
    }

    private String getCreditLevel(Integer score) {
        if (score == null)
            return "未知";
        if (score >= 800)
            return "信用极好";
        if (score >= 700)
            return "信用优秀";
        if (score >= 600)
            return "信用良好";
        if (score >= 500)
            return "信用一般";
        return "信用较差";
    }

    private String getCreditLevelColor(Integer score) {
        if (score == null)
            return "#909399";
        if (score >= 800)
            return "#F5A623";
        if (score >= 700)
            return "#67C23A";
        if (score >= 600)
            return "#409EFF";
        if (score >= 500)
            return "#E6A23C";
        return "#F56C6C";
    }
}
