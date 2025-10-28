package entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Record(String username,
        BigDecimal price,
        Integer bookingId,
        LocalDateTime time) { }
