package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.CreateDiary;
import zerobase.weather.dto.DeleteDiary;
import zerobase.weather.dto.UpdateDiary;
import zerobase.weather.exception.DiaryException;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController // HTTP status code 를 지정해서 내려줄 수 있음.
public class DiaryController {
    private final DiaryService diaryService;
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @ApiOperation(value = "일기 생성", notes =
                    "- 날짜 형식(yyyy-MM-dd)으로 조회\n" +
                    "- text parameter 로 일기 글을 입력\n" +
                    "- 외부 API 에서 받아온 날씨 데이터와 함께 DB에 저장.") // api 설명
    @PostMapping("/create/diary")
    public CreateDiary.CreateDiaryResponse createDiary(
            @RequestParam @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-02")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody CreateDiary.CreateDiaryRequest request
    ) {
        return CreateDiary.CreateDiaryResponse.from(diaryService.createDiary(
                date,
                request.getText()));
    }

    @ApiOperation(value = "일기 조회",
            notes = "" +
            "- 날짜 형식(yyyy-MM-dd)으로 조회\n" +
            "- 해당 날짜의 일기를 List 형태로 반환") // api 설명
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-01") LocalDate date
    ) throws DiaryException {
        return diaryService.readDiary(date)
                .stream().map(diaryDto ->
                        Diary.builder()
                                .id(diaryDto.getId())
                                .weather(diaryDto.getWeather())
                                .icon(diaryDto.getIcon())
                                .text(diaryDto.getText())
                                .date(diaryDto.getDate())
                                .build())
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "해당 기간의 일기 조회", notes =
                    "- 조회할 날짜 기간의 시작일/종료일 입력\n" +
                    "- 해당 기간의 일기를 List 형태로 반환.") // api 설명
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-01") LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-02") LocalDate endDate
    ) throws DiaryException {
        return diaryService.readDiaries(startDate, endDate)
                .stream().map(diaryDto ->
                        Diary.builder()
                                .id(diaryDto.getId())
                                .weather(diaryDto.getWeather())
                                .icon(diaryDto.getIcon())
                                .text(diaryDto.getText())
                                .date(diaryDto.getDate())
                                .build())
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "일기 글 수정", notes =
                    "- date parameter 로 수정할 날짜 입력.\n" +
                    "- text parameter 로 수정할 새 일기 글 입력\n" +
                    "- 해당 날짜의 첫번째 일기 글을 새로 받아온 일기글로 수정.") // api 설명
    @PutMapping("/update/diary")
    UpdateDiary.UpdateDiaryResponse updateDiary(
            @RequestParam @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-02")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody UpdateDiary.UpdateDiaryRequest request
    ) {
        return UpdateDiary.UpdateDiaryResponse.from(diaryService.updateDiary(date, request.getText()));
    }

    @ApiOperation(value = "해당 날짜의 일기 삭제", notes = "" +
            "- date parameter 로 삭제할 날짜 입력.\n" +
            "- 해당 날짜의 모든 일기를 삭제.")
    @DeleteMapping("/delete/diary")
    DeleteDiary.DeleteDiaryResponse deleteDiary(
            @RequestParam @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2023-09-02")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DeleteDiary.DeleteDiaryRequest request = new DeleteDiary.DeleteDiaryRequest(date);
        diaryService.deleteDiary(request.getDate());
        return DeleteDiary.DeleteDiaryResponse.from();
    }
}
