package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.TransactionRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionRecordController {

    private final TransactionRecordService transactionRecordService;

    @PostMapping("/pay/{orderId}")
    public Result<TransactionRecordVO> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        TransactionRecordVO vo = transactionRecordService.createPayment(orderId, userId);
        return Result.success(vo);
    }

    @PostMapping("/release/{orderId}")
    public Result<TransactionRecordVO> releasePayment(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        TransactionRecordVO vo = transactionRecordService.releasePayment(orderId, userId);
        return Result.success(vo);
    }

    @PostMapping("/refund/{orderId}")
    public Result<TransactionRecordVO> refundOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        TransactionRecordVO vo = transactionRecordService.refundPayment(orderId, userId);
        return Result.success(vo);
    }

    @GetMapping("/my")
    public Result<List<TransactionRecordVO>> myTransactions(HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        List<TransactionRecordVO> list = transactionRecordService.getUserTransactions(userId);
        return Result.success(list);
    }

    @GetMapping("/order/{orderId}")
    public Result<List<TransactionRecordVO>> orderTransactions(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        List<TransactionRecordVO> list = transactionRecordService.getOrderTransactions(orderId);
        return Result.success(list);
    }
}
