package zerobase.weather.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(DateWeatherRepository.class)
class DiaryRepositoryTest {
    @MockBean
    DiaryRepository diaryRepository;

    @Test
    @DisplayName("날짜에 따른 모든 일기검색")
    void findAllByDate() {
        //given
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(Diary.builder()
                .date(testDate)
                .text("Hi")
                .weather("Sunny")
                .icon("sun")
                .temperature(28)
                .build());
        diaryList.add(Diary.builder()
                .date(testDate)
                .text("Hello")
                .weather("Cloudy")
                .icon("cloud")
                .temperature(27)
                .build());
        assertEquals(diaryList.get(0).getWeather(), "Sunny");
        given(diaryRepository.save(any())).willReturn(diaryList);

        //when
        given(diaryRepository.findAllByDate(testDate)).willReturn(diaryList);

        //then
        List<Diary> resultList = diaryRepository.findAllByDate(testDate);
        Assertions.assertTrue(resultList.size() > 0);
        assertEquals(resultList.size(), 2);
    }

    @Test
    @DisplayName("일기 기간 검색")
    void findAllByDateBetween() {
        //given
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(Diary.builder()
                .date(LocalDate.of(2023, 8, 1))
                .text("Hi")
                .weather("Sunny")
                .icon("sun")
                .temperature(28)
                .build());
        diaryList.add(Diary.builder()
                .date(LocalDate.of(2023, 8, 3))
                .text("Hello")
                .weather("Cloudy")
                .icon("cloud")
                .temperature(27)
                .build());
        //when
        List<Diary> diary = diaryRepository.findAllByDateBetween(any(), any()).stream().collect(Collectors.toList());
        given(diaryRepository.findAllByDateBetween(any(), any())).willReturn(diary);

        //then
        assertEquals(diaryRepository.findAllByDateBetween(any(), any()), diary);
    }

    @Test
    @DisplayName("검색날짜의 최상단 일기 요청")
    void getFirstByDate() {
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

        //when
        given(diaryRepository.getFirstByDate(any())).willReturn(diary.get(0));

        //then
        Diary resultDiary = diaryRepository.getFirstByDate(testDate);
        assertEquals(resultDiary, diary.get(0));
    }

    @Test
    @DisplayName("모든 일기 삭제")
    void deleteAllByDate() {
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