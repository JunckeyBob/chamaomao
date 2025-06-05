const express = require('express');
const router = express.Router();
const upload = require('../middlewares/fileUpload');
const axios = require('axios');
const path = require('path');
const fs = require('fs');
const config = require('../config/config');

// 文件上传路由
router.post('/', upload.single('file'), async (req, res, next) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'No file uploaded' });
    }

   // console.log(1)

    // 构造文件URL
  //  const fileUrl = `${req.protocol}://${req.get('host')}/uploads/${req.file.filename}`;

    console.log(req.file.filename)
    


    // 响应客户端
    res.json({
      message: 'File uploaded successfully',
      data: req.file.filename
    });

  } catch (error) {
    // 清理上传的文件（如果有）
    if (req.file) {
      fs.unlink(path.join(config.upload.tempDir, req.file.filename), () => {});
    }
    next(error);
  }
});

// 文件下载路由
router.get('/:filename', (req, res) => {
  console.log(1);
  let hed = path.resolve(__dirname, '../..')
  const filePath = path.join(hed, "uploads", req.params.filename);
  console.log(filePath);
  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).send('File not found');
    }
    console.log(2)
    res.sendFile(filePath);
  });
});

module.exports = router;