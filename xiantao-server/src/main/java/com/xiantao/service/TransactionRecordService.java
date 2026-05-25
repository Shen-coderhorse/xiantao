package com.xiantao.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.entity.TransactionRecord;
import com.xiantao.vo.TransactionRecordVO;

public interface TransactionRecordService extends IService<TransactionRecord> {

    TransactionRecordVO createPayment(Long orderId, Long buyerId);

    TransactionRecordVO releasePayment(Long orderId, Long buyerId);

    TransactionRecordVO refundPayment(Long orderId, Long userId);

    List<TransactionRecordVO> getUserTransactions(Long userId);

    List<TransactionRecordVO> getOrderTransactions(Long orderId);
}
