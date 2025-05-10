package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING("новое бронирование, ожидает одобрения"),
    APPROVED("бронирование подтверждено"),
    REJECTED("бронирование отменено владельцем"),
    CANCELED("бронирование отменено создателем");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}