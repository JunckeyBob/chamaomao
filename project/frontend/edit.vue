 1. 获取user和cat数据
  2. 实现修改/添加功能
  3. 如果是修改，取消和确认都跳转到原详情页面；如果是添加，取消跳转到home，确认跳转到猫猫详情页面

<template>
<!-- main content -->
  <div class="home-container">
    <!-- head with back button -->
    <div class="head-container">
      <!-- back button -->
      <button class="back-button" @click="this.$router.push('/home')">
        <i class="fa-solid fa-arrow-left"></i>&nbsp;返回
      </button>
    </div>

    <!-- content container -->
    <div class="content-container">

      <!-- information container -->
      <div class="info-container">
        <!-- image -->
        <div class="image-container">
          <!-- simulation image -->
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileChange" 
            accept="image/*"
          >
          <div v-if="previewUrl" class="preview">
          <img :src="previewUrl" alt="预览图">
          </div>
        </div>
        <!-- text -->
        <div class="text-container">
          <div class="name-container">
            <h1 class="name" >名字：</h1>
            <input class="edit-name" v-model="name"></input>
            <select style="margin: 50px 0 0 20px" v-model="status">
              <option disabled selected>选择状态</option>
              <option>已领养</option>
              <option>待领养</option>
              <option>留园观察</option>
              <option>失踪</option>
            </select>
          </div>
          <div class="line-container"></div>
          <div class="line-container">
            <div class="attr-container">品种：</div>
            <select v-model="breed">
              <option disabled selected>选择品种</option>
              <option>橘猫 / 橘白</option>
              <option>玳瑁 / 三花</option>
              <option>狸猫 / 狸白</option>
              <option>奶牛</option>
              <option>纯色</option>
              <option>品种猫</option>
            </select>
          </div>
          <div class="line-container">
            <div class="attr-container">基本信息：</div>
          </div>
          <input class="input-container" v-model="basicinfo"></input>
          <div class="line-container">
            <div class="attr-container">性格：</div>
          </div>
          <input class="input-container" v-model="catsnality"></input>
        </div>
      </div>

    </div>

    <!-- button container -->
    <div class="button-container">
      <button class="op-button" @click="AddCatInfo()"><i class="fa-solid fa-check"></i>&nbsp;确认</button>
      <button class="op-button" @click="this.$router.push('/home')"><i class="fa-solid fa-xmark"></i>&nbsp;取消</button>
    </div>

  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
axios.defaults.withCredentials = true; 

const fileInput = ref(null);
const file = ref(null);
const previewUrl = ref('');

const handleFileChange = (e) => {
  const selectedFile = e.target.files[0];
  if (!selectedFile) return;

  // 验证文件类型
  if (!selectedFile.type.startsWith('image/')) {
    showMessage('请选择图片文件', 'error');
    return;
  }

  // 验证文件大小（限制5MB）
  if (selectedFile.size > 5 * 1024 * 1024) {
    showMessage('文件大小不能超过5MB', 'error');
    return;
  }

  file.value = selectedFile;
  previewUrl.value = URL.createObjectURL(selectedFile);
  console.log("photo_ok!");
};

export default {
  name: 'Edit',
  setup () {
    var cat = ref({
      id: 1,
      name: '猫猫名字',
      img: '',
      type: '',
      state: ''
    });

    var user = ref({
      name: '用户名字',
      ID: '11111111',
      role: 'supervisor'
    });

    onMounted(() => {
      document.title = '编辑猫猫信息';

      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
    });

    return {
      cat,
      user
    };
  }, 
  data() {
    return {
      name:'',
      status:'',
      breed:'',
      basicinfo:'',
      catsnality:''
    }
  },
  methods: {
    AddCatInfo() {
      let url = "http://localhost:8080/api/animals"
      let id = 0
      axios.post(url, {name:this.name, species:"猫", breed:this.breed, gender:"FEMALE", healthStatus:this.status, basicinfo:this.basicinfo, catsnality:this.catsnality, age:'3'}).then(res=>{
        if(res.status == 201){
            console.log(res.data)
            id = res.data.animalId
        }
      url = url + "/" + id + "/photos"
      const formData = new FormData();
      formData.append('file', file.value);
      axios.post(url, formData,  {headers: {
    'Content-Type': 'multipart/form-data'
  }}).then(res=>{
        console.log(res.data)
        if(res.status == 200){
            this.$router.push('/home') // or wherever App.vue content should go

    }})

    })
   }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');


.home-container {
  width: 100%;
  height: 100%;
}

.head-container {
  display: block;
  width: 100%;
  height: 10%;
  padding-top: 20px;
}

.content-container {
  display: block;
  width: 100%;
  min-height: 70%;
  background-image: url("./img/stripes.svg");
  background-size: cover;
}

.info-container {
  display: block;
  width: 100%;
  min-height: 400px;
}

.button-container {
  display: block;
  width: 100%;
}

.image-container {
  float: left;
  display: block;
  width: 500px;
  height: 100%;
  background-image: url("./img/head.svg");
  background-size: cover;
}

.text-container {
  float: left;
  display: block;
  height: 100%;
  padding: 0px 60px;
  color: #fff3d8;
}

.back-button {
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  border-radius: 10px;
  margin: 10px 40px;
  height: 40px;
  width: 100px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 25px;
}

.preview img {
  max-width: 100%;
  max-height: 200px;
  margin-top: 10px;
}

.image {
  background-color: #ff9600;
  color: #fffed8;
  border: none;
  height: 260px;
  width: 260px;
  border-radius: 130px;
  margin: 120px;
}

.image-title {
  margin-left: 80px;
  font-size: 50px;
}

.name-container {
  display: block;
  width: 100%;
  min-height: 90px;
}

.name {
  float: left;
  font-size: 50px;
  font-weight: bold;
}

.edit-name {
  float: left;
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  border-radius: 10px;
  height: 50px;
  width: 300px;
  padding: 5px 15px 5px 15px;
  margin: 35px 0 0 10px;
  font-size: 40px;
}

.add-icon {
  font-size: 160px;
  font-weight: bold;
  float: left;
  margin: 30px 0 0 60px;
}

.state {
  float: left;
  font-size: 25px;
  color: #ff9600;
  margin: 55px 10px;
}

.info {
  float: left;
  font-size: 35px;
  color: #fff3d8;
  margin: 0px;
}

.op-button {
  float: right;
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 20px;
  margin-right: 30px;
  height: 70px;
  width: 240px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 45px;
}

select {
  float: left;
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 10px;
  padding: 5px 10px 5px 10px;
  font-size: 30px;
  font-family: 'ZCOOL KuaiLe', sans-serif;
}

option {
  background-color: #ff9600;
  color: #fff3d8;
  font-size: 30px;
  font-family: 'ZCOOL KuaiLe', sans-serif;
}

.line-container {
  display: block;
  width: 100%;
  min-height: 40px;
  margin: 0 0 20px 0;
}

.attr-container {
  font-size: 30px;
  color: #fff3d8;
  font-family: 'ZCOOL KuaiLe', sans-serif;
  display: block;
  min-height: 40px;
  float: left;
  min-width: 100px;
  margin: 0 0 0 0;
}

.input-container {
  display: block;
  min-width: 100px;
  min-height: 30px;
  font-size: 25px;
  font-family: 'ZCOOL KuaiLe', sans-serif;
  color: #fff3d8;
  background-color: #ff9600;
  padding: 10px 10px 10px 10px;
  border-radius: 10px;
  margin-bottom: 20px;
}
</style>