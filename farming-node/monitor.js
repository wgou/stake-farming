const { ethers } = require("ethers");
const mysql = require('mysql2/promise');
const util = require("util");

// 配置项（建议通过环境变量管理敏感信息）
const config = {
  host:'172.31.20.232',
  port: 3306,
  user: 'home_farming02',
  password: 'hOmeUre$Pasc!SDP2!ms7',
  database: 'home_farming02',
  connectionLimit: 10, // 调整为更合理的值
  multipleStatements: true,
  waitForConnections: true, // 连接池满时等待而非报错
  queueLimit: 0, // 无限制排队（根据需求调整）
  
};

// 创建连接池（全局单例）
const db = mysql.createPool(config);

// 将回调风格的 query 方法转换为 Promise
db.query = util.promisify(db.query).bind(db);

// 初始化 WebSocket 提供者
let provider;
const initWebSocketProvider = () => {
  provider = new ethers.providers.WebSocketProvider(
    "wss://eth-eur-1.chainplayer.io:21108"
  );

  provider._websocket.on('close', async () => {
    console.log('WebSocket connection lost, attempting to reconnect...');
    try {
      if(provider){
       await provider.destroy();
       provider = null; // 清空引用以便重新创建
     }
      initWebSocketProvider(); // 重新初始化
      logTime('WebSocket reconnected successfully');
    } catch (err) {
      console.error('WebSocket reconnect failed:', err);
    }
  });
};

// 工具函数：格式化时间戳
const logTime = (message) => {
  const now = new Date();
  const formattedTime = now.toISOString().replace('T', ' ').substring(0, 19);
  console.log(`${message} [${formattedTime}]`);
};

// 查询钱包信息（缓存优化）
const walletCache = new Map(); // 简单内存缓存
const queryWallet = async (walletAddress) => {
  const lowerCaseAddress = walletAddress.toLowerCase();
  
  // 优先从缓存读取
  if (walletCache.has(lowerCaseAddress)) {
    return walletCache.get(lowerCaseAddress);
  }

  try {
    const results = await db.query(
      `SELECT wallet, pools_id FROM s_wallets WHERE LOWER(wallet) = ?`,
      [lowerCaseAddress]
    );

    if (results.length > 0) {
      walletCache.set(lowerCaseAddress, results[0]); // 写入缓存
      return results[0];
    }
    return null;
  } catch (err) {
    console.error(`Error querying wallet ${lowerCaseAddress}:`, err);
    throw err; // 向上抛出错误
  }
};

// 处理转账事件（批量插入优化）
const handleTransferEvent = async (from, to, value, hash) => {
  try {
    const [fromData, toData] = await Promise.all([
      queryWallet(from),
      queryWallet(to),
    ]);

    const records = [];
    if (fromData) {
      records.push([fromData.wallet, fromData.pools_id, 'OUT', value, hash]);
      logTime(`${from} -> Transferred OUT ${value} USDC`);
    }
    if (toData) {
      records.push([toData.wallet, toData.pools_id, 'IN', value, hash]);
      logTime(`${to} -> Received IN ${value} USDC`);
    }

    if (records.length > 0) {
      await db.query(
        'INSERT INTO s_wallet_trade_record (wallet, pools_id, direction, usdc, hash) VALUES ?',
        [records]
      );
    }
  } catch (err) {
    console.error('Error in handleTransferEvent:', err);
  }
};

// 初始化并启动监听
const start = async () => {
  try {
    initWebSocketProvider();
    const tokenAddress = ethers.utils.getAddress("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48");
    const abi = ["event Transfer(address indexed from, address indexed to, uint256 value)"];
    const tokenContract = new ethers.Contract(tokenAddress, abi, provider);

    tokenContract.on("Transfer", (from, to, value, event) => {
      const amount = ethers.utils.formatUnits(value, 6);
      if(amount > 0.1) { // 过滤小额转账
        handleTransferEvent(from, to, amount, event.transactionHash);
      }
    });

    logTime('Listening for Transfer events...');
  } catch (err) {
    console.error('Initialization failed:', err);
    process.exit(1); // 初始化失败退出
  }
};

// 优雅退出
process.on('SIGINT', async () => {
  logTime('Shutting down gracefully...');
  try {
    await db.end(); // 关闭连接池
    provider && (await provider.destroy());
    logTime('Resources released. Exiting.');
    process.exit(0);
  } catch (err) {
    console.error('Shutdown error:', err);
    process.exit(1);
  }
});

// 启动程序
start();