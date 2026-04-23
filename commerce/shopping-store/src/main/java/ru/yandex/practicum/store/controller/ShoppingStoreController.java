package ru.yandex.practicum.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.ShoppingStoreApi;
import ru.yandex.practicum.commerce.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.QuantityState;
import ru.yandex.practicum.store.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер витрины онлайн-магазина.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    public PageProductDto getProducts(ProductCategory category, Integer page, Integer size, List<String> sort) {
        log.info("GET /api/v1/shopping-store category={}, page={}, size={}, sort={}", category, page, size, sort);
        return shoppingStoreService.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store createNewProduct");
        ProductDto created = shoppingStoreService.createNewProduct(productDto);
        log.info("Created product productId={}", created.productId());
        return created;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("POST /api/v1/shopping-store updateProduct productId={}", productDto.productId());
        ProductDto updated = shoppingStoreService.updateProduct(productDto);
        log.info("Updated product productId={}", updated.productId());
        return updated;
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore productId={}", productId);
        Boolean result = shoppingStoreService.removeProductFromStore(productId);
        log.info("Removed product productId={}", productId);
        return result;
    }

    @Override
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.info("Updated quantityState for productId={}", productId);
        return shoppingStoreService.setProductQuantityState(productId, quantityState);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("GET /api/v1/shopping-store/{}", productId);
        return shoppingStoreService.getProduct(productId);
    }
}
