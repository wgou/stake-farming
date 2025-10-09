let Web3 = require('web3')
let web3 = new Web3();

const ethers = require('ethers');

var express = require('express')
var bodyParser = require('body-parser');
var app = express()
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));




 

app.post("/decodeOutput", (req, resp) => {
    let { abi, data } = req.body
   let { outputs } = JSON.parse(abi);
    let result = web3.eth.abi.decodeParameters(outputs, data);
    let _result = [];
    for(let i = 0; i < result.__length__; i ++){
        let item = result[i];
        if(outputs[i].type === "tuple[]"){
            item.forEach(__item => {
                _result.push(__item);
            })
        } else if (outputs[i].components) {
            let obj = {};
            outputs[i].components.forEach(__item=>{
                obj[__item.name] = item[__item.name]
            })
            _result.push(obj);
        }
        else {
            _result.push(item);
        }
    }
    console.log("decodeOutput:" ,result)
    resp.send(_result)
  

})


app.post("/encodeInput", (req, resp) => {
  let { abi, data } = req.body


  console.log("Received ABI:", abi);
  console.log("Received Data:", data);

   let  encodedParams = web3.eth.abi.encodeFunctionCall(JSON.parse(abi), data);
    console.log("encodeInput:", encodedParams);
    resp.send(encodedParams);

})


 
app.listen(8000);
console.log("app server 8000 启动成功!");

 