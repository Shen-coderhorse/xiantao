package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiantao.common.Result;
import com.xiantao.entity.TransactionRecord;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.vo.TransactionRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/transaction")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionRecordService transactionRecordService;

    @GetMapping("/list")
    public Result<Page<TransactionRecordVO>> getTransactionList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(TransactionRecord::getTransactionType, type);
        }
        if (status != null) {
            wrapper.eq(TransactionRecord::getStatus, status);
        }
        wrapper.orderByDesc(TransactionRecord::getCreateTime);

        Page<TransactionRecord> page = new Page<>(pageNum, pageSize);
        transactionRecordService.page(page, wrapper);

        List<TransactionRecordVO> voList = page.getRecords().stream()
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
                .collect(Collectors.toList());

        Page<TransactionRecordVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(voList);
        return Result.success(resultPage);
    }

    private String getTransactionTypeText(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "付款";
            case 2 -> "托管";
            case 3 -> "解冻";
            case 4 -> "退款";
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
