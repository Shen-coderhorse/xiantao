package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiantao.common.Result;
import com.xiantao.entity.User;
import com.xiantao.entity.UserCredit;
import com.xiantao.mapper.UserCreditMapper;
import com.xiantao.service.UserService;
import com.xiantao.vo.UserCreditVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/credit")
@RequiredArgsConstructor
public class AdminCreditController {

    private final UserCreditMapper userCreditMapper;
    private final UserService userService;

    @GetMapping("/list")
    public Result<Page<UserCreditVO>> getCreditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer creditLevel) {

        LambdaQueryWrapper<UserCredit> wrapper = new LambdaQueryWrapper<>();
        if (creditLevel != null) {
            wrapper.ge(UserCredit::getCreditScore, getMinScore(creditLevel));
        }
        wrapper.orderByDesc(UserCredit::getCreditScore);

        Page<UserCredit> page = new Page<>(pageNum, pageSize);
        userCreditMapper.selectPage(page, wrapper);

        // 批量查询用户昵称
        List<Long> userIds = page.getRecords().stream()
                .map(UserCredit::getUserId)
                .collect(Collectors.toList());
        Map<Long, String> userNameMap = Map.of();
        if (!userIds.isEmpty()) {
            userNameMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getNickname));
        }

        List<UserCreditVO> voList = page.getRecords().stream()
                .map(c -> {
                    UserCreditVO vo = new UserCreditVO();
                    vo.setUserId(c.getUserId());
                    vo.setCreditScore(c.getCreditScore());
                    vo.setCreditLevel(getCreditLevelText(c.getCreditScore()));
                    vo.setCreditLevelColor(getCreditLevelColor(c.getCreditScore()));
                    vo.setTotalTransactions(c.getTotalTransactions());
                    vo.setCompletedTransactions(c.getCompletedTransactions());
                    vo.setCancelledTransactions(c.getCancelledTransactions());
                    vo.setGoodRatingCount(c.getGoodRatingCount());
                    vo.setMediumRatingCount(c.getMediumRatingCount());
                    vo.setBadRatingCount(c.getBadRatingCount());
                    vo.setViolationCount(c.getViolationCount());
                    return vo;
                })
                .collect(Collectors.toList());

        Page<UserCreditVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(voList);
        return Result.success(resultPage);
    }

    private int getMinScore(Integer level) {
        return switch (level) {
            case 1 -> 900;
            case 2 -> 700;
            case 3 -> 500;
            default -> 0;
        };
    }

    private String getCreditLevelText(Integer score) {
        if (score >= 900) return "极好";
        if (score >= 700) return "优秀";
        if (score >= 500) return "良好";
        if (score >= 350) return "一般";
        return "较差";
    }

    private String getCreditLevelColor(Integer score) {
        if (score >= 900) return "#67C23A";
        if (score >= 700) return "#409EFF";
        if (score >= 500) return "#E6A23C";
        if (score >= 350) return "#F56C6C";
        return "#909399";
    }
}
