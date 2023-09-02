package zerobase.weather.dto;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Id;
import java.time.LocalDate;

public class DeleteDiary {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class DeleteDiaryRequest {
        @Id
        @NotNull
        private LocalDate date;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class DeleteDiaryResponse {
        private String result;

        public static DeleteDiaryResponse from() {
            return DeleteDiaryResponse.builder()
                    .result("삭제 완료")
                    .build();
        }
    }
}
