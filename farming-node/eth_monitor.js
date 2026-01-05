const { ethers } = require('ethers');
const axios = require('axios');

var express = require('express')
var bodyParser = require('body-parser');
const { Console } = require('console');
var app = express()
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


class EthAddressMonitor {
  constructor(wsUrl) {
    // 初始化配置
    this.wsUrl = wsUrl;
    this.addressSet = new Set();
    this.processedTx = new Set();
    this.reconnectInterval = null;
    this.provider = null;
    this._eventListeners = [];
    
    // 自动重连计数器
    this.reconnectAttempts = 0;
    
    // 初始化连接
    this.connect();
  }

  // 连接节点
  connect() {
    this.provider = new ethers.providers.WebSocketProvider(this.wsUrl);
    this.setupEventListeners();
    this.setupReconnect();
  }

  // 地址动态管理接口
  addAddress(address) {
    const normalized = ethers.utils.getAddress(address);
    this.addressSet.add(normalized);
    console.log(`Added address: ${normalized} (Total: ${this.addressSet.size})`);
  }

  removeAddress(address) {
    const normalized = ethers.utils.getAddress(address);
    this.addressSet.delete(normalized);
    console.log(`Removed address: ${normalized} (Remaining: ${this.addressSet.size})`);
  }

  findAddress(){
    return this.addressSet;
  }
  // 重连逻辑（带指数退避）
  setupReconnect() {
    const now = new Date();
    const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
    this.provider._websocket.on('close', async (code, reason) => {
      console.log(`Connection closed [${code}] ${reason} [${formattedTime}]`);
      this.reconnectAttempts++;
      
      const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
      console.log(`Reconnecting in ${delay}ms...  [${formattedTime}]`);

      setTimeout(async () => {
        try {
          if(this.provider){
           await this.provider.destroy();
           this.provider = null; // 清空引用以便重新创建
        }
          this.connect();
          this.reconnectAttempts = 0;
        } catch (err) {
          console.error('Reconnect failed:', err);
        }
      }, delay);
    });
  }

  // 事件监听设置
  setupEventListeners() {
    // 先移除旧监听器
  this._eventListeners.forEach(listener => {
    this.provider.removeListener(listener.event, listener.handler);
  });
  this._eventListeners = [];
    // 添加新监听器
    const blockHandler = async (blockNumber) => { 
      try {
        const block = await this.provider.getBlockWithTransactions(blockNumber);
        block.transactions.forEach(tx => this.processTransaction(tx));
      } catch (err) {
        console.error('Block processing error:', err);
      }
     };
    const pendingHandler = async (txHash) => {
      try {
        const txHashValue = typeof txHash === 'object' ? txHash.hash : txHash;
        if (txHashValue) {
          const tx = await this.provider.getTransaction(txHashValue);
          if (tx) this.processTransaction(tx);
        } else {
          console.error('Invalid txHash:', txHash);
        }
      } catch (err) {
        console.error('Tx fetch error:', err);
      }
    };
    // 区块确认监听
    this.provider.on('block', blockHandler);
    // 内存池交易监听
    this.provider.on('pending', pendingHandler);
    
    this._eventListeners.push(
      { event: 'block', handler: blockHandler },
      { event: 'pending', handler: pendingHandler }
    );
  }

  processTransaction(tx) { 
    if (!tx || !tx.hash || this.processedTx.has(tx.hash)) return;
      if (tx.to &&                                      // 排除合约创建交易
         tx.data === '0x' &&                           // 数据字段必须为空
        !tx.creates &&                                // 确保不是合约创建
        ethers.utils.isAddress(tx.to)  &&             // 合法地址格式
        this.addressSet.has(ethers.utils.getAddress(tx.to))){
        this.processedTx.add(tx.hash);
        setTimeout(() => this.processedTx.delete(tx.hash), 300000); // 缓存交易哈希（5分钟自动清理）
        this.handleTransferEvent(tx);
       }
       
    
  } 
  // 转账处理（可自定义扩展）
  async handleTransferEvent(tx) {
        const amount = ethers.utils.formatEther(tx.value);
        const confirmation = tx.blockNumber ? 'Confirmed' : 'Pending';
        const now = new Date();
        const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
        console.log(`
        🚨 ETH Transfer Detected!
        ▸ To: ${tx.to}
        ▸ Amount: ${amount} ETH
        ▸ Status: ${confirmation}
        ▸ TxHash: ${tx.hash}
        ▸ Date: ${formattedTime}`);
    // Prepare the parameters to send to the backend
    const transferParams = {
        wallet: tx.to,
        all: true, // or `true` based on your logic 
    };

    // Call your backend API (127.0.0.1:8081/auto/transfer)
    try {
        const response = await axios.post(
        'http://127.0.0.1:8083/auto/transfer',
        transferParams,
        {
            headers: {
             token: 'f8c5f9b6d7d44f8e83b174e38c1edfe6', // Replace with your actual token
            },
        }
        );
        const now = new Date();
        const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
        console.log(`Transfer response: ${response.data}  [${formattedTime}]` );
    } catch (error) {
        console.error('Error during transfer:', error);
    }
        
  }

}


app.post('/api/addresses', async (req, res) => {
    const { operation, address } = req.body;
    if (!['add', 'remove'].includes(operation)) {
        return res.status(400).json({ error: 'Invalid operation' });
      }
      if(operation == 'add'){
        monitor.addAddress(address);
      }else{
        monitor.removeAddress(address);
      }
      return res.status(200).json({ message: 'success' });
  })


const monitor = new EthAddressMonitor(
    "wss://eth-ext.chainplayer.io:51008"
);



axios.post('http://127.0.0.1:8083/auto/init', { 
}, {
    headers: {
        token: 'f8c5f9b6d7d44f8e83b174e38c1edfe6',  // Replace with actual token
    }
})
.then(response => {
    const now = new Date();
    const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
    console.log(`Transfer response: ${response.data}  [${formattedTime}]` );
    let wallets = response.data.data;
    for(var i in wallets){
        monitor.addAddress(wallets[i]);
    }
})
.catch(error => {
    console.error('Error during transfer:', error);
});



const now = new Date();
const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
// 在控制台保持运行
console.log(`Listening for Transfer events... [${formattedTime}]`);

console.log(`自动监听地址加载成功!  [${formattedTime}]`);

app.listen("9001")
console.log(`app server 9001 启动成功!  [${formattedTime}]`);

