<!--
  TODO: 
  1. 完成地图组件的功能，实现定位、上传，和查看的功能(这里比较推荐使用AMAP的API，谷歌需要梯子)
  2. 管理员有查看权限，点击添加也有添加的功能，添加界面和普通用户一样 (从home获取管理员信息)； 查看地图的UI位于./template/map/map-su.html
  3. 普通用户只有添加权限
-->

<template>
  <div class="home-container">


    <!-- head with back button -->
    <div class="head-container">
      <!-- back button -->
      <button class="back-button">
        <i class="fa-solid fa-arrow-left"></i>&nbsp;返回
      </button>
    </div>

    <!-- content container -->
    <div class="content-container">

      <!-- map container -->
      <div class="map-container">

        <!-- upload image -->
        <button class="upload-button">
          <p><i class="fa-solid fa-plus"></i></p>
        </button>
        <!-- upload to map -->
        <button class="confirm-button"><p>上<br>传</p></button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'Map',
  setup () {
    var user = ref({
      name: '用户名字',
      ID: '11111111',
      role: 'supervisor'
    });

    onMounted(() => {
      document.title = '地图 - 查毛毛';
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
    })
  }

}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');


.home-container {
  width: 100%;
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
  min-height: 65%;
  background-image: url("../img/stripes.svg");
  background-size: cover;
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

.map-container {
  display: block;
  width: 80%;
  min-height: 600px;
  margin-left: 7%;
  margin-top: 0px;
  border: none;
  border-radius: 30px;
  background-color: #fff3d8;
  color: #ff9600;
  padding: 30px 30px 0 30px;
}

.title-container {
  display: block;
  border: none;
  width: 100%;
}

.title {
  color: #ff9600;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 50px;
  float: left;
}

.add-button {
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 10px;
  width: 100px;
  height: 50px;
  align-content: center;
  float: right;
  font-size: 30px;
  margin-top: 40px;
}

.iframe-container {
  display: block;
  width: 100%;
  height: 300px;
}

.upload-button {
  display: block;
  width: 80%;
  height: 560px;
  background-color: #ffd885;
  color: #fff3d8;
  border: none;
  border-radius: 30px 0 0 30px;
  float: left;
}

.upload-button p {
  font-size: 200px;
  font-weight: 700;
}

.confirm-button {
  display: block;
  width: 20%;
  height: 560px;
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 0 30px 30px 0;
  float: left;
}

.confirm-button p {
  font-size: 100px;
  font-weight: 700;
  font-family: "ZCOOL KuaiLe", sans-serif;
}
</style>