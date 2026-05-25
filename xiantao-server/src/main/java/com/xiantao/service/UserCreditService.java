package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.entity.UserCredit;
import com.xiantao.vo.UserCreditVO;

public interface UserCreditService extends IService<UserCredit> {

    UserCreditVO getUserCredit(Long userId);

    void initUserCredit(Long userId);

    void updateCreditAfterOrderComplete(Long userId);

    void updateCreditAfterOrderCancel(Long userId);

    void updateCreditAfterRating(Long userId, Integer rating);
}
