const http2 = require("http2");
const fs = require("fs");
const express = require("express");


const app = express();


app.use(express.static("public"));


const server=http2.createSecureServer({

    key:fs.readFileSync("./cert/key.pem"),

    cert:fs.readFileSync("./cert/cert.pem")

},app);



server.listen(3443,()=>{

    console.log("HTTP/2 running");

});