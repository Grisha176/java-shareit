package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBrokerId(Long userId);

    List<Booking> findAllByBrokerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBrokerIdAndEndTimeIsBefore(Long userId, LocalDateTime now);

    List<Booking> findAllByBrokerIdAndStartTimeIsAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByBrokerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsBefore(Long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndTimeIsBefore(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartTimeIsAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status);

    Booking findByItemAndBroker(Item item, User user);

    boolean existsByItemAndStatus(Item item, BookingStatus status);

    boolean existsByItemAndStartTimeIsBeforeAndEndTimeIsAfter(Item item, LocalDateTime time, LocalDateTime time2);

    @Query("SELECT b FROM Booking b WHERE b.item = :item AND b.endTime < CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.endTime DESC")
    Optional<Booking> findLastFinishedBookingByItem(@Param("item") Item item);

    // Следующее будущее бронирование
    @Query("SELECT b FROM Booking b WHERE b.item = :item AND b.startTime > CURRENT_TIMESTAMP AND b.status = 'APPROVED' ORDER BY b.startTime ASC")
    Optional<Booking> findNextBookingByItem(@Param("item") Item item);


}
