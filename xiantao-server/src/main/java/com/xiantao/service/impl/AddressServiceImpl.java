package com.xiantao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiantao.common.BusinessException;
import com.xiantao.dto.AddressDTO;
import com.xiantao.entity.Address;
import com.xiantao.mapper.AddressMapper;
import com.xiantao.service.AddressService;
import com.xiantao.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<AddressVO> getUserAddresses(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.orderByDesc(Address::getIsDefault);
        wrapper.orderByDesc(Address::getCreateTime);

        List<Address> addresses = this.list(wrapper);
        return addresses.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressVO createAddress(Long userId, AddressDTO dto) {
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        this.save(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public AddressVO updateAddress(Long userId, Long id, AddressDTO dto) {
        Address address = this.getById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此地址");
        }

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        BeanUtils.copyProperties(dto, address);
        this.updateById(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long id) {
        Address address = this.getById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此地址");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public AddressVO setDefaultAddress(Long userId, Long id) {
        Address address = this.getById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }

        clearDefault(userId);
        address.setIsDefault(1);
        this.updateById(address);

        return convertToVO(address);
    }

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.eq(Address::getIsDefault, 1);
        wrapper.last("LIMIT 1");

        Address address = this.getOne(wrapper);
        if (address == null) {
            return null;
        }
        return convertToVO(address);
    }

    @Override
    public AddressVO getAddressById(Long userId, Long id) {
        Address address = this.getById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此地址");
        }
        return convertToVO(address);
    }

    private void clearDefault(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.eq(Address::getIsDefault, 1);
        List<Address> defaults = this.list(wrapper);
        for (Address address : defaults) {
            address.setIsDefault(0);
            this.updateById(address);
        }
    }

    private AddressVO convertToVO(Address address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        vo.setFullAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        return vo;
    }
}
