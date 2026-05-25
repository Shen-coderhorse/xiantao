package com.xiantao.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.RatingDTO;
import com.xiantao.entity.UserRating;
import com.xiantao.vo.RatingVO;
import com.xiantao.vo.UserCreditVO;

public interface UserRatingService extends IService<UserRating> {

    RatingVO createRating(Long reviewerId, RatingDTO dto);

    List<RatingVO> getUserRatings(Long userId);

    List<RatingVO> getUserReceivedRatings(Long userId);

    UserCreditVO getUserCredit(Long userId);

    void updateCreditAfterOrderComplete(Long orderId);

    void updateCreditAfterOrderCancel(Long orderId);
}
