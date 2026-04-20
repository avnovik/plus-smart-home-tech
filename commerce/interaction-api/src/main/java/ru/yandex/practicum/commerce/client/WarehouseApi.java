package ru.yandex.practicum.commerce.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;

@RequestMapping("/api/v1/warehouse")
public interface WarehouseApi {

    @PutMapping
    void newProductInWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
