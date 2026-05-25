package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiantao.common.Result;
import com.xiantao.entity.TransactionRecord;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.service.UserService;
import com.xiantao.vo.TransactionRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transaction")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final UserService userService;
    private final TransactionRecordService transactionRecordService;

    @GetMapping("/list")
    public Result<List<TransactionRecordVO>> getTransactionList(
            HttpServletRequest request,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(TransactionRecord::getTransactionType, type);
        }
        if (status != null) {
            wrapper.eq(TransactionRecord::getStatus, status);
        }
        wrapper.orderByDesc(TransactionRecord::getCreateTime);

        List<TransactionRecord> list = transactionRecordService.list(wrapper);
        List<TransactionRecordVO> voList = list.stream()
                .map(t -> {
                    TransactionRecordVO vo = new TransactionRecordVO();
                    vo.setId(t.getId());
                    vo.setOrderId(t.getOrderId());
                    vo.setOrderNo(t.getOrderNo());
                    vo.setTransactionType(t.getTransactionType());
                    vo.setTransactionTypeText(getTransactionTypeText(t.getTransactionType()));
                    vo.setAmount(t.getAmount());
                    vo.setFromUserId(t.getFromUserId());
                    vo.setToUserId(t.getToUserId());
                    vo.setStatus(t.getStatus());
                    vo.setStatusText(getStatusText(t.getStatus()));
                    vo.setRemark(t.getRemark());
                    vo.setCreateTime(t.getCreateTime());
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());

        return Result.success(voList);
    }

    private String getTransactionTypeText(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "支付";
            case 2 -> "退款";
            case 3 -> "提现";
            case 4 -> "充值";
            default -> "未知";
        };
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "处理中";
            case 1 -> "成功";
            case 2 -> "失败";
            default -> "未知";
        };
    }
}
