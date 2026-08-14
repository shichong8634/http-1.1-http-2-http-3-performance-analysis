import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestCDP {

    public static void main(String[] args) throws Exception {

        URL url =
                new URL(
                        "http://localhost:9223/json/version"
                );

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

        String line;

        while ((line = reader.readLine()) != null) {

            System.out.println(line);

        }

        reader.close();

        connection.disconnect();
    }
}