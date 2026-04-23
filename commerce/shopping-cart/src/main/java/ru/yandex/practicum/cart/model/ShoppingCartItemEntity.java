package ru.yandex.practicum.cart.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartItemEntity {

    @EmbeddedId
    private ShoppingCartItemId id;

    @MapsId("cartId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCartEntity cart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public ShoppingCartItemEntity(ShoppingCartEntity cart, java.util.UUID productId, Integer quantity) {
        this.cart = cart;
        this.id = new ShoppingCartItemId(cart.getId(), productId);
        this.quantity = quantity;
    }
}
