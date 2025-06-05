const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const config = require('./config/config');
const uploadRouter = require('./routes/upload');
// const downloadRouter = require('./routes/download');


const app = express();

const corsOptions = {
  origin: 'http://localhost:3000', // 只允许前端域名访问
  methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
  credentials: true, // 如果需要传递 cookies
  optionsSuccessStatus: 204
};

// 中间件
app.use(cors(corsOptions));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 路由
app.use('/api/upload', uploadRouter);
// app.use('/download', downloadRouter);

app.use((err, req, res, next) => {
   console.error('Error Stack:', err.stack);
   console.error('Request Details:', {
    method: req.method,
    url: req.originalUrl,
    headers: req.headers,
    params: req.params,
    query: req.query,
    body: req.body
  });
  
  // 检查是否是 multer 错误
  if (err instanceof multer.MulterError) {
    return res.status(400).json({
      error: 'File upload error',
      details: err.code // 'LIMIT_FILE_SIZE' 等
    });
  }
  
  // 处理未定义变量错误
  if (err.message.includes('undefined')) {
    return res.status(500).json({
      error: 'Server configuration error',
      message: 'A required variable was undefined'
    });
  }
  
  // 默认错误处理
  res.status(500).json({ 
    error: 'Internal Server Error',
    message: err.message 
  });
  next(new Error('Something went wrong'));
});


// 启动服务器
const PORT = config.port || 3001;
app.listen(PORT, () => {
  console.log(`Proxy server running on port ${PORT}`);
});