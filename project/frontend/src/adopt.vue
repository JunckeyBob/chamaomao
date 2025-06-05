<!--
  TODO:
  1. 使用express下载或者上传
  2. 提交申请
  3. 对于supervisor角色，按照./template/adopt/adopt-su.html修改领养申请
-->

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
<template v-if="this.$globalData.user_id==2">
    <div class="content">
      <p>1. 先下载领养材料和申请表模板</p><br>
      <button class="op-button" @click="downnewFile()"><i class="fa-solid fa-download"></i>下载材料</button><br>
      <p>2. 填写表单后上传</p>
      <input
      type="file"
      ref="fileInput"
      @change="handleFileSelect"
      style="display: none"
    />
      <button @click="triggerFileInput" class="op-button"><i class="fa-solid fa-upload"></i>上传申请表</button><br>
      <p>3. 确认提交</p>
      <button class="op-button" @click="Uploadappl"><i class="fa-solid fa-paper-plane"></i>  提交申请</button><br>
    </div>
    </template>

<template v-if="this.$globalData.user_id==1">
    <div class="content">
      <p>1. 于此处下载修改前的材料</p><br>
      <button class="op-button" @click="downnewFile()"><i class="fa-solid fa-download"></i>下载旧材料</button><br>
      <p>2. 于此处上传修改后的材料</p>
      <input
      type="file"
      ref="fileInput"
      @change="handleFileSelect"
      style="display: none"
    />
      <button @click="triggerFileInput" class="op-button"><i class="fa-solid fa-upload"></i>上传新材料</button><br>
      <p>3. 点击确认修改</p>
      <button @click="upnewgud()" class="op-button"><i class="fa-solid fa-check"></i>确认修改</button><br>
    </div>    
</template>
  </div>
</template>



<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
axios.defaults.withCredentials = true; 


// const 
export default{
name: 'Adopt',
setup() {
onMounted(() =>{
  document.title = '领养申请';
  const link = document.createElement('link');
  link.rel = 'stylesheet';
  link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
  document.head.appendChild(link);
  console.log(this.globalData.cat_id);
})
}
,
data () {
  return{
    selectedFile: null
  }  
},
methods: {
  async Uploadappl() {
    if (!this.selectedFile) {
      alert("请选择申请表文件");
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    try {
        const response = await axios.post('http://localhost:3001/api/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        // let urll = response.data.toString()
        console.log(response.data)
        const res = await axios.post('http://localhost:8080/api/adoption-applications',
          {animalId: this.$globalData.cat_id, details: response.data.data}
        ).then(res=>{
        console.log(res)
        if(res.status == 200){
            this.$router.push('/home') // or wherever App.vue content should go
    }}).catch(error => {
        console.log(error);
        alert("失败！");
      });;
        this.selectedFile = null;
      } catch (error) {
        console.error('上传失败:', error);
      } 
      this.$router.push('/home')
  },
    async downloadFile(filename){
        const url = 'http://localhost:3001/api/upload/' + filename
        const response = await axios.get(url, {
          responseType: 'blob'
        });
        console.log(1);
        const blob = new Blob([response.data]);
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        link.remove();

  },
  triggerFileInput() {
    this.$refs.fileInput.click();
  },
  handleFileSelect(event) {
      const files = event.target.files;
      if (files && files.length > 0) {
        this.selectedFile = files[0];
        this.uploadStatus = null;
      }
      event.target.value = ''; // 重置input以便重复选择相同文件
  },
  async downnewFile() {
    const response = await axios.get('http://localhost:8080/api/adoption-guides');
    if(response.data.length == 0) {
      alert("尚未有材料");
    }
    else {
      let filename = response.data[0].title;
      this.downloadFile(filename);
    }
  },
  async upnewgud() {
    if (!this.selectedFile) return;
    const formData = new FormData();
    formData.append('file', this.selectedFile);

    const res = await axios.post('http://localhost:3001/api/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
    });

    const response = await axios.get('http://localhost:8080/api/adoption-guides');   
    
    if(response.data.length == 0) {
        await axios.post('http://localhost:8080/api/adoption-guides', {title: res.data.data, content:"1"});
    }
    else {
        let url = 'http://localhost:8080/api/adoption-guides/' + response.data[0].guideId;
        await axios.put(url, {title: res.data.data, content:"1"});
    }
    this.$router.push('/home');
  }

}

}


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');



.home-container {
  width: 100%;
  padding: 30px;
  background-image: url("../img/window.svg");
  background-size: cover;
}

.head-container {
  display: block;
  width: 100%;
  height: 10%;
  padding: 0;
}

.back-button {
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  border-radius: 10px;
  margin: 0px 0 0 20px;
  height: 40px;
  width: 100px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 25px;
}

.content {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 90%;
}

.op-button {
  background-color: #ff9600;
  color: #fff3d8;
  display: block;
  border: none;
  border-radius: 10px;
  width: 300px;
  height: 70px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 40px;
  padding: 10px 20px 10px 20px;
  margin: 20px 0  0 200px;
}

p {
  color: #ff9600;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
  margin: 20px 0 0 200px;
}

.text-container {
  float: left;
  display: block;
  height: 100%;
  padding: 0px 60px;
  color: #fff3d8;
}

.edit-application {
  float: left;
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  border-radius: 30px;
  height: 350px;
  width: 350px;
  padding: 5px 15px 5px 15px;
  margin: 35px 0 0 10px;
  font-size: 40px;
}
</style>