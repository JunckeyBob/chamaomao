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

    <div class="content">
      <p>1. 先下载领养材料和申请表模板</p><br>
      <button class="op-button"><i class="fa-solid fa-download"></i>  下载材料</button><br>
      <p>2. 填写表单后上传</p>
      <button class="op-button"><i class="fa-solid fa-upload"></i>  上传申请表</button><br>
      <p>3. 确认提交</p>
      <button class="op-button"><i class="fa-solid fa-paper-plane"></i>  提交申请</button><br>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
export default {
  name: 'Adopt',
  setup () {
    var user = ref({
      name: '用户名字',
      ID: '11111111',
      role: 'supervisor'
    });

    var cat = ref({
      id: 1,
      name: '猫猫名字',
      img: '',
      type: '',
      state: ''
    });

    onMounted(() => {
      document.title = '领养申请';
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
    });
    return {
      user,
      cat
    };
  },
  methods: {

  }
};
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
</style>