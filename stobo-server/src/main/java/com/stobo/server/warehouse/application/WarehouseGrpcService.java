package com.stobo.server.warehouse.application;

import java.util.Map;
import java.util.stream.Collectors;

import com.stobo.proto.warehouse.ActivateWarehouseEntryRequest;
import com.stobo.proto.warehouse.ActivateWarehouseEntryResponse;
import com.stobo.proto.warehouse.AddWarehouseEntryRequest;
import com.stobo.proto.warehouse.Entry;
import com.stobo.proto.warehouse.GetWarehouseEntryRequest;
import com.stobo.proto.warehouse.GetWarehouseEntryResponse;
import com.stobo.proto.warehouse.Status;
import com.stobo.proto.warehouse.AddWarehouseEntryResponse;
import com.stobo.proto.warehouse.Booking;
import com.stobo.proto.warehouse.CancelWarehouseBookingRequest;
import com.stobo.proto.warehouse.CancelWarehouseBookingResponse;
import com.stobo.proto.warehouse.ConfirmWarehouseBookingRequest;
import com.stobo.proto.warehouse.ConfirmWarehouseBookingResponse;
import com.stobo.proto.warehouse.CreateWarehouseBookingRequest;
import com.stobo.proto.warehouse.CreateWarehouseBookingResponse;
import com.stobo.proto.warehouse.DeactivateWarehouseEntryRequest;
import com.stobo.proto.warehouse.DeactivateWarehouseEntryResponse;
import com.stobo.proto.warehouse.StatusChange;
import com.stobo.proto.warehouse.WarehouseServiceGrpc.WarehouseServiceImplBase;
import com.stobo.server.common.proto.Item;
import com.stobo.server.warehouse.domain.WarehouseService;
import com.stobo.server.common.proto.Id;

import io.grpc.stub.StreamObserver;

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
    public void getWarehouseEntry(GetWarehouseEntryRequest request,
            StreamObserver<GetWarehouseEntryResponse> responseObserver) {
        com.stobo.server.warehouse.domain.Entry item = this.warehouseService.findEntryById(request.getProductId());
        GetWarehouseEntryResponse message = GetWarehouseEntryResponse.newBuilder()
                .setEntry(this.toEntry(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void addWarehouseEntry(AddWarehouseEntryRequest request,
            StreamObserver<AddWarehouseEntryResponse> responseObserver) {
        Addition addition = request.getAddition();
        com.stobo.server.warehouse.domain.Entry item = this.warehouseService.addEntry(
                addition.getProductId(), addition.getQuantity());
        AddWarehouseEntryResponse message = AddWarehouseEntryResponse.newBuilder()
                .setEntry(this.toEntry(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void activateWarehouseEntry(ActivateWarehouseEntryRequest request,
            StreamObserver<ActivateWarehouseEntryResponse> responseObserver) {
        StatusChange change = request.getChange();
        com.stobo.server.warehouse.domain.Entry item = this.warehouseService.activateEntry(change.getProductId(),
                change.getReason());
        ActivateWarehouseEntryResponse message = ActivateWarehouseEntryResponse.newBuilder()
                .setEntry(this.toEntry(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void deactivateWarehouseEntry(DeactivateWarehouseEntryRequest request,
            StreamObserver<DeactivateWarehouseEntryResponse> responseObserver) {
        StatusChange change = request.getChange();
        com.stobo.server.warehouse.domain.Entry item = this.warehouseService.inactivateEntry(change.getProductId(),
                change.getReason());
        DeactivateWarehouseEntryResponse message = DeactivateWarehouseEntryResponse.newBuilder()
                .setEntry(this.toEntry(item))
                .build();

        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    private Booking toBooking(com.stobo.server.warehouse.domain.Booking booking) {
        return Booking.newBuilder()
                .setId(Id.encode(booking.getId()))
                .addAllItems(booking.getItems()
                        .stream()
                        .map(Item::from)
                        .collect(Collectors.toList()))
                .build();
    }

    private Entry toEntry(com.stobo.server.warehouse.domain.Entry entry) {
        return Entry.newBuilder()
                .setItem(Item.from(entry.getItem()))
                .setStatus(this.toStatus(entry.getStatus()))
                .build();
    }

    private Status toStatus(com.stobo.server.warehouse.domain.Status status) {
        return switch (status) {
            case ACTIVE -> Status.STATUS_ACTIVE;
            case INACTIVE -> Status.STATUS_INACTIVE;
        };
    }
}
