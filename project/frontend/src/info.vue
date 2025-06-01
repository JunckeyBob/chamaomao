<!--
  TODO:
  1. 从home获取用户信息
  2. 修改密码
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

    <!-- title container -->
    <div class="title-container">
      <p style="float: left">吴秋彼</p>
      <!--<p style="float: left; font-size: 30px; margin-left: 0; color: #ff9600">管理员</p>-->
    </div>

    <!-- info container -->
    <div class="info-container">
      <div class="line-container"></div>
      <div class="line-container">
        <div class="text-container">ID: </div>
        <div class="input-container" style="background-color: #ffc23f; color: #fff3d8">12345678</div>
      </div>
      <div class="line-container">
        <div class="text-container">旧密码：</div>
        <div class="input-container">******</div>
      </div>
      <div class="line-container">
        <div class="text-container">新密码：</div>
        <div class="input-container">*********</div>
      </div>
      <div class="line-container">
        <div class="text-container">确认密码：</div>
        <div class="input-container">*********</div>
      </div>
      <div class="line-container">
        <button class="confirm-button">修改</button>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';

export default {
  name: 'Profile',

  setup () {
    var user = ref({
      name: 'QB',
      ID: '111111',
      role: 'supervisor'
    })
  },
  methods: {

  }
}

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');


.home-container {
  width: 100%;
  padding: 30px;
  background-image: url("./img/window.svg");
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

.title-container {
  display: block;
  width: 100%;
  height: 70px;
  color: #fff3d8;
  font-weight: bold;
  font-size: 60px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  padding: 0;
  margin: 0;
}

.title-container p {
  margin: 30px 30px 30px 60px;
}

.info-container {
  display: block;
  width: 100%;
  min-height: 500px;
  color: #fff3d8;
  font-size: 40px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  padding: 0;
}

.line-container {
  display: block;
  width: 100%;
  height: 60px;
  color: #fff3d8;
  font-size: 40px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  margin: 0 0 20px 0 ;
  padding: 0px ;
}

.text-container {
  display: block;
  float: left;
  width: 200px;
  height: 60px;
  font-size: 40px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  margin: 0 0 0 60px ;
  padding: 0px ;
}


.input-container {
  display: block;
  float: left;
  width: 400px;
  height: 50px;
  font-size: 30px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  border: none;
  border-radius: 5px;
  color: #ffc23f;
  background-color: #fff3d8;
  padding: 10px 0 0 20px;
}

.confirm-button {
  display: block;
  width: 160px;
  height: 60px;
  padding: 10px 20px 10px 20px;
  font-size: 30px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  border: none;
  border-radius: 5px;
  background-color: #ff9600;
  color: #fff3d8;
  margin: 30px 0 0 60px;
  text-align: center;
}
</style>