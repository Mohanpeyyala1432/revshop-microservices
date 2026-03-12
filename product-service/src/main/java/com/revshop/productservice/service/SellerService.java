package com.revshop.productservice.service;

import com.revshop.productservice.model.Seller;
import java.util.List;

public interface SellerService {
    Seller addSeller(Seller seller);
    List<Seller> getAllSellers();
    Seller getSellerById(Long id);
}
