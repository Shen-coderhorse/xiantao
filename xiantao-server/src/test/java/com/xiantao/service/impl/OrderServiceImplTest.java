package com.xiantao.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.xiantao.common.BusinessException;
import com.xiantao.dto.OrderCreateDTO;
import com.xiantao.entity.Order;
import com.xiantao.entity.Product;
import com.xiantao.service.AddressService;
import com.xiantao.service.LogisticsService;
import com.xiantao.service.ProductService;
import com.xiantao.service.TransactionRecordService;
import com.xiantao.service.UserRatingService;
import com.xiantao.service.UserService;
import com.xiantao.vo.AddressVO;
import com.xiantao.vo.OrderVO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderServiceImpl 核心交易路径单元测试。
 * 通过 spy 隔离 MyBatis-Plus 持久层（save/getById/updateById），只验证业务规则与协作调用。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTest {

    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private AddressService addressService;
    @Mock
    private LogisticsService logisticsService;
    @Mock
    private TransactionRecordService transactionRecordService;
    @Mock
    private UserRatingService userRatingService;

    private OrderServiceImpl orderService;

    private static final Long BUYER_ID = 100L;
    private static final Long SELLER_ID = 200L;
    private static final Long PRODUCT_ID = 1L;
    private static final Long ADDRESS_ID = 10L;
    private static final Long ORDER_ID = 1000L;

    @BeforeEach
    void setUp() {
        orderService = spy(new OrderServiceImpl(
                productService, userService, addressService,
                logisticsService, transactionRecordService, userRatingService));
    }

    private Product onSaleProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setTitle("二手键盘");
        product.setPrice(new BigDecimal("99.00"));
        product.setSellerId(SELLER_ID);
        product.setStatus(1);
        return product;
    }

    private Order order(int status) {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderNo("20260730000001");
        order.setProductId(PRODUCT_ID);
        order.setProductTitle("二手键盘");
        order.setProductPrice(new BigDecimal("99.00"));
        order.setSellerId(SELLER_ID);
        order.setBuyerId(BUYER_ID);
        order.setAddressId(ADDRESS_ID);
        order.setStatus(status);
        order.setCreateTime(LocalDateTime.now());
        return order;
    }

    private OrderCreateDTO createDTO() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setProductId(PRODUCT_ID);
        dto.setAddressId(ADDRESS_ID);
        return dto;
    }

    // ==================== createOrder ====================

    @Test
    @DisplayName("创建订单：商品不存在时拒绝")
    void createOrder_productNotFound() {
        when(productService.getById(PRODUCT_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(BUYER_ID, createDTO()));
        assertEquals("商品不存在", ex.getMessage());
    }

    @Test
    @DisplayName("创建订单：商品已下架/已售出时拒绝")
    void createOrder_productNotOnSale() {
        Product product = onSaleProduct();
        product.setStatus(0);
        when(productService.getById(PRODUCT_ID)).thenReturn(product);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(BUYER_ID, createDTO()));
        assertEquals("商品已下架或已售出", ex.getMessage());
    }

    @Test
    @DisplayName("创建订单：不能购买自己的商品")
    void createOrder_buyOwnProduct() {
        when(productService.getById(PRODUCT_ID)).thenReturn(onSaleProduct());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(SELLER_ID, createDTO()));
        assertEquals("不能购买自己的商品", ex.getMessage());
    }

    @Test
    @DisplayName("创建订单：收货地址不存在时拒绝且不落单")
    void createOrder_addressNotFound() {
        when(productService.getById(PRODUCT_ID)).thenReturn(onSaleProduct());
        when(addressService.getAddressById(BUYER_ID, ADDRESS_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(BUYER_ID, createDTO()));
        assertEquals("收货地址不存在", ex.getMessage());
        verify(orderService, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("创建订单：成功路径落单并将商品置为交易中")
    void createOrder_success() {
        Product product = onSaleProduct();
        when(productService.getById(PRODUCT_ID)).thenReturn(product);
        when(addressService.getAddressById(BUYER_ID, ADDRESS_ID)).thenReturn(new AddressVO());
        doReturn(true).when(orderService).save(any(Order.class));
        when(productService.updateById(product)).thenReturn(true);

        OrderVO vo = orderService.createOrder(BUYER_ID, createDTO());

        assertNotNull(vo);
        assertEquals("待付款", vo.getStatusText());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals(0, saved.getStatus());
        assertEquals(BUYER_ID, saved.getBuyerId());
        assertEquals(SELLER_ID, saved.getSellerId());
        assertNotNull(saved.getOrderNo());

        // 商品同步下架（进入交易中）
        assertEquals(0, product.getStatus());
        verify(productService).updateById(product);
    }

    // ==================== payOrder ====================

    @Test
    @DisplayName("支付订单：订单不存在时拒绝")
    void payOrder_orderNotFound() {
        doReturn(null).when(orderService).getById(ORDER_ID);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.payOrder(BUYER_ID, ORDER_ID));
        assertEquals("订单不存在", ex.getMessage());
    }

    @Test
    @DisplayName("支付订单：非买家无权支付")
    void payOrder_notBuyer() {
        doReturn(order(0)).when(orderService).getById(ORDER_ID);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.payOrder(SELLER_ID, ORDER_ID));
        assertEquals("无权操作此订单", ex.getMessage());
        verify(transactionRecordService, never()).createPayment(any(), any());
    }

    @Test
    @DisplayName("支付订单：状态非待付款时拒绝")
    void payOrder_wrongStatus() {
        doReturn(order(1)).when(orderService).getById(ORDER_ID);

        assertThrows(BusinessException.class, () -> orderService.payOrder(BUYER_ID, ORDER_ID));
        verify(transactionRecordService, never()).createPayment(any(), any());
    }

    @Test
    @DisplayName("支付订单：成功路径委托交易流水服务")
    void payOrder_success() {
        doReturn(order(0)).when(orderService).getById(ORDER_ID);

        OrderVO vo = orderService.payOrder(BUYER_ID, ORDER_ID);

        assertNotNull(vo);
        verify(transactionRecordService).createPayment(ORDER_ID, BUYER_ID);
    }

    // ==================== cancelOrder（含退款回滚分支）====================

    @Test
    @DisplayName("取消订单：已完成订单不可取消")
    void cancelOrder_completedOrder() {
        doReturn(order(2)).when(orderService).getById(ORDER_ID);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancelOrder(BUYER_ID, ORDER_ID));
        assertEquals("只能取消待付款或待发货的订单", ex.getMessage());
        verify(transactionRecordService, never()).refundPayment(any(), any());
    }

    @Test
    @DisplayName("取消订单：无关用户无权取消")
    void cancelOrder_unrelatedUser() {
        doReturn(order(0)).when(orderService).getById(ORDER_ID);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancelOrder(999L, ORDER_ID));
        assertEquals("无权操作此订单", ex.getMessage());
    }

    @Test
    @DisplayName("取消待付款订单：不触发退款，商品恢复在售")
    void cancelOrder_unpaid_noRefund() {
        Order order = order(0);
        Product product = onSaleProduct();
        product.setStatus(0);
        doReturn(order).when(orderService).getById(ORDER_ID);
        doReturn(true).when(orderService).updateById(order);
        when(productService.getById(PRODUCT_ID)).thenReturn(product);

        OrderVO vo = orderService.cancelOrder(BUYER_ID, ORDER_ID);

        assertNotNull(vo);
        assertEquals(3, order.getStatus());
        verify(transactionRecordService, never()).refundPayment(any(), any());
        // 商品恢复在售
        assertEquals(1, product.getStatus());
        verify(productService).updateById(product);
        verify(userRatingService).updateCreditAfterOrderCancel(ORDER_ID);
    }

    @Test
    @DisplayName("取消已付款订单：先退款再恢复商品（回滚分支）")
    void cancelOrder_paid_refunds() {
        Order order = order(1);
        Product product = onSaleProduct();
        product.setStatus(0);
        doReturn(order).when(orderService).getById(ORDER_ID);
        doReturn(true).when(orderService).updateById(order);
        when(productService.getById(PRODUCT_ID)).thenReturn(product);

        OrderVO vo = orderService.cancelOrder(BUYER_ID, ORDER_ID);

        assertNotNull(vo);
        assertEquals(3, order.getStatus());
        verify(transactionRecordService).refundPayment(ORDER_ID, BUYER_ID);
        assertEquals(1, product.getStatus());
        verify(userRatingService).updateCreditAfterOrderCancel(ORDER_ID);
    }

    // ==================== completeOrder ====================

    @Test
    @DisplayName("确认收货：状态非待发货时拒绝")
    void completeOrder_wrongStatus() {
        doReturn(order(0)).when(orderService).getById(ORDER_ID);

        assertThrows(BusinessException.class, () -> orderService.completeOrder(BUYER_ID, ORDER_ID));
        verify(transactionRecordService, never()).releasePayment(any(), any());
    }

    @Test
    @DisplayName("确认收货：成功路径解冻货款、商品置已售、更新信用")
    void completeOrder_success() {
        Order order = order(1);
        Product product = onSaleProduct();
        product.setStatus(0);
        doReturn(order).when(orderService).getById(ORDER_ID);
        when(productService.getById(PRODUCT_ID)).thenReturn(product);

        OrderVO vo = orderService.completeOrder(BUYER_ID, ORDER_ID);

        assertNotNull(vo);
        verify(transactionRecordService).releasePayment(ORDER_ID, BUYER_ID);
        assertEquals(2, product.getStatus());
        verify(userRatingService).updateCreditAfterOrderComplete(ORDER_ID);
    }

    // ==================== shipOrder ====================

    @Test
    @DisplayName("发货：非卖家无权发货")
    void shipOrder_notSeller() {
        doReturn(order(1)).when(orderService).getById(ORDER_ID);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.shipOrder(BUYER_ID, ORDER_ID, new com.xiantao.dto.ShipDTO()));
        assertEquals("只有卖家可以发货", ex.getMessage());
        verify(logisticsService, never()).createLogistics(any(), any());
    }

    @Test
    @DisplayName("发货：成功路径委托物流服务")
    void shipOrder_success() {
        doReturn(order(1)).when(orderService).getById(ORDER_ID);
        com.xiantao.dto.ShipDTO shipDTO = new com.xiantao.dto.ShipDTO();

        OrderVO vo = orderService.shipOrder(SELLER_ID, ORDER_ID, shipDTO);

        assertNotNull(vo);
        verify(logisticsService).createLogistics(ORDER_ID, shipDTO);
    }
}
