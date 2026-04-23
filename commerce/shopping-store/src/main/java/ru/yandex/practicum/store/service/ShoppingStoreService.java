package ru.yandex.practicum.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PageProductDto getProducts(ProductCategory category, Integer page, Integer size, List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<ProductEntity> productsPage = productRepository.findAllByProductCategoryAndProductState(
                category,
                ProductState.ACTIVE,
                pageable
        );

        return productMapper.toPageDto(productsPage);
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        ProductEntity entity = productMapper.toNewEntity(productDto);
        ProductEntity saved = productRepository.saveAndFlush(entity);
        return productMapper.toDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
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
        return productMapper.toDto(saved);
    }

    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
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
        return true;
    }

    @Transactional
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
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
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
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
