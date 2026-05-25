package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.dto.OrderCreateDTO;
import com.xiantao.dto.OrderQueryDTO;
import com.xiantao.entity.Order;
import com.xiantao.vo.OrderVO;
import com.xiantao.vo.PageVO;

public interface OrderService extends IService<Order> {

    OrderVO createOrder(Long buyerId, OrderCreateDTO dto);

    PageVO<OrderVO> getOrderList(Long userId, OrderQueryDTO query);

    OrderVO getOrderDetail(Long userId, Long id);

    OrderVO payOrder(Long userId, Long id);

    OrderVO completeOrder(Long userId, Long id);

    OrderVO cancelOrder(Long userId, Long id);

    OrderVO shipOrder(Long userId, Long id, com.xiantao.dto.ShipDTO shipDTO);

    OrderVO receiveOrder(Long userId, Long id);
}
