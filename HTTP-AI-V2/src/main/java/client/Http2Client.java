package client;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Http2Client {


    public HttpResponse<String> send(
            String url
    ) throws Exception {


        HttpClient client =
                HttpClient.newBuilder()
                        .version(
                                HttpClient.Version.HTTP_2
                        )
                        .build();


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(url)
                        )
                        .GET()
                        .build();


        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

    }


}