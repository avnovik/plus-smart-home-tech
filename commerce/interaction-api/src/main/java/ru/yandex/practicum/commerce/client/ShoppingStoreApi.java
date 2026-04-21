package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.QuantityState;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @GetMapping
    PageProductDto getProducts(
            @RequestParam("category") ProductCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "sort", required = false) List<String> sort
    );

    @PutMapping
    ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState
    );

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);
}
