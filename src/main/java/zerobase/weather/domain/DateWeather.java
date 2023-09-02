package zerobase.weather.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "date_weather")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DateWeather {
    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;
}
