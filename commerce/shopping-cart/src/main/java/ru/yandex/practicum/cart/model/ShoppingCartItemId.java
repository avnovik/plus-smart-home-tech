package ru.yandex.practicum.cart.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartItemId implements Serializable {

    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    public ShoppingCartItemId(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShoppingCartItemId that = (ShoppingCartItemId) o;
        return cartId.equals(that.cartId) && productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        int result = cartId.hashCode();
        result = 31 * result + productId.hashCode();
        return result;
    }
}
