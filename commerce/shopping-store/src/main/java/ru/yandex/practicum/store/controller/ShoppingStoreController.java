package ru.yandex.practicum.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.client.ShoppingStoreApi;
import ru.yandex.practicum.commerce.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductState;
import ru.yandex.practicum.commerce.dto.store.QuantityState;
import ru.yandex.practicum.store.mapper.ProductMapper;
import ru.yandex.practicum.store.model.ProductEntity;
import ru.yandex.practicum.store.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер витрины онлайн-магазина.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public PageProductDto getProducts(ProductCategory category, Integer page, Integer size, List<String> sort) {
        log.info("GET /api/v1/shopping-store category={}, page={}, size={}, sort={}", category, page, size, sort);

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<ProductEntity> productsPage = productRepository.findAllByProductCategoryAndProductState(
                category,
                ProductState.ACTIVE,
                pageable
        );

        return productMapper.toPageDto(productsPage);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store createNewProduct");
        ProductEntity entity = productMapper.toNewEntity(productDto);
        ProductEntity saved = productRepository.saveAndFlush(entity);
        log.info("Created product productId={}", saved.getId());
        return productMapper.toDto(saved);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("POST /api/v1/shopping-store updateProduct productId={}", productDto == null ? null : productDto.productId());
        if (productDto.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        ProductEntity entity = productRepository.findById(productDto.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productDto.productId()
                ));

        productMapper.updateEntity(entity, productDto);
        ProductEntity saved = productRepository.saveAndFlush(entity);
        log.info("Updated product productId={}", saved.getId());
        return productMapper.toDto(saved);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore productId={}", productId);
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId
                ));

        entity.setProductState(ProductState.DEACTIVATE);
        productRepository.saveAndFlush(entity);
        log.info("Removed product productId={}", productId);
        return true;
    }

    @Override
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.info(
                "POST /api/v1/shopping-store/quantityState productId={}, quantityState={}",
                productId,
                quantityState
        );
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }
        if (quantityState == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantityState is required");
        }

        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId
                ));

        entity.setQuantityState(quantityState);
        productRepository.saveAndFlush(entity);
        log.info("Updated quantityState for productId={}", productId);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("GET /api/v1/shopping-store/{}", productId);
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId
                ));
        return productMapper.toDto(entity);
    }

    private Sort parseSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        List<String> tokens = sort.stream()
                .filter(s -> s != null && !s.isBlank())
                .toList();

        if (tokens.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders;
        boolean containsCommaFormat = tokens.stream().anyMatch(s -> s.contains(","));
        if (containsCommaFormat) {
            orders = tokens.stream()
                    .map(this::parseOrder)
                    .toList();
        } else {
            orders = new ArrayList<>();
            for (int i = 0; i < tokens.size(); i += 2) {
                String property = tokens.get(i);
                String direction = (i + 1) < tokens.size() ? tokens.get(i + 1) : "asc";
                orders.add(parseOrder(property + "," + direction));
            }
        }

        return Sort.by(orders);
    }

    private Sort.Order parseOrder(String value) {
        if (value == null || value.isBlank()) {
            return Sort.Order.by("id");
        }

        String[] parts = value.split(",", 2);
        String property = parts[0];
        String direction = parts.length > 1 ? parts[1] : "asc";

        if ("desc".equalsIgnoreCase(direction)) {
            return Sort.Order.desc(property);
        }
        return Sort.Order.asc(property);
    }
}
