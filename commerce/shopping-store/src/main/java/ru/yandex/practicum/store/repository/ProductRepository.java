package ru.yandex.practicum.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductState;
import ru.yandex.practicum.store.model.ProductEntity;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Page<ProductEntity> findAllByProductCategoryAndProductState(ProductCategory productCategory,
                                                               ProductState productState,
                                                               Pageable pageable);
}
