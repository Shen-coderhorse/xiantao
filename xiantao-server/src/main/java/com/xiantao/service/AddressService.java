package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.AddressDTO;
import com.xiantao.entity.Address;
import com.xiantao.vo.AddressVO;

import java.util.List;

public interface AddressService extends IService<Address> {

    List<AddressVO> getUserAddresses(Long userId);

    AddressVO createAddress(Long userId, AddressDTO dto);

    AddressVO updateAddress(Long userId, Long id, AddressDTO dto);

    void deleteAddress(Long userId, Long id);

    AddressVO setDefaultAddress(Long userId, Long id);

    AddressVO getDefaultAddress(Long userId);

    AddressVO getAddressById(Long userId, Long id);
}
