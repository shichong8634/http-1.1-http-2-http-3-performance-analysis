# Testing Procedure

For each HTTP protocol (HTTP/1.1, HTTP/2, and HTTP/3), the following procedure was performed:

1. Open Google Chrome and access the experimental website.

2. Open Chrome Developer Tools by pressing F12.

3. Navigate to the Network panel.

4. Enable "Disable cache" to prevent previously stored resources from affecting the results.

5. Perform a hard reload of the webpage.

6. Record the following performance metrics from the Network panel:

   - Requests
   - Transferred data
   - Time To First Byte (TTFB)
   - Number of Resources
   - Finish Time
   - DOMContentLoaded Time
   - Load Time

7. Repeat the experiment 10 times for each protocol.

8. Export and organise the collected data into Excel spreadsheets for statistical analysis.