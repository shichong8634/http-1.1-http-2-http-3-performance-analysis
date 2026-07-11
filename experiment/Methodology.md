## Methodology

# Experimental Environment
This study evaluates the performance differences between HTTP/1.1, HTTP/2, and HTTP/3 through a controlled web performance experiment. A web-based image gallery application was developed as the experimental platform. The website contains multiple pages, images, and static resources, representing a typical modern web application with multiple HTTP requests.
The web application was developed using HTML, CSS, and JavaScript. A Node.js environment with Express.js was used to serve the website resources. Express.js was selected because it provides a lightweight and simple framework for building web servers and handling static file requests.
The website was deployed in a public hosting environment and accessed through HTTPS. Cloudflare services were enabled to provide secure communication and HTTP/3 support based on the QUIC protocol. Google Chrome Developer Tools was used to monitor network activities and collect performance information during the experiments.
________________________________________
# Website and Server Configuration
The experimental website was designed as a gaming image gallery containing multiple images and static resources. The purpose of using an image-based website was to simulate a resource-intensive webpage where multiple files need to be requested and transferred between client and server.
For HTTP/1.1 testing, a Node.js Express server was configured to provide static website resources. The server used the default HTTP/1.1 communication method provided by Express.
For HTTP/2 testing, a secure HTTP/2 server was implemented using the native Node.js HTTP/2 module with TLS certificates. This configuration enabled HTTP/2 communication through an encrypted HTTPS connection.
For HTTP/3 testing, HTTP/3 support was enabled through Cloudflare using the QUIC transport protocol. The browser established HTTP/3 connections with the server when HTTP/3 was available.
The same website resources and testing conditions were maintained for all protocols to ensure a fair comparison.
________________________________________
# Experimental Procedure
The experiment was conducted using the same webpage and testing environment for HTTP/1.1, HTTP/2, and HTTP/3. Each protocol was tested repeatedly to reduce the influence of random network variations.
For each HTTP protocol, 10 independent tests were performed, resulting in a total of 30 experimental samples. During each test, the browser loaded the complete website, and network performance information was recorded using Chrome Developer Tools.
Before each measurement, the browser cache was disabled to reduce the impact of previously stored resources. The same webpage, resource files, and testing procedure were used throughout all experiments.
The collected data was exported and organized into Excel spreadsheets for further statistical analysis. Average values, maximum values, minimum values, and standard deviations were calculated to compare the performance and stability of different HTTP protocols.
________________________________________
# Performance Metrics
To evaluate the performance of different HTTP protocols, several important web performance metrics were selected.

- (1)Page Load Time
  Page Load Time represents the total time required for the webpage and its resources to completely load. This metric reflects the overall user experience and is one of the main indicators for evaluating web performance.

- (2)Time To First Byte (TTFB)
  Time To First Byte (TTFB) measures the time between sending an HTTP request and receiving the first byte of response data from the server. This metric reflects server response latency and connection performance.

- (3)Standard Deviation
  Standard deviation was calculated to evaluate the stability of each protocol during repeated experiments. A lower standard deviation indicates more consistent performance, while a higher value represents greater variation between tests.