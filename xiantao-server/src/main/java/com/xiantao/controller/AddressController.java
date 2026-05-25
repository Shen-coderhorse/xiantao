package com.xiantao.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiantao.common.Result;
import com.xiantao.dto.AddressDTO;
import com.xiantao.service.AddressService;
import com.xiantao.utils.JwtUtils;
import com.xiantao.vo.AddressVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    public Result<List<AddressVO>> list(HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        List<AddressVO> list = addressService.getUserAddresses(userId);
        return Result.success(list);
    }

    @PostMapping
    public Result<AddressVO> create(@Valid @RequestBody AddressDTO dto, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        AddressVO vo = addressService.createAddress(userId, dto);
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<AddressVO> update(@PathVariable Long id, @Valid @RequestBody AddressDTO dto,
            HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        AddressVO vo = addressService.updateAddress(userId, id, dto);
        return Result.success(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        addressService.deleteAddress(userId, id);
        return Result.success(null);
    }

    @PutMapping("/{id}/default")
    public Result<AddressVO> setDefault(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        AddressVO vo = addressService.setDefaultAddress(userId, id);
        return Result.success(vo);
    }

    @GetMapping("/default")
    public Result<AddressVO> getDefault(HttpServletRequest request) {
        Long userId = JwtUtils.getCurrentUserId(request);
        AddressVO vo = addressService.getDefaultAddress(userId);
        return Result.success(vo);
    }
}
