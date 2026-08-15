[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.21937087.svg)](https://doi.org/10.5281/zenodo.21937087)

## Performance Evaluation of HTTP/1.1, HTTP/2 and HTTP/3: A Comparative Experimental Study

# Overview

This project is an undergraduate research study that evaluates the performance differences between HTTP/1.1, HTTP/2, and HTTP/3.

Version 2.0 extends the previous experiment by adding a larger experimental dataset and a wider range of performance metrics. The experiment focuses on a single HTML document request under the same testing environment.

A total of 3000 measurements were collected, including 1000 tests for each HTTP protocol.

# Research Objectives

The main objectives of this project are:

- To compare the performance of HTTP/1.1, HTTP/2, and HTTP/3.
- To analyse response time and network performance under the same testing conditions.
- To evaluate protocol performance through repeated experiments.
- To collect a larger dataset for statistical analysis.

# Research Questions

This study addresses the following research questions:

- How do HTTP/1.1, HTTP/2, and HTTP/3 differ in performance?
- Which protocol provides lower response time under the tested conditions?
- How do network metrics differ between the three protocols?
- How stable are the protocols during repeated measurements?

# Experimental Methodology

A web-based application was developed using:

- HTML
- CSS
- JavaScript

The backend environment was implemented using:

- Node.js
- Express.js

The website was tested under three HTTP protocol environments:

- HTTP/1.1
- HTTP/2
- HTTP/3

For Version 2.0, the experiment focuses on a single `index.html` document request.

Each protocol was tested 1000 times under the same experimental conditions, resulting in a total of 3000 measurements.

# Performance Metrics

The following metrics were collected and analysed:

| Metric | Description |
|---|---|
| Response Time | Time required to complete the HTTP request |
| TTFB | Time To First Byte |
| RTT | Round Trip Time |
| Jitter | Variation in network delay |
| Packet Loss | Percentage of lost packets |
| Data Size | Amount of transferred data |
| Throughput | Amount of data transferred over time |
| Success Rate | Percentage of successful requests |
| Standard Deviation | Measurement of performance variation |

# Technologies and Tools

### Development

- Node.js
- Express.js
- HTML
- CSS
- JavaScript

### Testing and Analysis

- Microsoft Edge
- Edge DevTools Protocol (CDP)
- QUIC / HTTP/3
- Microsoft Excel
- Git & GitHub
  
# Experimental Results

Version 2.0 compares HTTP/1.1, HTTP/2 and HTTP/3 using 1000 measurements for each protocol.

The main results include:

- HTTP/3 achieved the lowest average response time.
- HTTP/3 achieved the lowest average TTFB.
- HTTP/1.1 and HTTP/2 achieved a 100% success rate.
- HTTP/3 achieved a 98.3% success rate.
- The collected data was analysed using mean, median and standard deviation.


## Average Page Load Time

![Response Time](figures/V2.0/figure1_Response_Time.png)


## Average TTFB

![TTFB](figures/V2.0/figure2_TTFB_Mean.png)


## Success Rate

![Success Rate](figures/V2.0/figure3_Success_Rate.png)


# Repository Structure
Performance-Analysis-of-Modern-Web-Protocols/

│── README.md

│── website/
└── Web application source code

│── datasets/
├── raw_HTTP1.1 data
├── raw_HTTP2 data
└── raw_HTTP3 data
└── integral data

│── experiment/
└──Testing and Screenshots

│── results/
└── Statistical analysis and summary

│── figures/
└── Experimental result figures

│── paper/
└── Research paper

# Future Work

Future improvements may include:

- Using the collected data for HTTP performance prediction.
- Investigating machine-learning-based protocol selection.

------------------------------------------

Yunlong Wang

BSc (Hons) in Computing Science

Griffith College Dublin
