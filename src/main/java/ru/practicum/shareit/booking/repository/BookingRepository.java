package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllByBookerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndTimeIsBefore(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartTimeIsAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndTimeIsBefore(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartTimeIsAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item = :item AND b.startTime < CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.startTime DESC LIMIT 1")
    Optional<Booking> findLastFinishedBookingByItem(@Param("item") Item item);

    // Следующее будущее бронирование
    @Query("SELECT b FROM Booking b WHERE b.item = :item AND b.startTime > CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.startTime ASC")
    Optional<Booking> findNextBookingByItem(@Param("item") Item item);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.item_id = b.item_id " +
            "WHERE b.booker_id = ?1 AND i.item_id = ?2 AND b.status = 'APPROVED' AND b.end_time < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);


}
