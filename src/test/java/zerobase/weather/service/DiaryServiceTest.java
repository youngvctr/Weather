package zerobase.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import zerobase.weather.controller.DiaryController;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.DiaryDto;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@WebMvcTest(DiaryController.class)
class DiaryServiceTest {

    @MockBean
    private DiaryService diaryService;

    @MockBean
    private DateWeatherRepository dateWeatherRepository;

    @MockBean
    private DiaryRepository diaryRepository;

    @Test
    @DisplayName("openweathermap api 테스트")
    void saveWeatherDate() throws Exception {
        //given
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(new ArrayList<DateWeather>());

        //when
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="
                + "4d1e8e61007181ecd247ed156e5a8a99"
                + "&units=metric";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int resCode = conn.getResponseCode();
        assertEquals(resCode, 200);

        String inputLine;
        StringBuffer response = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }

        //then
        assertEquals(response.toString().contains("main"), true);
        assertEquals(response.toString().contains("temp"), true);
        assertEquals(response.toString().contains("icon"), true);
        assertEquals(response.toString().contains("weather"), true);
    }

    @Test
    @DisplayName("일기 생성 테스트")
    void createDiary() {
        //given
        DiaryDto diary = diaryService.createDiary(any(), anyString()).builder()
                .text("hi")
                .build();
        given(diaryService.createDiary(any(), anyString()))
                .willReturn(diary);
        //when
        //then
        assertEquals(diaryService.createDiary(diary.getDate(), diary.getText()), diary);
    }

    @Test
    @DisplayName("일기 조회 테스트")
    void readDiary() {
        //given
        List<DiaryDto> diary = diaryService.readDiary(any()).stream().collect(Collectors.toList());
        given(diaryService.readDiary(any())).willReturn(diary);
        //when
        //then
        assertEquals(diaryService.readDiary(any()), diary);
    }

    @Test
    @DisplayName("일기 기간 조회 테스트")
    void readDiaries() {
        //given
        List<DiaryDto> diary = diaryService.readDiaries(any(), any()).stream().collect(Collectors.toList());
        given(diaryService.readDiaries(any(), any())).willReturn(diary);
        //when
        //then
        assertEquals(diaryService.readDiaries(any(), any()), diary);
    }

    @Test
    @DisplayName("일기 수정 테스트")
    void updateDiary() {
        //given
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        Diary diary = Diary.builder()
                .text("hello!")
                .date(testDate)
                .id(1)
                .icon("Sunny")
                .temperature(25)
                .weather("Sunny")
                .build();

        given(diaryRepository.getFirstByDate(any())).willReturn(diary);

        //when
        Diary nowDiary = diaryRepository.getFirstByDate(testDate);
        nowDiary.setText("1235");
        diaryRepository.save(nowDiary);
        diaryService.updateDiary(nowDiary.getDate(), nowDiary.getText());

        //then
        Diary searchDiary = diaryRepository.getFirstByDate(testDate);
        assertEquals(searchDiary.getText(), "1235");
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiary() {
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        //given
        List<Diary> diary = new ArrayList<>();
        diary.add(Diary.builder()
                .text("hello!")
                .date(testDate)
                .id(1)
                .icon("Sunny")
                .temperature(25)
                .weather("Sunny")
                .build());
        diary.add(Diary.builder()
                .text("hello!")
                .date(testDate)
                .id(1)
                .icon("Sunny")
                .temperature(25)
                .weather("Sunny")
                .build());

        given(diaryRepository.findAllByDate(any())).willReturn(diary);
        assertEquals(diaryRepository.findAllByDate(testDate).size(), 2);

        //when
        given(diaryRepository.save(diary.get(1))).willReturn(diary.get(1));
        diaryRepository.deleteAllByDate(testDate);

        //then
        assertEquals(diaryRepository.findAll().size() == 0, true);
    }
}