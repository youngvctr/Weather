package zerobase.weather.dto;

import com.sun.istack.NotNull;
import lombok.*;

import java.time.LocalDate;

public class UpdateDiary {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class UpdateDiaryRequest {
        @NotNull
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateDiaryResponse {
        private int id;
        private String weather;
        private String icon;
        private double temperature;
        private String text;
        private LocalDate date;

        public static UpdateDiaryResponse from(DiaryDto diaryDto) {
            return UpdateDiaryResponse.builder()
                    .id(diaryDto.getId())
                    .weather(diaryDto.getWeather())
                    .icon(diaryDto.getIcon())
                    .temperature(diaryDto.getTemperature())
                    .text(diaryDto.getText())
                    .date(diaryDto.getDate())
                    .build();
        }
    }
}
