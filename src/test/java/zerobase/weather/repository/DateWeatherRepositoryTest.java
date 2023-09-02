package zerobase.weather.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.dto.DateWeatherDto;
import zerobase.weather.dto.DiaryDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@WebMvcTest(DateWeatherRepository.class)
class DateWeatherRepositoryTest {

    @MockBean
    DateWeatherRepository dateWeatherRepository;

    @Test
    @DisplayName("날짜에 따른 모든 날씨데이터 검색")
    void findAllByDate() {
        //given
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        List<DateWeather> dateWeatherList = new ArrayList<>();
        dateWeatherList.add(DateWeather.builder()
                .date(testDate)
                .weather("Sunny")
                .icon("sun")
                .temperature(28)
                .build());
        dateWeatherList.add(DateWeather.builder()
                .date(testDate)
                .weather("Cloudy")
                .icon("cloud")
                .temperature(28)
                .build());
        assertEquals(dateWeatherList.get(0).getWeather(), "Sunny");
        given(dateWeatherRepository.save(any())).willReturn(dateWeatherList);

        //when
        given(dateWeatherRepository.findAllByDate(testDate)).willReturn(dateWeatherList);

        //then
        List<DateWeather> resultList = dateWeatherRepository.findAllByDate(testDate);
        Assertions.assertTrue(dateWeatherList.size() > 0);
        assertEquals(resultList.size(), 2);
    }
}