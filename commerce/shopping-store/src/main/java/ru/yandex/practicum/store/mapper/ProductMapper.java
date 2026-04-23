package ru.yandex.practicum.store.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.store.PageProductDto;
import ru.yandex.practicum.commerce.dto.store.PageableObject;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.SortObject;
import ru.yandex.practicum.store.model.ProductEntity;

import java.util.List;
import java.util.UUID;

/**
 * Маппер между JPA-сущностями shopping-store и DTO из interaction-api.
 */
@Component
public class ProductMapper {

    public ProductDto toDto(ProductEntity entity) {
        return new ProductDto(
                entity.getId(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getImageSrc(),
                entity.getQuantityState(),
                entity.getProductState(),
                entity.getProductCategory(),
                entity.getPrice()
        );
    }

    public SortObject toSortObject(Sort.Order order) {
        return new SortObject(
                order.getDirection().name(),
                order.getNullHandling().name(),
                order.isAscending(),
                order.getProperty(),
                order.isIgnoreCase()
        );
    }

    public ProductEntity toNewEntity(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(UUID.randomUUID());
        entity.setProductName(dto.productName());
        entity.setDescription(dto.description());
        entity.setImageSrc(dto.imageSrc());
        entity.setQuantityState(dto.quantityState());
        entity.setProductState(dto.productState());
        entity.setProductCategory(dto.productCategory());
        entity.setPrice(dto.price());
        return entity;
    }

    public void updateEntity(ProductEntity entity, ProductDto dto) {
        entity.setProductName(dto.productName());
        entity.setDescription(dto.description());
        entity.setImageSrc(dto.imageSrc());
        entity.setQuantityState(dto.quantityState());
        entity.setProductState(dto.productState());
        entity.setProductCategory(dto.productCategory());
        entity.setPrice(dto.price());
    }

    public PageProductDto toPageDto(Page<ProductEntity> page) {
        List<ProductDto> content = page.getContent().stream()
                .map(this::toDto)
                .toList();

        List<SortObject> sortObjects = page.getSort().stream()
                .map(this::toSortObject)
                .toList();

        PageableObject pageableObject = new PageableObject(
                page.getPageable().getOffset(),
                sortObjects,
                page.getPageable().isUnpaged(),
                page.getPageable().isPaged(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize()
        );

        return new PageProductDto(
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSize(),
                content,
                page.getNumber(),
                sortObjects,
                page.getNumberOfElements(),
                pageableObject,
                page.isEmpty()
        );
    }
}
