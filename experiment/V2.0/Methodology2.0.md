## Methodology

# Experimental Environment

This study evaluates the performance differences between HTTP/1.1, HTTP/2, and HTTP/3 through a controlled web performance experiment. A self-developed web application was used as the experimental platform.

The web application was developed using HTML, CSS, and JavaScript. A Node.js environment with Express.js was used to serve the website resources.

Microsoft Edge was used for HTTP/3 testing. The browser was controlled through the Edge DevTools Protocol (CDP), and HTTP/3 communication was performed using QUIC.

# Website and Server Configuration

The experimental website contains multiple pages and static resources. For Version 2.0, the index.html document was selected as the main test target.

The experiment focused on a single HTML document request instead of loading the complete website. This was done to reduce the influence of additional resources such as images, CSS and JavaScript files.

The same target URL and testing conditions were used for HTTP/1.1, HTTP/2, and HTTP/3.

# Experimental Procedure

The same testing procedure was used for HTTP/1.1, HTTP/2, and HTTP/3. Each protocol was tested 1000 times using the same target HTML document.

During each test, the testing program sent a request and recorded the required performance information. The collected data was saved as raw experimental data.

A total of 3000 measurements were collected, including 1000 measurements for each protocol.

After the experiments were completed, the three datasets were combined into one master dataset and organised using Microsoft Excel.

# Performance Metrics

To evaluate the performance of the three HTTP protocols, several performance metrics were collected.

- (1) Response Time
  Response Time represents the time required to complete the HTTP request and receive the response.

- (2) Time To First Byte (TTFB)
  TTFB measures the time between sending a request and receiving the first byte of the response.

- (3) Round Trip Time (RTT)
  RTT represents the time required for data to travel from the client to the destination and return.

- (4) Jitter
  Jitter represents the variation in network delay between measurements.

- (5) Packet Loss
  Packet Loss represents the percentage of packets that were not successfully received.

- (6) Data Size
  Data Size represents the amount of data transferred during the request.

- (7) Throughput
  Throughput represents the amount of data transferred within a period of time.

- (8) Success Rate
  Success Rate represents the percentage of requests that completed successfully.

- (9) Standard Deviation
  Standard deviation was calculated to evaluate the stability of each protocol during repeated experiments.