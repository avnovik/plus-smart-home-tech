package ru.yandex.practicum.commerce.client;

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
import ru.yandex.practicum.commerce.dto.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @GetMapping
    PageProductDto getProducts(
            @RequestParam("category") ProductCategory category,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") List<String> sort
    );

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);
}
