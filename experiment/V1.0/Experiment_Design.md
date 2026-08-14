## Experiment Design

# Objective

The objective of this experiment is to evaluate and compare the performance of HTTP/1.1, HTTP/2, and HTTP/3 using the same web application environment.

The study focuses on analysing how different HTTP protocols affect webpage loading performance and response latency.

# Independent Variables

The independent variable in this experiment is the HTTP protocol version:

HTTP/1.1
HTTP/2
HTTP/3

# Dependent Variables

The following performance metrics were collected and analysed:

Page Load Time
Time To First Byte (TTFB)
DOMContentLoaded Time
Number of Requests
Transferred Data Size
Resource Count

Additionally, standard deviation was calculated to evaluate performance stability.

# Controlled Variables

To ensure a fair comparison, the following factors were kept consistent:

Same test website
Same webpage resources
Same image files
Same CSS files
Same JavaScript files
Same browser (Google Chrome)
Same testing procedure
Same measurement method

# Test Website

The experiment used a self-developed web application as the testing platform.

The website contains four main pages:

Home
About
Gallery
Contact

The Gallery page contains multiple image resources to simulate a modern webpage with multiple HTTP requests.

# Experimental Environment

The website was developed using:

HTML
CSS
JavaScript

The server environment was implemented using:

Node.js
Express.js

HTTP/3 support was enabled through Cloudflare using the QUIC protocol.

# Browser and Measurement Tools

The experiment used:

Browser

Google Chrome

Performance Measurement Tool

Chrome Developer Tools Network Panel

The following metrics were collected from the Network panel:

Requests
Transferred
TTFB
DOMContentLoaded
Load Time
Experimental Procedure

For each HTTP protocol:

The same website was loaded in Google Chrome.
Browser cache was disabled.
A hard reload was performed.
Performance metrics were recorded.
The experiment was repeated 10 times.

A total of 30 measurements were collected:

HTTP/1.1: 10 tests
HTTP/2: 10 tests
HTTP/3: 10 tests