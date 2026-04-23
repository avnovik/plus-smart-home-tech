package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.WarehouseApi;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.service.WarehouseService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {

    private static final List<AddressDto> WAREHOUSE_ADDRESSES = List.of(
            new AddressDto("Россия", "Москва", "Тверская", "1", "1"),
            new AddressDto("Россия", "Санкт-Петербург", "Невский проспект", "1", "1")
    );

    private final WarehouseService warehouseService;

    private final AddressDto warehouseAddress = WAREHOUSE_ADDRESSES.get(ThreadLocalRandom.current().nextInt(WAREHOUSE_ADDRESSES.size()));

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("newProductInWarehouse: productId={}", request == null ? null : request.productId());
        warehouseService.registerNewProduct(request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("checkProductQuantityEnoughForShoppingCart: cartId={}", shoppingCart == null ? null : shoppingCart.shoppingCartId());
        return warehouseService.checkCart(shoppingCart);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("addProductToWarehouse: productId={}, quantity={}", request == null ? null : request.productId(), request == null ? null : request.quantity());
        warehouseService.addQuantity(request.productId(), request.quantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("getWarehouseAddress");
        return warehouseAddress;
    }
}
