package com.revshop.productservice.service;

import com.revshop.productservice.model.Seller;
import com.revshop.productservice.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setSellerId(1L);
    }

    @Test
    void addSeller_Success() {
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller result = sellerService.addSeller(seller);

        assertNotNull(result);
        assertEquals(1L, result.getSellerId());
        verify(sellerRepository).save(seller);
    }

    @Test
    void getAllSellers_Success() {
        when(sellerRepository.findAll()).thenReturn(Arrays.asList(seller));

        List<Seller> result = sellerService.getAllSellers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSellerId());
    }

    @Test
    void getSellerById_Found() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Seller result = sellerService.getSellerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getSellerId());
    }

    @Test
    void getSellerById_NotFound_ThrowsException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> sellerService.getSellerById(1L));
    }
}
