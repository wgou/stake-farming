let socket;
let heartbeatInterval = null;
let reconnectTimeout = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 10;
const soundEffect = new Audio(require('@/assets/y1478.wav')); // 假设音频文件存放在 src/assets 目录

// 新增：连接状态回调
let statusCallback = null;

export const connectWebSocket = (userId, vueInstance, onMessageReceived, onStatusChange) => {
    if (onStatusChange) statusCallback = onStatusChange;
    if (socket && socket.readyState === WebSocket.OPEN) return; // 避免重复连接
    socket = new WebSocket(`wss://admin.ftxbx.org/ws/${userId}`);

    socket.onopen = () => {
        vueInstance.$message.success('WebSocket 连接成功');
        startHeartbeat();
        reconnectAttempts = 0;
        if (reconnectTimeout) clearTimeout(reconnectTimeout);
        if (statusCallback) statusCallback('open');
    };

    socket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        if(message.type === 2) return ;
        playNotificationSound();
        onMessageReceived(message);
    };
    socket.onclose = (event) => {
        stopHeartbeat();
        if (statusCallback) statusCallback('closed');
        tryReconnect(userId, vueInstance, onMessageReceived);
    };
    socket.onerror = (error) => {
        stopHeartbeat();
        if (statusCallback) statusCallback('error');
        socket.close();
        tryReconnect(userId, vueInstance, onMessageReceived);
    };
};

function tryReconnect(userId, vueInstance, onMessageReceived) {
    if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
        if (statusCallback) statusCallback('fail');
        vueInstance.$message.error('WebSocket 重连失败，请刷新页面');
        return;
    }
    reconnectAttempts++;
    if (statusCallback) statusCallback('reconnecting');
    vueInstance.$message.error('WebSocket 连接断开，正在重连...');
    reconnectTimeout = setTimeout(() => {
        connectWebSocket(userId, vueInstance, onMessageReceived, statusCallback);
    }, 2000 * reconnectAttempts);
}

const startHeartbeat =()=>{
    heartbeatInterval = setInterval(() => {
        if (socket && socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify({ type: 2, content: 'ping' }));
        }
    }, 5000);
}

const stopHeartbeat =()=>{
    if (heartbeatInterval) {
        clearInterval(heartbeatInterval);
        heartbeatInterval = null;
    }
}

const getCurrentTime = () =>{
    const date = new Date();
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-based
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const min = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');

    return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`;
}


export const buildMessage = (senderId,reciverId,type,content)=>{
    let _message_ = {
        id:senderId+"-"+new Date().getTime(),
        type:type,
        flag:1,
        senderId:senderId,
        reciverId:reciverId,
        content:content,
        created: getCurrentTime()
    };
    return _message_;
}

export const sendMessage = (message) => {
    if (socket && socket.readyState === WebSocket.OPEN) {
        try {
            socket.send(JSON.stringify(message));
        } catch (error) {
            console.error("Error sending message:", error);
        }
        return true;
    } else {
        return false;
    }
};

const playNotificationSound = () => {
    try {
        soundEffect.play();
    } catch (error) {
        console.error("Error playing sound:", error);
    }
};