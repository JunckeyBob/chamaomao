<!--
  TODO:
  1. 从home获取cat的数据
  2. 显示cat的图片
  3. 对于supervisor角色，按照./template/cat/cat-su.html显示按钮，已经注释掉
  4. 将信息传递
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

    <!-- content container -->
    <div class="content-container">

      <!-- information container -->
      <div class="info-container">
        <!-- image -->
        <div class="image-container">
          <!-- simulation image -->
          <div class="image">
            <p class="image-title"><br>猫猫<br>图片</p>
          </div>
        </div>
        <!-- text -->
        <div class="text-container">
          <div class="name-container">
            <h1 class="name">{{ cat.name }}</h1>
            <p class="state">{{ cat.state }}</p>
          </div>
          <p class="info">
            品种：{{ cat.type }} <br><br>
            基本信息：已于2024-1-1绝育 <br><br>
            性格：活泼好动，喜欢玩耍 <br><br>
          </p>
        </div>
      </div>

    </div>

    <!-- button container -->
    <div class="button-container">
      <button class="op-button" style="margin-right: 50px" @click="this.$router.push('/adopt')"><i class="fa-solid fa-house"></i>&nbsp;领养</button>
      <!--<button class="op-button" @click="this.$router.push('/map')"><i class="fa-solid fa-location-dot"></i>&nbsp;定位</button>
      <button class="op-button"><i class="fa-solid fa-pen-to-square"></i>&nbsp;编辑</button>-->
    </div>

  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'Cat',
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
      document.title = '猫猫详情';
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
  methods: {

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
  padding-bottom: 50px;
}

.content-container {
  display: block;
  width: 100%;
  min-height: 65%;
  background-image: url("../img/stripes.svg");
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