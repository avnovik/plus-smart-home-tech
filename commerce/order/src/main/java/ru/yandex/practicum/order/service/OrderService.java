package ru.yandex.practicum.order.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.client.DeliveryClient;
import ru.yandex.practicum.commerce.client.PaymentClient;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.common.AddressDto;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.order.model.OrderEntity;
import ru.yandex.practicum.order.model.OrderItemEntity;
import ru.yandex.practicum.order.model.OrderState;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String DEFAULT_USERNAME = "unknown";
    private static final String USERNAME_EMPTY_REASON = "Имя пользователя не должно быть пустым";
    private static final String ORDER_NOT_FOUND_REASON = "Не найден заказ";

    private final OrderRepository orderRepository;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final WarehouseClient warehouseClient;

    @Transactional(readOnly = true)
    public List<OrderEntity> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USERNAME_EMPTY_REASON);
        }

        return orderRepository.findAllByUsername(username);
    }

    @Transactional
    public OrderEntity createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.shoppingCart();

        try {
            warehouseClient.checkProductQuantityEnoughForShoppingCart(cart);
        } catch (FeignException ex) {
            if (ex.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет заказываемого товара на складе");
            }
            throw ex;
        }

        UUID orderId = UUID.randomUUID();
        OrderEntity order = new OrderEntity(orderId, DEFAULT_USERNAME, OrderState.NEW);
        order.setShoppingCartId(cart.shoppingCartId());

        AddressDto address = request.deliveryAddress();
        if (address != null) {
            order.setCountry(address.country());
            order.setCity(address.city());
            order.setStreet(address.street());
            order.setHouse(address.house());
            order.setFlat(address.flat());
        }

        Map<UUID, Long> products = cart.products();
        if (products != null) {
            for (Map.Entry<UUID, Long> entry : products.entrySet()) {
                OrderItemEntity item = new OrderItemEntity(order, entry.getKey(), entry.getValue());
                order.getItems().add(item);
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity productReturn(ProductReturnRequest request) {
        OrderEntity order = getOrderOrThrow(request.orderId());

        Map<UUID, Long> products = request.products();
        if (products != null && !products.isEmpty()) {
            warehouseClient.acceptReturn(products);
        }

        order.setState(OrderState.PRODUCT_RETURNED);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity payment(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);

        if (order.getPaymentId() == null) {
            OrderDto dto = toDtoForCalculation(order);
            PaymentDto payment = paymentClient.payment(dto);
            order.setPaymentId(payment == null ? null : payment.paymentId());
            order.setState(OrderState.ON_PAYMENT);
        } else {
            order.setState(OrderState.PAID);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity paymentFailed(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity delivery(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);

        if (order.getState() == OrderState.ON_DELIVERY) {
            order.setState(OrderState.DELIVERED);
            return orderRepository.save(order);
        }

        if (order.getDeliveryId() == null) {
            AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
            AddressDto fromAddress = toDeliveryAddress(warehouseAddress);
            AddressDto toAddress = toDeliveryAddress(order);

            DeliveryDto request = new DeliveryDto(null, fromAddress, toAddress, order.getId(), DeliveryState.CREATED);
            DeliveryDto planned = deliveryClient.planDelivery(request);

            order.setDeliveryId(planned == null ? null : planned.deliveryId());
        }

        order.setState(OrderState.ON_DELIVERY);

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity deliveryFailed(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity complete(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);
        order.setState(OrderState.COMPLETED);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity calculateTotalCost(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);

        OrderDto dto = toDtoForCalculation(order);

        Double productCost = paymentClient.productCost(dto);
        order.setProductPrice(productCost == null ? null : java.math.BigDecimal.valueOf(productCost));

        Double totalCost = paymentClient.getTotalCost(dto);
        order.setTotalPrice(totalCost == null ? null : java.math.BigDecimal.valueOf(totalCost));

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity calculateDeliveryCost(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);

        OrderDto dto = toDtoForCalculation(order);

        Double deliveryCost = deliveryClient.deliveryCost(dto);
        order.setDeliveryPrice(deliveryCost == null ? null : java.math.BigDecimal.valueOf(deliveryCost));

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity assembly(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);

        if (order.getDeliveryWeight() != null && order.getDeliveryVolume() != null && order.getFragile() != null) {
            order.setState(OrderState.ASSEMBLED);
            return orderRepository.save(order);
        }

        OrderDto dto = toDtoForCalculation(order);
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest(order.getId(), dto.products());
        BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(request);

        if (booked != null) {
            order.setDeliveryWeight(booked.deliveryWeight());
            order.setDeliveryVolume(booked.deliveryVolume());
            order.setFragile(booked.fragile());
        }

        order.setState(OrderState.ASSEMBLED);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity assemblyFailed(UUID orderId) {
        OrderEntity order = getOrderOrThrow(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderRepository.save(order);
    }

    private OrderEntity getOrderOrThrow(UUID orderId) {
        if (orderId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND_REASON);
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND_REASON));
    }

    private AddressDto toDeliveryAddress(AddressDto address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.country(),
                address.city(),
                address.street(),
                address.house(),
                address.flat()
        );
    }

    private AddressDto toDeliveryAddress(OrderEntity order) {
        if (order == null) {
            return null;
        }
        return new AddressDto(
                order.getCountry(),
                order.getCity(),
                order.getStreet(),
                order.getHouse(),
                order.getFlat()
        );
    }

    private OrderDto toDtoForCalculation(OrderEntity order) {
        Map<UUID, Long> products = order.getItems()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        item -> item.getId().getProductId(),
                        OrderItemEntity::getQuantity
                ));

        return new OrderDto(
                order.getId(),
                order.getShoppingCartId(),
                products,
                order.getPaymentId(),
                order.getDeliveryId(),
                ru.yandex.practicum.commerce.dto.order.OrderState.valueOf(order.getState().name()),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice() == null ? null : order.getTotalPrice().doubleValue(),
                order.getDeliveryPrice() == null ? null : order.getDeliveryPrice().doubleValue(),
                order.getProductPrice() == null ? null : order.getProductPrice().doubleValue()
        );
    }
}
