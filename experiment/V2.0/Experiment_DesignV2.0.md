## Experiment Design

# Objective

The objective of this experiment is to evaluate and compare the performance of HTTP/1.1, HTTP/2, and HTTP/3 using the same web application environment.

The study focuses on analysing response time and network performance for a single HTML document request.

# Independent Variables

The independent variable in this experiment is the HTTP protocol version:

HTTP/1.1
HTTP/2
HTTP/3

# Dependent Variables

The following performance metrics were collected and analysed:

Response Time
Time To First Byte (TTFB)
Round Trip Time (RTT)
Jitter
Packet Loss
Transferred Data Size
Throughput
Success Rate

Additionally, mean, median and standard deviation were calculated to compare performance and stability.

# Controlled Variables

To ensure a fair comparison, the following factors were kept consistent:

Same test website
Same target HTML document
Same browser environment
Same testing procedure
Same measurement method
Same test URL

# Test Website

The experiment used a self-developed web application as the testing platform.

The website contains several pages, including Home, About, Gallery and Contact.

For Version 2.0, the index.html document was used as the main test target. The experiment focused on the performance of a single HTML document request rather than loading all website resources.

# Experimental Environment

The website was developed using:

HTML
CSS
JavaScript

The server environment was implemented using:

Node.js
Express.js

HTTP/3 testing was performed using Microsoft Edge with QUIC support.

# Browser and Measurement Tools

The experiment used:

Browser

Microsoft Edge

Performance Measurement Tool

Microsoft Edge DevTools Protocol (CDP)

The testing program collected the required network performance data during each request.

# Experimental Procedure

For each HTTP protocol:

The same target HTML document was requested.
The same testing environment was used.
The required performance metrics were recorded.
The experiment was repeated 1000 times.

A total of 3000 measurements were collected:

HTTP/1.1: 1000 tests
HTTP/2: 1000 tests
HTTP/3: 1000 tests