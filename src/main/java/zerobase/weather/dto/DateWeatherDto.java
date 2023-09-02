package zerobase.weather.dto;

import lombok.*;
import zerobase.weather.domain.DateWeather;

import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateWeatherDto {
    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

    public static DateWeatherDto fromDateWeatherEntity(DateWeather dateWeather){
        return DateWeatherDto.builder()
                .date(dateWeather.getDate())
                .weather(dateWeather.getWeather())
                .temperature(dateWeather.getTemperature())
                .icon(dateWeather.getIcon())
                .build();
    }
}
