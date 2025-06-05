// src/utils/eventBus.js
import mitt from 'mitt';

// 创建事件总线实例
const emitter = mitt();

// 可选：导出事件名称常量（避免拼写错误）
export const EVENTS = {
  MSG_FROM_A: 'msg-from-a',
  USER_LOGIN: 'user-login'
};

export default emitter;