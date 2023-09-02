package zerobase.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadDiaries {
    private int id;
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;
}
