package client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http3.client.HTTP3Client;
import org.eclipse.jetty.http3.client.transport.HttpClientTransportOverHTTP3;
import org.eclipse.jetty.quic.client.ClientQuicConfiguration;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class Http3ClientTest {

    public static void main(String[] args) throws Exception {

        SslContextFactory.Client sslContextFactory =
                new SslContextFactory.Client();

        ClientQuicConfiguration quicConfiguration =
                new ClientQuicConfiguration(
                        sslContextFactory,
                        null
                );

        HTTP3Client http3Client =
                new HTTP3Client(
                        quicConfiguration
                );

        HttpClientTransportOverHTTP3 transport =
                new HttpClientTransportOverHTTP3(
                        http3Client
                );

        HttpClient client =
                new HttpClient(transport);

        client.start();

        System.out.println("HTTP/3 client started.");

        client.stop();
    }
}