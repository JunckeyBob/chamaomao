

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
          @click="handleButtonClick('profile')"
        >
          <i class="fa-solid fa-circle-user"></i><br>我的
        </button>
        <button 
          class="home-button"
          @click="handleButtonClick('apply')"
        >
          <i class="fa-solid fa-file"></i><br>申请
        </button>
        <button 
          class="home-button"
          @click="handleButtonClick('map')"
        >
          <i class="fa-solid fa-location-dot"></i><br>地图
        </button>
        <button 
          class="home-button"
          @click="handleButtonClick('filter')"
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
          @click="viewCat(cat.id)"
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
  name: 'App',
  setup() {
    // Reactive data
    const cats = ref([
      { id: 1, name: '猫猫名字1', img: '', type: '', state: '' },
      { id: 2, name: '猫猫名字2', img: '', type: '', state: '' }
    ])
    const user = ref({
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

    // Methods
    const handleButtonClick = (buttonType) => {
      console.log(`Clicked ${buttonType} button`)
      // Add your navigation logic here
      switch(buttonType) {
        case 'profile':
          // Handle profile navigation
          break
        case 'apply':
          // Handle application navigation
          break
        case 'map':
          // Handle map navigation
          break
        case 'filter':
          // Handle filter logic
          break
      }
    }

    const viewCat = (catId) => {
      console.log(`Viewing cat with ID: ${catId}`)
      // Add navigation to cat detail page
    }

    return {
      cats,
      user,
      handleButtonClick,
      viewCat
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');


.home-container {
  width: 100%;
  height: 100%;
}

.head-container {
  display: block;
  min-width: 100%;
  min-height: 20%;
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
  width: 1270px;
  height: 150px;
  background-color: #fff3d8;
  border-radius: 20px;
  border: none;
  margin-bottom: 30px;
  margin-left: 60px;
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
  margin-left: 570px;
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