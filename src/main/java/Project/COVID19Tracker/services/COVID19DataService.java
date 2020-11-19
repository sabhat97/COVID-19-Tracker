package Project.COVID19Tracker.services;

import Project.COVID19Tracker.models.locationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class COVID19DataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<locationStats> allStats = new ArrayList<>();

    public List<locationStats> getAllStats() {
        return allStats;
    }

    // As soon as the instance of the service is created it executes this method
    @PostConstruct
    //Scheduled the method to run every 1st hour of everyday
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        //Creating a new HTTP client
        HttpClient client = HttpClient.newHttpClient();

        //Creating a new HTTP request using the builder pattern
        HttpRequest request = HttpRequest.newBuilder()

                //Create a URI of VIRUS_DATA_URL
                // then pass it to the outside uri method in the form of string
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        // Sending the client request and take the body and return as the String
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        //Make the above string as reader
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        //passing the reader using the open source library for parsing CSV
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        //Using this list because of concurrency issue
        //If someone tries to use our service while we are creating this they should not get error
        //After we are done with creating this we populate the allStat with newStat at the end
        List<locationStats> newStat = new ArrayList<>();

        //Looping through the raw data and printing the records under the column or header "Province/State"
        for (CSVRecord record : records) {
            locationStats locationStat = new locationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCase = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCase = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setTotalNewCases(latestCase);
            locationStat.setDiffFromPrevDay(latestCase - prevDayCase);
            newStat.add(locationStat);

        }
        this.allStats = newStat;
    }
}
