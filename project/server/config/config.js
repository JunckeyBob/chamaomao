const path = require('path');

module.exports = {
  port: 3001,
  backend: {
    baseUrl: 'http://localhost:8080', // Spring Boot 后端地址
    uploadEndpoint: '/api/files'      // 后端文件上传接口
  },
  upload: {
    tempDir: '../uploads',             // 临时文件存储目录
    maxFileSize: 50 * 1024 * 1024,    // 50MB
    allowedTypes: ['image/jpeg', 'image/png', 'application/pdf']
  }
};