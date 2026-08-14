import client.Http2Client;


public class TestHTTP2 {


    public static void main(String[] args)
            throws Exception {


        Http2Client client =
                new Http2Client();


        var response =
                client.send(
                        "https://www.yunlong-performance-research.com"
                );


        System.out.println(
                "Status: "
                        +
                        response.statusCode()
        );


        System.out.println(
                "Version: "
                        +
                        response.version()
        );

    }

}