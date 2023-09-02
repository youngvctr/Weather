package zerobase.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.dto.CreateDiary;
import zerobase.weather.dto.DiaryDto;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("일기 작성")
    void createDiary() throws Exception {
        //given
        DiaryDto diary = diaryService.createDiary(any(), anyString()).builder()
                .id(1)
                .date(LocalDate.now())
                .text("Hi")
                .build();

        given(diaryService.createDiary(any(), anyString())).willReturn(diary);

        //when
        //then
        mockMvc.perform(post("/create/diary")
                        .content(objectMapper.writeValueAsString(
                                new CreateDiary.CreateDiaryRequest(diary.getText())
                        ))
                        .param("date", String.valueOf(LocalDate.now()))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(String.valueOf(LocalDate.now())))
                .andDo(print());
    }

    @Test
    @DisplayName("일기 조회")
    void readDiary() throws Exception {
        //given
        List<DiaryDto> diaryDtoList = Arrays.asList(
                DiaryDto.builder()
                        .text("Hello")
                        .date(LocalDate.now())
                        .id(1)
                        .icon("Sunny")
                        .temperature(25)
                        .weather("Sunny")
                        .build(),
                DiaryDto.builder()
                        .text("World")
                        .date(LocalDate.now())
                        .id(2)
                        .icon("")
                        .temperature(0d)
                        .build(),
                DiaryDto.builder()
                        .text("!!!!!")
                        .date(LocalDate.now())
                        .id(3)
                        .icon("")
                        .temperature(0d)
                        .build()
        );

        given(diaryService.readDiary(any())).willReturn(diaryDtoList);

        //when
        //then
        mockMvc.perform(get("/read/diary?date=2023-09-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(String.valueOf(LocalDate.now())))
                .andExpect(jsonPath("$[0].text").value("Hello"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].icon").value("Sunny"))
                .andExpect(jsonPath("$[0].weather").value("Sunny"))
                .andExpect(jsonPath("$[0].temperature").value(String.valueOf(0.0)));
    }

    @Test
    @DisplayName("일기 기간 조회")
    void readDiaries() throws Exception {
        //given
        List<DiaryDto> diaryDtoList = Arrays.asList(
                DiaryDto.builder()
                        .text("Hello")
                        .date(LocalDate.of(2023, 8, 1))
                        .id(1)
                        .icon("Cloudy")
                        .temperature(0d)
                        .weather("Cloudy")
                        .build(),
                DiaryDto.builder()
                        .text("World")
                        .date(LocalDate.of(2023, 8, 2))
                        .id(2)
                        .icon("")
                        .temperature(0d)
                        .weather("")
                        .build(),
                DiaryDto.builder()
                        .text("Smile")
                        .date(LocalDate.of(2023, 8, 3))
                        .id(3)
                        .icon("")
                        .temperature(0d)
                        .weather("")
                        .build()
        );
        given(diaryService.readDiaries(any(), any())).willReturn(diaryDtoList);

        //when
        //then
        mockMvc.perform(get("/read/diaries?startDate=2023-08-01&endDate=2023-08-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(String.valueOf(LocalDate.of(2023, 8, 1))))
                .andExpect(jsonPath("$[0].text").value("Hello"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].icon").value("Cloudy"))
                .andExpect(jsonPath("$[0].weather").value("Cloudy"))
                .andExpect(jsonPath("$[0].temperature").value(0d));
    }

    @Test
    @DisplayName("일기 내용 수정")
    void updateDiary() throws Exception {
        //given
        List<DiaryDto> diaryDtoList = Arrays.asList(
                DiaryDto.builder()
                        .text("Hello")
                        .date(LocalDate.of(2023, 8, 1))
                        .id(1)
                        .icon("Sunny")
                        .temperature(25)
                        .weather("Sunny")
                        .build(),
                DiaryDto.builder()
                        .text("World")
                        .date(LocalDate.of(2023, 8, 1))
                        .id(2)
                        .icon("")
                        .temperature(0d)
                        .build(),
                DiaryDto.builder()
                        .text("!!!!!")
                        .date(LocalDate.of(2023, 8, 1))
                        .id(3)
                        .icon("")
                        .temperature(0d)
                        .build()
        );

        given(diaryService.readDiary(any())).willReturn(diaryDtoList);
        given(diaryService.updateDiary(any(), anyString()))
                .willReturn(DiaryDto.builder()
                        .text("world!")
                        .date(LocalDate.of(2023, 8, 1))
                        .id(1)
                        .icon("Sunny")
                        .temperature(25)
                        .weather("Sunny")
                        .build());

        //when
        DiaryDto diaryDto = diaryService.updateDiary(LocalDate.of(2023, 8, 1), "world!");
        //then
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("text", diaryDto.getText());

        mockMvc.perform(put("/update/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempMap))
                        .param("date", String.valueOf(diaryDto.getDate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(String.valueOf(LocalDate.of(2023, 8, 1))))
                .andExpect(jsonPath("$.text").value("world!"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.icon").value("Sunny"))
                .andExpect(jsonPath("$.weather").value("Sunny"))
                .andExpect(jsonPath("$.temperature").value(25))
                .andDo(print());
    }

    @Test
    @DisplayName("해당 날짜 일기 모두 삭제")
    void deleteDiary() throws Exception {
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        //given
        List<DiaryDto> diaryDtoList = Arrays.asList(
                DiaryDto.builder()
                        .text("Hello")
                        .date(testDate)
                        .id(1)
                        .icon("Sunny")
                        .temperature(25)
                        .weather("Sunny")
                        .build(),
                DiaryDto.builder()
                        .text("World")
                        .date(testDate)
                        .id(2)
                        .icon("")
                        .temperature(0d)
                        .build(),
                DiaryDto.builder()
                        .text("!!!!!")
                        .date(testDate)
                        .id(3)
                        .icon("")
                        .temperature(0d)
                        .build()
        );

        given(diaryService.readDiary(any())).willReturn(diaryDtoList);
        //when
        //then
        mockMvc.perform(delete("/delete/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", String.valueOf(testDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("삭제 완료"))
                .andDo(print());
    }
}