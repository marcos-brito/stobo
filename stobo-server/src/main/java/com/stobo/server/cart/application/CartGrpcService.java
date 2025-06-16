package com.stobo.server.cart.application;

import com.stobo.proto.cart.AddCartEntryRequest;
import com.stobo.proto.cart.AddCartEntryResponse;
import com.stobo.proto.cart.Entry;
import com.stobo.proto.cart.Change;
import com.stobo.proto.cart.CartServiceGrpc.CartServiceImplBase;
import com.stobo.proto.cart.GetCartEntriesRequest;
import com.stobo.proto.cart.GetCartEntriesResponse;
import com.stobo.proto.cart.ClearCartEntryRequest;
import com.stobo.proto.cart.ClearCartEntryResponse;
import com.stobo.proto.cart.ClearCartEntriesRequest;
import com.stobo.proto.cart.ClearCartEntriesResponse;
import com.stobo.proto.cart.DeleteCartEntryRequest;
import com.stobo.proto.cart.DeleteCartEntryResponse;
import com.stobo.server.cart.domain.Cart;
import com.stobo.server.cart.domain.CartService;
import com.stobo.server.common.proto.Timestamp;
import com.stobo.server.common.proto.Item;

import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;

class CartGrpcService extends CartServiceImplBase {
    private final CartService cartService;

    CartGrpcService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void getCartEntries(GetCartEntriesRequest request, StreamObserver<GetCartEntriesResponse> responseObserver) {
        Cart cart = this.cartService.findCartByUserId(request.getUserId());
        GetCartEntriesResponse message = GetCartEntriesResponse.newBuilder()
                .addAllEntries(this.toEntries(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();

    }

    @Override
    public void clearCartEntries(ClearCartEntriesRequest request,
            StreamObserver<ClearCartEntriesResponse> responseObserver) {
        Cart cart = this.cartService.clearCart(request.getUserId());
        ClearCartEntriesResponse message = ClearCartEntriesResponse.newBuilder()
                .addAllEntries(this.toEntries(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void addCartEntry(AddCartEntryRequest request,
            StreamObserver<AddCartEntryResponse> responseObserver) {
        Change change = request.getChange();
        Cart cart = this.cartService.addEntry(
                change.getUserId(), change.getItem().getProductId(), change.getItem().getQuantity());
        AddCartEntryResponse message = AddCartEntryResponse.newBuilder()
                .addAllEntries(this.toEntries(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCartEntry(DeleteCartEntryRequest request,
            StreamObserver<DeleteCartEntryResponse> responseObserver) {
        Change change = request.getChange();
        Cart cart = this.cartService.removeEntry(
                change.getUserId(), change.getItem().getProductId(), change.getItem().getQuantity());
        DeleteCartEntryResponse message = DeleteCartEntryResponse.newBuilder()
                .addAllEntries(this.toEntries(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void clearCartEntry(ClearCartEntryRequest request,
            StreamObserver<ClearCartEntryResponse> responseObserver) {
        Change change = request.getChange();
        Cart cart = this.cartService.clearEntry(change.getUserId(), change.getItem().getProductId());
        ClearCartEntryResponse message = ClearCartEntryResponse.newBuilder()
                .addAllEntries(this.toEntries(cart))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    private List<Entry> toEntries(Cart cart) {
        return cart.getEntries()
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
    }

    private Entry toEntry(com.stobo.server.cart.domain.Entry entry) {
        return Entry.newBuilder()
                .setItem(Item.from(entry.getItem()))
                .setAddedAt(Timestamp.fromInstant(entry.getAddedAt()))
                .build();
    }
}
