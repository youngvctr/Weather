package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional; // readOnly 사용가능.
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.DateWeatherDto;
import zerobase.weather.dto.DiaryDto;
import zerobase.weather.exception.DiaryException;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;
import zerobase.weather.type.ErrorCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static zerobase.weather.type.ErrorCode.TOO_FAR_FROM_END_DATE;

/**
 * MVC | Client ->DTO-> Controller ->DTO-> Service ->DTO-> Repository ->Entity-> DB
 */
@Service
@Transactional
public class DiaryService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    @Value("${openweathermap.key}")
    private String apiKey;

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        DateWeather dateWeather = getWeatherFromApi();
        DateWeatherDto.fromDateWeatherEntity(
                dateWeatherRepository.save(dateWeather.builder()
                        .date(dateWeather.getDate())
                        .weather(dateWeather.getWeather())
                        .icon(dateWeather.getIcon())
                        .temperature(dateWeather.getTemperature())
                        .build()
                ));
        logger.info("schedule is successfully finished");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE) //readOnly = false,
    public DiaryDto createDiary(LocalDate date, String text) {
        //open weather map 에서 날씨 데이터 가져오기 or DB에서 가져오기
        logger.info("started to create diary");
        DateWeather dateWeather = getDateWeather(date);
        logger.info("end to create diary");
        return DiaryDto.fromEntity(
                diaryRepository.save(Diary.builder()
                        .text(text)
                        .weather(dateWeather.getWeather())
                        .icon(dateWeather.getIcon())
                        .date(date)
                        .temperature(dateWeather.getTemperature())
                        .build()
                ));
    }

    public DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        if (dateWeatherListFromDB.size() == 0 &&  date == LocalDate.now()) {
            // 새로 api에서 날씨 정보를 가져와야 한다.
            DateWeather dateWeather = getWeatherFromApi();
            DateWeatherDto.fromDateWeatherEntity(
                    dateWeatherRepository.save(dateWeather.builder()
                            .date(date)
                            .weather(dateWeather.getWeather())
                            .icon(dateWeather.getIcon())
                            .temperature(dateWeather.getTemperature())
                            .build()
                    ));
            return dateWeather;
        } else if (dateWeatherListFromDB.size() == 0 && date != LocalDate.now()){
            return new DateWeather(date,  "", "", 0);
        }

        return dateWeatherListFromDB.get(0);
    }

    public DateWeather getWeatherFromApi() {
        //open weather map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));

        return dateWeather;
    }

    @Transactional(readOnly = true)
    public List<DiaryDto> readDiary(LocalDate date) throws DiaryException {
        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new DiaryException(ErrorCode.INVALID_DATE);
        }
        List<Diary> result = diaryRepository.findAllByDate(date);
        if (result.isEmpty())
            throw new DiaryException(ErrorCode.NULL_LIST_ERROR); //throw new DiaryException(ErrorCode.NULL_LIST_ERROR);
        return result.stream().map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    public List<DiaryDto> readDiaries(LocalDate startDate, LocalDate endDate) throws DiaryException {
        if (startDate.isAfter(endDate)) throw new DiaryException(ErrorCode.INVALID_REQUEST);
        if (endDate.getYear() - startDate.getYear() >= 1 && endDate.getDayOfYear() - startDate.getDayOfYear() > 0) {
            throw new DiaryException(TOO_FAR_FROM_END_DATE);
        }
        List<Diary> result = diaryRepository.findAllByDateBetween(startDate, endDate);
        if (result.isEmpty()) {
            throw new DiaryException(ErrorCode.NULL_LIST_ERROR);
        }
        return result.stream().map(DiaryDto::fromEntity).collect(Collectors.toList());
    }

    public DiaryDto updateDiary(LocalDate date, String text) {  //업데이트
        List<Diary> result = diaryRepository.findAllByDate(date);
        if (result.isEmpty()) {
            throw new DiaryException(ErrorCode.NULL_DATA_ERROR);
        }

        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
//        diaryRepository.save(nowDiary);
        return DiaryDto.fromEntity(
                diaryRepository.save(Diary.builder()
                        .id(nowDiary.getId())
                        .text(text)
                        .weather(nowDiary.getWeather())
                        .icon(nowDiary.getIcon())
                        .date(date)
                        .temperature(nowDiary.getTemperature())
                        .build()
                ));
    }

    public String deleteDiary(LocalDate date) {
        List<Diary> result = diaryRepository.findAllByDate(date);
        if (result.isEmpty()) {
            throw new DiaryException(ErrorCode.NULL_DATA_ERROR);
        }
        diaryRepository.deleteAllByDate(date);
        return "삭제 완료";
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey + "&units=metric";
        //System.out.println(apiUrl);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int resCode = conn.getResponseCode();
            BufferedReader br;
            if (resCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }
}
