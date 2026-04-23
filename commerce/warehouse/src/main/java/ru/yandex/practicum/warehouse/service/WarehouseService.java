package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.commerce.dto.common.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.DimensionDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.exception.WarehouseErrorReasons;
import ru.yandex.practicum.warehouse.model.WarehouseProductEntity;
import ru.yandex.practicum.warehouse.repository.WarehouseProductRepository;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;

    @Transactional
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        UUID productId = request.productId();
        if (warehouseProductRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WarehouseErrorReasons.PRODUCT_ALREADY_REGISTERED.message());
        }

        WarehouseProductEntity entity = new WarehouseProductEntity();
        entity.setProductId(productId);
        entity.setFragile(Boolean.TRUE.equals(request.fragile()));
        entity.setWeight(toLongRequired(request.weight()));

        DimensionDto dimension = request.dimension();
        entity.setWidth(toLongRequired(dimension.width()));
        entity.setHeight(toLongRequired(dimension.height()));
        entity.setDepth(toLongRequired(dimension.depth()));

        entity.setQuantity(0L);

        warehouseProductRepository.saveAndFlush(entity);
    }

    @Transactional
    public void addQuantity(UUID productId, Long quantityToAdd) {
        WarehouseProductEntity entity = warehouseProductRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, WarehouseErrorReasons.NO_PRODUCT_INFO.message()));

        entity.setQuantity(entity.getQuantity() + quantityToAdd);
        warehouseProductRepository.saveAndFlush(entity);
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkCart(ShoppingCartDto cart) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : cart.products().entrySet()) {
            UUID productId = entry.getKey();
            Long qtyRequested = entry.getValue();
            if (qtyRequested == null) {
                continue;
            }

            WarehouseProductEntity entity = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, WarehouseErrorReasons.PRODUCT_NOT_IN_WAREHOUSE.message()));

            if (entity.getQuantity() < qtyRequested) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WarehouseErrorReasons.LOW_QUANTITY.message());
            }

            totalWeight += entity.getWeight() * qtyRequested;
            totalVolume += (entity.getWidth() * entity.getHeight() * entity.getDepth()) * qtyRequested;
            fragile = fragile || Boolean.TRUE.equals(entity.getFragile());
        }

        return new BookedProductsDto(totalWeight, totalVolume, fragile);
    }

    private Long toLongRequired(Double value) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WarehouseErrorReasons.INVALID_PRODUCT_PARAMS.message());
        }
        return Math.round(value);
    }
}
