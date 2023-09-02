package zerobase.weather.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버에서 오류가 발생했습니다."),
    NULL_LIST_ERROR("조회 내역이 없습니다."),
    TOO_FAR_FROM_END_DATE("조회 날짜는 1년을 초과할 수 없습니다."),
    INVALID_DATE("날짜 형식에 오류가 있습니다."),
    NULL_DATA_ERROR("해당 날짜에 기록이 없습니다."),
    INVALID_REQUEST("잘못된 요청입니다.");

    private final String description;
}
