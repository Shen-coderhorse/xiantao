package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.dto.RatingDTO;
import com.xiantao.service.UserRatingService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.RatingVO;
import com.xiantao.vo.UserCreditVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class UserRatingController {

    private final UserRatingService userRatingService;

    @PostMapping
    public Result<RatingVO> create(@Valid @RequestBody RatingDTO dto, HttpServletRequest request) {
        Long reviewerId = JwtUtils.getCurrentUserId(request);
        RatingVO vo = userRatingService.createRating(reviewerId, dto);
        return Result.success(vo);
    }

    @GetMapping("/my")
    public Result<List<RatingVO>> myRatings(HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        List<RatingVO> list = userRatingService.getUserRatings(userId);
        return Result.success(list);
    }

    @GetMapping("/user/{userId}")
    public Result<List<RatingVO>> getUserRatings(@PathVariable Long userId) {
        List<RatingVO> list = userRatingService.getUserReceivedRatings(userId);
        return Result.success(list);
    }

    @GetMapping("/credit")
    public Result<UserCreditVO> getUserCredit(HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        UserCreditVO vo = userRatingService.getUserCredit(userId);
        return Result.success(vo);
    }
}
