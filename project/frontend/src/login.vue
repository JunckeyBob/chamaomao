<!-- 
  TODO:
  1. 添加handleLogin方法
-->
<template>
  <div class="home-container">
    <div class="main-container">

      <div class="login-container">
        <!-- name -->
        <div class="line-container">
          <div class="icon-container"><i class="fa-solid fa-user"></i></div>
          <div class="input-container">吴秋彼</div>
        </div>
        <!-- password -->
        <div class="line-container">
          <div class="icon-container"><i class="fa-solid fa-lock"></i></div>
          <div class="input-container">********</div>
        </div>
        <!-- buttons -->
        <div class="line-container">
          <button>注册</button>
          <button>登录</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'Login',
  methods: {
    handleLogin() {
      // Your login validation logic here
      // If login is successful:
      this.$router.push('/home') // or wherever App.vue content should go
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');

body, html {
  margin: 0;
  font-family: "ZCOOL KuaiLe", sans-serif;
  display: flex;
  height: 100%;
  width: 100%;
  background-color: #ffc23f;
}

.home-container {
  width: 100%;
  background-image: url("./img/white-stripes.svg");
  background-size: cover;
}

.main-container {
  display: flex;
  background-image: url("./img/house.svg");
  background-size: cover;
  width: 40%;
  margin-left: 25%;
  min-height: 80%;
}

.login-container {
  display: block;
  color: #fff3d8;
  margin-top: 250px;
  margin-left: 175px;
}

.line-container {
  display: flex;
  height: 50px;
  font-size: 40px;
  margin-top: 25px;
}

.icon-container {
  display: block;
  width: 70px;
  height: 100%;
  float: left;
}

.input-container {
  display: block;
  float: left;
  width: 200px;
  background-color: #fff3d8;
  height: 40px;
  padding: 5px 10px;
  color: #833c0e;
  font-size: 30px;
  border: none;
  border-radius: 5px;
  text-align: left;
}

button {
  display: block;
  float: left;
  width: 130px;
  margin-right: 30px;
  margin-top: 10px;
  height: 50px;
  background-color: #ffc23f;
  color: #833c0e;
  font-size: 30px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  padding: 8px;
  border: none;
  border-radius: 5px;
}
</style>