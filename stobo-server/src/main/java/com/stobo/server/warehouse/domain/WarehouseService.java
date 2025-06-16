package com.stobo.server.warehouse.domain;

import com.stobo.server.common.exception.NotFoundException;
import com.stobo.server.common.proto.Id;
import com.stobo.server.common.domain.Item;
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
    private final EntryRepository ItemRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final AdditionRepository additionRepository;
    private final BookingRepository bookingRepository;

    WarehouseService(EntryRepository itemRepository,
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
        List<Item> bookingItems = new ArrayList<Item>();

        for (Map.Entry<String, Integer> pair : entries.entrySet()) {
            Entry entry = this.findEntryById(pair.getKey());

            if (!entry.canSupply(pair.getValue()))
                throw new ItemUnavailable("");

            entry.deleteSupply(pair.getValue());
            entry = this.ItemRepository.save(entry);

            Item item = new Item(entry.getItem().getProductId(), pair.getValue()) ;

            bookingItems.add(item);
        }

        Booking booking = Booking.builder().items(bookingItems).createdAt(Instant.now()).build();

        return this.bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(String bookingId) {
        Booking booking = this.findBookingById(bookingId);

        for (Item item : booking.getItems()) {
            Entry entry = this.findEntryById(item.getProductId());

            if (!entry.hasSupply()) {
                this.inactivateEntry(Id.encode(entry.getId()), NO_SUPPLY_REASON);
                this.ItemRepository.save(entry);
            }
        }

        this.bookingRepository.delete(booking);
        return booking;
    }

    @Transactional
    public Booking cancelBooking(String bookingId) {
        Booking booking = this.findBookingById(bookingId);

        for (Item item : booking.getItems()) {
            Entry entry = this.findEntryById(item.getProductId());

            entry.addSupply(item.getQuantity());
            this.ItemRepository.save(entry);
        }

        this.bookingRepository.delete(booking);
        return booking;
    }

    @Transactional
    public Entry addEntry(String productId, int quantity) {
        Entry entry = this.findEntryById(productId);
        Addition addition = Addition.builder()
                .entry(entry)
                .quantity(quantity)
                .createdAt(Instant.now())
                .build();

        entry.addSupply(addition.getQuantity());
        this.additionRepository.save(addition);
        return this.ItemRepository.save(entry);
    }

    public Entry activateEntry(String productId, String reason) {
        return this.changeEntryStatus(productId, Status.ACTIVE, reason);
    }

    public Entry inactivateEntry(String productId, String reason) {
        return this.changeEntryStatus(productId, Status.INACTIVE, reason);
    }

    @Transactional
    public Entry changeEntryStatus(String productId, Status status,
            String reason) {
        if (reason.isBlank())
            throw new ReasonMissing();

        Entry item = this.findEntryById(productId);
        StatusChange change = StatusChange.builder()
                .kind(status)
                .reason(reason)
                .changedAt(Instant.now())
                .build();

        item.setStatus(status);
        this.statusChangeRepository.save(change);
        return this.ItemRepository.save(item);
    }

    public Entry findEntryById(String productId) {
        return Id.decodeLong(productId)
                .flatMap(this.ItemRepository::findById)
                .orElseThrow(() -> new NotFoundException("item", productId));
    }

    private Entry findEntryById(long productId) {
        return findEntryById(Id.encode(productId));
    }

    public Booking findBookingById(String bookingId) {
        return Id.decodeLong(bookingId)
                .flatMap(this.bookingRepository::findById)
                .orElseThrow(() -> new NotFoundException("booking", bookingId));
    }
}
