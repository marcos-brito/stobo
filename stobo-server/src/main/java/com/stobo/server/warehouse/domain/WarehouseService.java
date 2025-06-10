package com.stobo.server.warehouse.domain;

import com.stobo.server.common.exception.NotFoundException;
import com.stobo.server.common.id.OpaqueId;
import com.stobo.server.warehouse.exception.ItemUnavailable;
import com.stobo.server.warehouse.exception.ReasonMissing;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseService {
    private static final String NO_SUPPLY_REASON = "estoque zerado";
    private final ItemRepository ItemRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final AdditionRepository additionRepository;
    private final BookingRepository bookingRepository;

    WarehouseService(ItemRepository itemRepository,
            StatusChangeRepository statusChangeRepository,
            AdditionRepository additionRepository,
            BookingRepository bookingRepository) {
        this.ItemRepository = itemRepository;
        this.statusChangeRepository = statusChangeRepository;
        this.additionRepository = additionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking createBooking(Map<String, Integer> entries) {
        List<BookingItem> bookingItems = new ArrayList<BookingItem>();

        for (Map.Entry<String, Integer> entry : entries.entrySet()) {
            Item item = this.findItemById(entry.getKey());

            if (!item.canSupply(entry.getValue()))
                throw new ItemUnavailable("");

            item.deleteSupply(entry.getValue());
            item = this.ItemRepository.save(item);

            BookingItem bookingItem = BookingItem.builder().item(item).quantity(entry.getValue()).build();

            bookingItems.add(bookingItem);
        }

        Booking booking = Booking.builder().items(bookingItems).createdAt(Instant.now()).build();

        return this.bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(String bookingId) {
        Booking booking = this.findBookingById(bookingId);

        for (BookingItem bookingItem : booking.getItems()) {
            Item item = bookingItem.getItem();

            if (!item.hasSupply()) {
                this.inactivateItem(OpaqueId.encode(item.getId()), NO_SUPPLY_REASON);
                this.ItemRepository.save(item);
            }
        }

        this.bookingRepository.delete(booking);
        return booking;
    }

    @Transactional
    public Booking cancelBooking(String bookingId) {
        Booking booking = this.findBookingById(bookingId);

        for (BookingItem bookingItem : booking.getItems()) {
            Item item = bookingItem.getItem();

            item.addSupply(bookingItem.getQuantity());
            this.ItemRepository.save(item);
        }

        this.bookingRepository.delete(booking);
        return booking;
    }

    @Transactional
    public Item addItem(String productId, int quantity) {
        Item item = this.findItemById(productId);
        Addition addition = Addition.builder()
                .item(item)
                .quantity(quantity)
                .createdAt(Instant.now())
                .build();

        item.addSupply(addition.getQuantity());
        this.additionRepository.save(addition);
        return this.ItemRepository.save(item);
    }

    public Item activateItem(String productId, String reason) {
        return this.changeItemStatus(productId, ItemStatus.ACTIVE, reason);
    }

    public Item inactivateItem(String productId, String reason) {
        return this.changeItemStatus(productId, ItemStatus.INACTIVE, reason);
    }

    @Transactional
    public Item changeItemStatus(String productId, ItemStatus status,
            String reason) {
        if (reason.isBlank())
            throw new ReasonMissing();

        Item item = this.findItemById(productId);
        StatusChange change = StatusChange.builder()
                .kind(status)
                .reason(reason)
                .changedAt(Instant.now())
                .build();

        item.setStatus(status);
        this.statusChangeRepository.save(change);
        return this.ItemRepository.save(item);
    }

    public Item findItemById(String productId) {
        return OpaqueId.decodeLong(productId)
                .flatMap(this.ItemRepository::findById)
                .orElseThrow(() -> new NotFoundException("item", productId));
    }

    public Booking findBookingById(String bookingId) {
        return OpaqueId.decodeLong(bookingId)
                .flatMap(this.bookingRepository::findById)
                .orElseThrow(() -> new NotFoundException("booking", bookingId));
    }
}
