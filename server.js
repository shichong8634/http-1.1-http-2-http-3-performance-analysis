const express = require("express");

const app = express();

//Provide static web pages in the website folder
app.use(express.static("website"));

////Start the server
app.listen(3000, () => {
    console.log("HTTP server is running!");
    console.log("Open: http://localhost:3000");
});