<!-- 
  TODO:
  1. 添加handleLogin方法 //done
  2. 添加createAccount方法 // done
  3. 将数据传输给别的模块
-->
<template>
  <div class="home-container">
    <div class="main-container">

      <div class="login-container">
        <!-- name -->
        <div class="line-container">
          <div class="icon-container"><i class="fa-solid fa-user"></i></div>
          <input class="input-container" v-model="phone" type="tel"></input>
        </div>
        <!-- password -->
        <div class="line-container">
          <div class="icon-container"><i class="fa-solid fa-lock"></i></div>
          <input class="input-container" v-model="password" type="password"></input>
        </div>
        <!-- buttons -->
        <div class="line-container">
          <button @click="createAccount()">注册</button>
          <button @click="handleLogin()">登录</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import axios from 'axios';
axios.defaults.withCredentials = true; 


export default {
  name: 'Login',
  
  setup () {
    onMounted(() => {
      document.title = '登录 - 查毛毛';

      const link = document.createElement('link')
      link.rel = 'stylesheet'
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'
      document.head.appendChild(link)
    });
  },
  data() {
      return {
        phone: '',  // 初始化phone为空字符串
        password: ''
  }
  },
  methods: {
    handleLogin() {
      // Your login validation logic here
      // If login is successful:
      let url = 'http://localhost:8080/api/users/login'
      axios.post(url, {phoneNumber:this.phone, password:this.password},
{headers: { responseType: 'json'}}
      ).then(res=>{
        console.log(res)
        if(res.status == 200){
            console.log(res.data)
            this.$router.push('/home') // or wherever App.vue content should go

    }}).catch(error => {
        console.error("There was an error!", error);
      });
      //this.$router.push('/home') // or wherever App.vue content should go
     
    },
    createAccount () {
      let url = 'http://localhost:8080/api/users/register'
      axios.post(url, {phoneNumber:this.phone, password:this.password, nickname: "admin"}).then(res=>{
        console.log(res.data)
    }
  )
  }
}
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');


.home-container {
  width: 100%;
  height: 100%;
  display: block;
  background-image: url("./img/white-stripes.svg");
  background-size: cover;
}

.main-container {
  display: flex;
  background-image: url("./img/house.svg");
  background-size: cover;
  width: 50%;
  margin-left: 25%;
  min-height: 80%;
}

.login-container {
  display: block;
  color: #fff3d8;
  margin-top: 220px;
  margin-left: 160px;
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