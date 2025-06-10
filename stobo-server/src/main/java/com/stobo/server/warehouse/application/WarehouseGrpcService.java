package com.stobo.server.warehouse.application;

import com.stobo.proto.warehouse.ActivateWarehouseItemRequest;
import com.stobo.proto.warehouse.ActivateWarehouseItemResponse;
import com.stobo.proto.warehouse.AddWarehouseItemRequest;
import com.stobo.proto.warehouse.AddWarehouseItemResponse;
import com.stobo.proto.warehouse.Addition;
import com.stobo.proto.warehouse.Booking;
import com.stobo.proto.warehouse.BookingItem;
import com.stobo.proto.warehouse.CancelWarehouseBookingRequest;
import com.stobo.proto.warehouse.CancelWarehouseBookingResponse;
import com.stobo.proto.warehouse.ConfirmWarehouseBookingRequest;
import com.stobo.proto.warehouse.ConfirmWarehouseBookingResponse;
import com.stobo.proto.warehouse.CreateWarehouseBookingRequest;
import com.stobo.proto.warehouse.CreateWarehouseBookingResponse;
import com.stobo.proto.warehouse.DeactivateWarehouseItemRequest;
import com.stobo.proto.warehouse.DeactivateWarehouseItemResponse;
import com.stobo.proto.warehouse.GetWarehouseItemRequest;
import com.stobo.proto.warehouse.GetWarehouseItemResponse;
import com.stobo.proto.warehouse.Item;
import com.stobo.proto.warehouse.ItemStatus;
import com.stobo.proto.warehouse.StatusChange;
import com.stobo.proto.warehouse.WarehouseServiceGrpc.WarehouseServiceImplBase;
import com.stobo.server.common.id.OpaqueId;
import com.stobo.server.warehouse.domain.WarehouseService;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.stream.Collectors;

class WarehouseGrpcService extends WarehouseServiceImplBase {
    private final WarehouseService warehouseService;

    WarehouseGrpcService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public void createWarehouseBooking(
            CreateWarehouseBookingRequest request,
            StreamObserver<CreateWarehouseBookingResponse> responseObserver) {
        Map<String, Integer> items = request.getItemsList().stream().collect(Collectors.toMap(
                item -> item.getProductId(), item -> item.getQuantity()));
        com.stobo.server.warehouse.domain.Booking booking = this.warehouseService.createBooking(items);
        CreateWarehouseBookingResponse message = CreateWarehouseBookingResponse.newBuilder()
                .setBooking(this.toBooking(booking))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void confirmWarehouseBooking(
            ConfirmWarehouseBookingRequest request,
            StreamObserver<ConfirmWarehouseBookingResponse> responseObserver) {
        com.stobo.server.warehouse.domain.Booking booking = this.warehouseService.confirmBooking(request.getId());
        ConfirmWarehouseBookingResponse message = ConfirmWarehouseBookingResponse.newBuilder()
                .setBooking(this.toBooking(booking))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void cancelWarehouseBooking(
            CancelWarehouseBookingRequest request,
            StreamObserver<CancelWarehouseBookingResponse> responseObserver) {
        com.stobo.server.warehouse.domain.Booking booking = this.warehouseService.cancelBooking(request.getId());
        CancelWarehouseBookingResponse message = CancelWarehouseBookingResponse.newBuilder()
                .setBooking(this.toBooking(booking))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void getWarehouseItem(GetWarehouseItemRequest request,
            StreamObserver<GetWarehouseItemResponse> responseObserver) {
        com.stobo.server.warehouse.domain.Item item = this.warehouseService.findItemById(request.getProductId());
        GetWarehouseItemResponse message = GetWarehouseItemResponse.newBuilder()
                .setItem(this.toItem(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void addWarehouseItem(AddWarehouseItemRequest request,
            StreamObserver<AddWarehouseItemResponse> responseObserver) {
        Addition addition = request.getAddition();
        com.stobo.server.warehouse.domain.Item item = this.warehouseService.addItem(
                addition.getProductId(), addition.getQuantity());
        AddWarehouseItemResponse message = AddWarehouseItemResponse.newBuilder()
                .setItem(this.toItem(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void activateWarehouseItem(
            ActivateWarehouseItemRequest request,
            StreamObserver<ActivateWarehouseItemResponse> responseObserver) {
        StatusChange change = request.getChange();
        com.stobo.server.warehouse.domain.Item item = this.warehouseService.activateItem(change.getProductId(),
                change.getReason());
        ActivateWarehouseItemResponse message = ActivateWarehouseItemResponse.newBuilder()
                .setItem(this.toItem(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void deactivateWarehouseItem(
            DeactivateWarehouseItemRequest request,
            StreamObserver<DeactivateWarehouseItemResponse> responseObserver) {
        StatusChange change = request.getChange();
        com.stobo.server.warehouse.domain.Item item = this.warehouseService.inactivateItem(change.getProductId(),
                change.getReason());
        DeactivateWarehouseItemResponse message = DeactivateWarehouseItemResponse.newBuilder()
                .setItem(this.toItem(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    private Booking toBooking(com.stobo.server.warehouse.domain.Booking booking) {
        return Booking.newBuilder()
                .setId(OpaqueId.encode(booking.getId()))
                .addAllItems(booking.getItems()
                        .stream()
                        .map(this::toBookingItem)
                        .collect(Collectors.toList()))
                .build();
    }

    private BookingItem toBookingItem(com.stobo.server.warehouse.domain.BookingItem item) {
        return BookingItem.newBuilder()
                .setProductId(OpaqueId.encode(item.getItem().getProductId()))
                .setQuantity(item.getQuantity())
                .build();
    }

    private Item toItem(com.stobo.server.warehouse.domain.Item item) {
        return Item.newBuilder()
                .setProductId(OpaqueId.encode(item.getProductId()))
                .setQuantity(item.getQuantity())
                .setStatus(this.toStatus(item.getStatus()))
                .build();
    }

    private ItemStatus toStatus(com.stobo.server.warehouse.domain.ItemStatus status) {
        return switch (status) {
            case ACTIVE -> ItemStatus.ITEM_STATUS_ACTIVE;
            case INACTIVE -> ItemStatus.ITEM_STATUS_INACTIVE;
        };
    }
}
