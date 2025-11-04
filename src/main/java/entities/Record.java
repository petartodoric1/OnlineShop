package entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Zapisuje plaćene narudžbe sa predanim parametrima
 * @param username
 * @param price
 * @param bookingId
 * @param time
 */
public record Record(String username,
        BigDecimal price,
        Integer bookingId,
        LocalDateTime time) { }
