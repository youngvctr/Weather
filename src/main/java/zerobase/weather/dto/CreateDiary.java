package zerobase.weather.dto;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Id;
import java.time.LocalDate;

public class CreateDiary {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class CreateDiaryRequest{
        @NotNull
        private String text;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CreateDiaryResponse{
        @Id
        @NotNull
        private LocalDate date;
        private String weather;
        private String icon;
        private double temperature;

        public static CreateDiaryResponse from (DiaryDto diaryDto){
            return CreateDiaryResponse.builder()
                    .date(diaryDto.getDate())
                    .weather(diaryDto.getWeather())
                    .icon(diaryDto.getIcon())
                    .temperature(diaryDto.getTemperature())
                    .build();
        }
    }
}
