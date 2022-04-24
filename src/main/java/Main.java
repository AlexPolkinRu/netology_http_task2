import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private final static String NASA_API_KEY = "iAYus4NhKYQwRcHJdrLBElBcaLDASuA0Fv7w1Img";
    private final static String REQUEST_URL = "https://api.nasa.gov/planetary/apod?api_key=";

    private final static String DESTINATION_DIR = "src/main/resources/";

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        HttpGet request = new HttpGet(REQUEST_URL + NASA_API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)
        ) {

            Entry entry = mapper.readValue(
                    response.getEntity().getContent(),
                    new TypeReference<Entry>() {
                    }
            );

            String url = entry.getUrl();
            String[] splitUrl = url.split("/");

            String fileName = splitUrl[splitUrl.length - 1];

            downloadFile(httpClient, url, fileName);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    private static void downloadFile(CloseableHttpClient httpClient, String url, String fileName) {
        HttpGet fileRequest = new HttpGet(url);

        try (
                CloseableHttpResponse fileResponse = httpClient.execute(fileRequest);
                BufferedInputStream in = new BufferedInputStream(fileResponse.getEntity().getContent());
                FileOutputStream fout = new FileOutputStream(DESTINATION_DIR + fileName)
        ) {

            byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                fout.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}