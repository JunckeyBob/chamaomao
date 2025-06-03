<!-- 
  TODO:
  1. 从后端获取所有的猫猫数据
  2. 从登录页面获得用户信息（还没做好
  3. 当用户点击筛选的时候，显示筛选卡片(filter card, already commented out)
  4. 根据筛选的条件，筛选出符合条件的猫猫
  5. 再次点击筛选按钮，隐藏筛选卡片
  6. 点击查看按钮，跳转到猫猫详情页面（还没做好
  7. 点击添加按钮，跳转到添加猫猫页面（还没做好）
  done by QB 8. 点击申请按钮，跳转到申请页面（还没做好 
  9. 有没有阅读的申请时，采用alert按钮（alert, already commented out
  done by QB 10. 点击我的按钮，跳转到个人信息页面（还没做好 
  11. 点击地图按钮，跳转到地图页面（还没做好
  12. 修改页面，调整比例
-->

<template>
  <div class="home-container">
    <!-- head container -->
    <div class="head-container">
      <!-- title container -->
      <div class="title-container">
        <p class="title">查毛毛 <i class="fa-solid fa-paw"></i></p>
      </div>

      <!-- button container -->
      <div class="button-container">
        <button 
          class="home-button" 
          style="margin-right: 35px"
          @click="toProfile()"
        >
          <i class="fa-solid fa-circle-user"></i><br>我的
        </button>
        <button 
          class="home-button"
          @click="this.$router.push('/application')"
        >
          <i class="fa-solid fa-file"></i><br>申请
        </button>
        <!-- Alert -->
        <!--<button class="home-button"><i class="fa-solid fa-circle-exclamation" style="color: red"></i><br>申请</button>-->
        <button 
          class="home-button"
          @click="this.$router.push('/map')"
        >
          <i class="fa-solid fa-location-dot"></i><br>地图
        </button>
        <button 
          class="home-button"
          @click=""
        >
          <i class="fa-solid fa-filter"></i><br>筛选
        </button>
      </div>
    </div>

    <!-- card container -->
    <div class="card-container">
      <!-- filter card -->
      <!--<div class="filter-card">
      <div class="filter-card">
        <div class="line-container">
          <div class="attr-container">品种：</div>
          <button class="select-button">橘猫 / 橘白</button>
          <button class="select-button">玳瑁 / 三花</button>
          <button class="select-button" style="background-color: #ffc23f; color: #fff3d8">狸猫 / 狸白</button>
          <button class="select-button">奶牛</button>
          <button class="select-button">纯色</button>
          <button class="select-button">品种猫</button>
        </div>

        <div class="line-container">
          <div class="attr-container">状态：</div>
          <button class="select-button">已领养</button>
          <button class="select-button" style="background-color: #ffc23f; color: #fff3d8">待领养</button>
          <button class="select-button">留园观察</button>
          <button class="select-button">失踪</button>
        </div>
      </div>  
      </div>-->

      <!-- card list -->
      <div 
        v-for="cat in cats" 
        :key="cat.id" 
        class="card"
      >
        <!-- simulate cat image -->
        <div class="cat-image">
          <p style="margin-top: 20px; margin-left: 23px">猫猫<br>图片</p>
        </div>
        <!-- simulate cat name -->
        <p class="cat-name">{{ cat.name }}</p>
        <!-- look up button -->
        <button 
          class="look-button"
          @click="this.$router.push('/cat')"
        >
          <i class="fa-solid fa-magnifying-glass"></i> &nbsp;查看
        </button>
      </div>

      <!-- add button -->
      <div v-if="user.role === 'supervisor'" class="card">
        <button class="add-button">
          <i class="fa-solid fa-plus"></i>
        </button> 
      </div>


    </div>

  </div>
</template>

<script>
import { ref } from 'vue'
import { onMounted } from 'vue'


export default {
  name: 'Home',
  setup() {
    // Reactive data
    var cats = ref([
      { id: 1, name: '猫猫名字1', img: '', type: '', state: '' },
      { id: 2, name: '猫猫名字2', img: '', type: '', state: '' }
    ])
    var user = ref({
      name: '用户名字',
      ID: '11111111',
      role: 'supervisor'
    })

    

    onMounted(() => {
      // Change title
      document.title = '查毛毛 - 浙江大学宠物领养系统'
      
      // Add meta tags
      const meta = document.createElement('meta')
      meta.name = 'description'
      meta.content = '浙江大学宠物领养系统'
      document.head.appendChild(meta)
      
      // Add external stylesheets
      const link = document.createElement('link')
      link.rel = 'stylesheet'
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css'
      document.head.appendChild(link)
    })


    return {
      cats,
      user
    }
  }, 
  methods: {
    toProfile () {
      this.$router.push('/profile')
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
  min-width: 100%;
  min-height: 25%;
  padding-top: 30px;
}

.title-container {
  float: left;
  height: 100%;
  min-width: 20%;
}

.button-container {
  float: right;
  height: 100%;
  min-width: 20%;
}


.title {
  float: left;
  color: #fff3d8;
  font-weight: bold;
  font-size: 4em;
  margin-left: 60px;
  margin-top: 0;
}

.logo {
  float: left;
  width: 100px;
  height: 50px;
}

.home-button {
  float: right;
  background-color: #ffc23f;
  border: none;
  color: #fff3d8;
  font-size: 1.8em;
  font-family: "ZCOOL KuaiLe", sans-serif;
  margin-right: 15px;
  margin-top: 10px;
}

.card-container {
  display: block;
  min-width: 100%;
  min-height: 80%;
  background-image: url("img/stripes.svg");
  background-size: cover; 
  padding-bottom: 30px;
}

.filter-card {
  display: block;
  width: 1230px;
  height: 165px;
  background-color: #fff3d8;
  border-radius: 20px;
  border: none;
  margin-bottom: 30px;
  margin-left: 60px;
  padding: 20px;
}

.card {
  display: block;
  width: 90%;
  height: 150px;
  background-color: #fff3d8;
  border-radius: 20px;
  border: none;
  margin-bottom: 30px;
  margin-left: 5%;
}

.cat-image {
  float: left;
  width: 110px;
  height: 110px;
  margin-top: 20px;
  margin-left: 20px;
  border-radius: 20px;
  background-color: #ffc23f;
  color: #fff3d8;
  font-size: 2em;
}

.cat-name {
  float: left;
  margin-top: 45px;
  margin-left: 25px;
  font-size: 3em;
  color: #ffc23f;
}

.look-button {
  float: right;
  background-color: #ff9600;
  height: 110px;
  width: 200px;
  border-radius: 20px;
  border: none;
  color: #fff3d8;
  font-size: 2.5em;
  font-family: "ZCOOL KuaiLe", sans-serif;
  margin-right: 20px;
  margin-top: 20px;
}

.add-button {
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  font-size: 110px;
  margin-left: 560px;
  margin-top: 15px;
}

.line-container {
  display: block;
  width: 100%;
  height: 50px;
  color: #ffc23f;
  font-size: 40px;
  margin: 20px 0 0 20px;
}

.attr-container {
  float: left;
  width: 130px;
  height: 50px;
}

.select-button {
  float: left;
  width: auto;
  height: 50px;
  background-color: #fff3d8;
  border: none;
  border-radius: 10px;
  color: #ffc23f;
  padding: 5px;
  font-size: 30px;
  font-family: "ZCOOL KuaiLe", sans-serif;
  margin-left: 20px;
}
</style>