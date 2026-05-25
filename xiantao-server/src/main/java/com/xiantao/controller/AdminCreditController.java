package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiantao.common.Result;
import com.xiantao.entity.UserCredit;
import com.xiantao.mapper.UserCreditMapper;
import com.xiantao.service.UserService;
import com.xiantao.vo.UserCreditVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/credit")
@RequiredArgsConstructor
public class AdminCreditController {

    private final UserService userService;
    private final UserCreditMapper userCreditMapper;

    @GetMapping("/list")
    public Result<List<UserCreditVO>> getCreditList(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer creditLevel) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        LambdaQueryWrapper<UserCredit> wrapper = new LambdaQueryWrapper<>();
        if (creditLevel != null) {
            wrapper.ge(UserCredit::getCreditScore, getMinScore(creditLevel));
        }
        wrapper.orderByDesc(UserCredit::getCreditScore);

        List<UserCredit> list = userCreditMapper.selectList(wrapper);
        List<UserCreditVO> voList = list.stream()
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

        return Result.success(voList);
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
