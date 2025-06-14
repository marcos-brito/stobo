package com.stobo.server.cart.application;

import com.stobo.proto.cart.AddCartItemRequest;
import com.stobo.proto.cart.AddCartItemResponse;
import com.stobo.proto.cart.Item;
import com.stobo.proto.cart.ItemChange;
import com.stobo.proto.cart.CartServiceGrpc.CartServiceImplBase;
import com.stobo.proto.cart.ClearCartItemRequest;
import com.stobo.proto.cart.ClearCartItemResponse;
import com.stobo.proto.cart.ClearCartItemsRequest;
import com.stobo.proto.cart.ClearCartItemsResponse;
import com.stobo.proto.cart.DeleteCartItemRequest;
import com.stobo.proto.cart.DeleteCartItemResponse;
import com.stobo.proto.cart.GetCartItemsRequest;
import com.stobo.proto.cart.GetCartItemsResponse;
import com.stobo.server.cart.domain.Cart;
import com.stobo.server.cart.domain.CartService;
import com.stobo.server.common.proto.Timestamp;
import com.stobo.server.common.proto.Id;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;

class CartGrpcService extends CartServiceImplBase {
    private final CartService cartService;

    CartGrpcService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void getCartItems(GetCartItemsRequest request,
            StreamObserver<GetCartItemsResponse> responseObserver) {
        Cart cart = this.cartService.findCartByUserId(request.getUserId());
        GetCartItemsResponse message = GetCartItemsResponse.newBuilder()
                .addAllItems(this.toItems(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void clearCartItems(ClearCartItemsRequest request,
            StreamObserver<ClearCartItemsResponse> responseObserver) {
        Cart cart = this.cartService.clearCart(request.getUserId());
        ClearCartItemsResponse message = ClearCartItemsResponse.newBuilder()
                .addAllItems(this.toItems(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void addCartItem(AddCartItemRequest request,
            StreamObserver<AddCartItemResponse> responseObserver) {
        ItemChange change = request.getChange();
        Cart cart = this.cartService.addItem(
                change.getUserId(), change.getProductId(), change.getQuantity());
        AddCartItemResponse message = AddCartItemResponse.newBuilder()
                .addAllItems(this.toItems(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCartItem(DeleteCartItemRequest request,
            StreamObserver<DeleteCartItemResponse> responseObserver) {
        ItemChange change = request.getChange();
        Cart cart = this.cartService.removeItem(
                change.getUserId(), change.getProductId(), change.getQuantity());
        DeleteCartItemResponse message = DeleteCartItemResponse.newBuilder()
                .addAllItems(this.toItems(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void clearCartItem(ClearCartItemRequest request,
            StreamObserver<ClearCartItemResponse> responseObserver) {
        ItemChange change = request.getChange();
        Cart cart = this.cartService.clearItem(change.getUserId(), change.getProductId());
        ClearCartItemResponse message = ClearCartItemResponse.newBuilder()
                .addAllItems(this.toItems(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    private List<Item> toItems(Cart cart) {
        return cart.getItems()
                .stream()
                .map(this::toItem)
                .collect(Collectors.toList());
    }

    private Item toItem(com.stobo.server.cart.domain.Item item) {
        return Item.newBuilder()
                .setProductId(Id.encode(item.getProductId()))
                .setQuantity(item.getQuantity())
                .setAddedAt(Timestamp.fromInstant(item.getAddedAt()))
                .build();
    }
}
