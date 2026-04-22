package ru.yandex.practicum.cart.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartEntity {

    @Id
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private CartState state;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemEntity> items = new HashSet<>();

    public ShoppingCartEntity(UUID id, String username, CartState state) {
        this.id = id;
        this.username = username;
        this.state = state;
    }
}
