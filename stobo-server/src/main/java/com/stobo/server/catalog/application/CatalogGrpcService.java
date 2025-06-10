package com.stobo.server.catalog.application;

import com.stobo.proto.catalog.AddProductCategoryRequest;
import com.stobo.proto.catalog.AddProductCategoryResponse;
import com.stobo.proto.catalog.CatalogServiceGrpc;
import com.stobo.proto.catalog.Category;
import com.stobo.proto.catalog.CategoryChange;
import com.stobo.proto.catalog.CreateProductRequest;
import com.stobo.proto.catalog.CreateProductResponse;
import com.stobo.proto.catalog.DeleteProductCategoryRequest;
import com.stobo.proto.catalog.DeleteProductCategoryResponse;
import com.stobo.proto.catalog.DeleteProductRequest;
import com.stobo.proto.catalog.DeleteProductResponse;
import com.stobo.proto.catalog.GetProductRequest;
import com.stobo.proto.catalog.GetProductResponse;
import com.stobo.proto.catalog.Product;
import com.stobo.proto.catalog.Price;
import com.stobo.server.catalog.domain.CatalogService;
import com.stobo.server.common.TimestampMapper;
import com.stobo.server.common.id.OpaqueId;

import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
class CatalogGrpcService extends CatalogServiceGrpc.CatalogServiceImplBase {
    private final CatalogService catalogService;

    public CatalogGrpcService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void getProduct(GetProductRequest request,
            StreamObserver<GetProductResponse> responseObserver) {
        com.stobo.server.catalog.domain.Product product = this.catalogService.findProductById(request.getProductId());
        GetProductResponse message = GetProductResponse.newBuilder()
                .setProduct(this.toProduct(product))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request,
            StreamObserver<CreateProductResponse> responseObserver) {
        com.stobo.server.catalog.domain.Product product = this.catalogService.createProduct(
                this.fromProduct(request.getProduct()));
        CreateProductResponse message = CreateProductResponse.newBuilder()
                .setProduct(this.toProduct(product))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(DeleteProductRequest request,
            StreamObserver<DeleteProductResponse> responseObserver) {
        com.stobo.server.catalog.domain.Product product = this.catalogService.deleteProduct(request.getProductId());
        DeleteProductResponse message = DeleteProductResponse.newBuilder()
                .setProduct(this.toProduct(product))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void addProductCategory(
            AddProductCategoryRequest request,
            StreamObserver<AddProductCategoryResponse> responseObserver) {
        CategoryChange change = request.getChange();
        com.stobo.server.catalog.domain.Product product = this.catalogService.addProductCategory(change.getProductId(),
                change.getCategory());
        AddProductCategoryResponse message = AddProductCategoryResponse.newBuilder()
                .setProduct(this.toProduct(product))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
        ;
    }

    @Override
    public void deleteProductCategory(
            DeleteProductCategoryRequest request,
            StreamObserver<DeleteProductCategoryResponse> responseObserver) {
        CategoryChange change = request.getChange();
        com.stobo.server.catalog.domain.Product product = this.catalogService.deleteProductCategory(
                change.getProductId(),
                change.getCategory());
        DeleteProductCategoryResponse message = DeleteProductCategoryResponse.newBuilder()
                .setProduct(this.toProduct(product))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
        ;
    }

    private Product toProduct(com.stobo.server.catalog.domain.Product product) {
        return Product.newBuilder()
                .setTitle(product.getName())
                .setDesc(product.getDesc())
                .setNumber(product.getNumber())
                .setWeight(product.getWeight())
                .setYear(TimestampMapper.fromInstant(product.getYear()))
                .setPrice(this.toPrice(product.getPrice()))
                .addAllCategories(this.toCategories(product.getCategories()))
                .build();
    }

    private Price toPrice(com.stobo.server.catalog.domain.Price price) {
        return Price.newBuilder()
                .setCurrency(price.getCurrency())
                .setCents(price.getCents())
                .build();
    }

    private List<Category> toCategories(List<com.stobo.server.catalog.domain.Category> categories) {
        return categories.stream()
                .map(category -> Category.newBuilder().setId(OpaqueId.encode(category.getId()))
                        .setName(category.getName()).build())
                .collect(Collectors.toList());
    }

    private com.stobo.server.catalog.domain.Product fromProduct(Product product) {
        return com.stobo.server.catalog.domain.Product.builder()
                .name(product.getTitle())
                .desc(product.getDesc())
                .number(product.getNumber())
                .weight(product.getWeight())
                .year(TimestampMapper.toInstant(product.getYear()))
                .price(this.fromPrice(product.getPrice()))
                .categories(this.fromCategories(product.getCategoriesList()))
                .build();
    }

    private com.stobo.server.catalog.domain.Price fromPrice(Price price) {
        return com.stobo.server.catalog.domain.Price.builder()
                .currency(price.getCurrency())
                .cents(price.getCents())
                .build();
    }

    private List<com.stobo.server.catalog.domain.Category> fromCategories(List<Category> categories) {
        return categories.stream().map(category -> com.stobo.server.catalog.domain.Category.builder()
                .name(category.getName())
                .build()).collect(Collectors.toList());
    }
}
