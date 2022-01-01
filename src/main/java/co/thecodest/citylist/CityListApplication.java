package co.thecodest.citylist;

import co.thecodest.citylist.domain.City;
import co.thecodest.citylist.repository.CityRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class CityListApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityListApplication.class, args);
    }

    @Bean
    ApplicationRunner init(CityRepository cityRepository) {
        return args -> {
            final int citiesNumber = cityRepository.findAll().size();
            if (citiesNumber == 0) {
                try (BufferedReader fileReader = new BufferedReader(new FileReader("src/main/resources/cities.csv"));
                     final CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

                    final Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                    for (CSVRecord csvRecord : csvRecords) {
                        cityRepository.save(new City(
                                Long.parseLong(csvRecord.get("Id")),
                                csvRecord.get("Name"),
                                csvRecord.get("Photo")
                        ));
                    }

                } catch (IOException e) {
                    throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
                }
            }
        };
    }

}
