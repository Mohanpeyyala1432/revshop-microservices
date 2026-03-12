package com.revshop.cartservice.service;

import com.revshop.cartservice.client.ProductClient;
import com.revshop.cartservice.dto.ProductDTO;
import com.revshop.cartservice.dto.WishlistDTO;
import com.revshop.cartservice.model.Wishlist;
import com.revshop.cartservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductClient productClient;

    public void addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return;
        }
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public List<WishlistDTO> getWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
            .map(wishlist -> {
                ProductDTO product = productClient.getProductById(wishlist.getProductId());
                return new WishlistDTO(wishlist.getWishlistId(), product);
            })
            .collect(Collectors.toList());
    }
}
