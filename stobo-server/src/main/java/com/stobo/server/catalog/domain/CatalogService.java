package com.stobo.server.catalog.domain;

import com.stobo.server.catalog.exception.NoAssociatedCategory;
import com.stobo.server.common.exception.NotFoundException;
import com.stobo.server.common.proto.Id;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {
    private final ProductRepository productRepository;

    CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        if (product.getCategories().isEmpty())
            throw new NoAssociatedCategory();

        return this.productRepository.save(product);
    }

    public Product addProductCategory(String productId, String category) {
        return this.findAnd(productId, product -> product.addCategory(category));
    }

    public Product deleteProductCategory(String productId, String category) {
        return this.findAnd(productId, product -> product.deleteCategory(category));
    }

    @Transactional
    public Product deleteProduct(String productId) {
        Product product = this.findProductById(productId);

        this.productRepository.delete(product);
        return product;
    }

    @Transactional
    public Product findAnd(String productId, Consumer<Product> consumer) {
        Product product = this.findProductById(productId);

        consumer.accept(product);
        return this.productRepository.save(product);
    }

    public Product findProductById(String productId) {
        return Id.decodeLong(productId)
                .flatMap(this.productRepository::findById)
                .orElseThrow(() -> new NotFoundException("product", productId));
    }
}
