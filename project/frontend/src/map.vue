<!--
  TODO: 
  1. 完成地图组件的功能，实现定位、上传，和查看的功能(这里比较推荐使用AMAP的API，谷歌需要梯子)
  2. 管理员有查看权限，点击添加也有添加的功能，添加界面和普通用户一样 (从home获取管理员信息)； 查看地图的UI位于./template/map/map-su.html
  3. 普通用户只有添加权限
-->
<template>
<template v-if="this.$globalData.user_id==2">
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

      <!-- map container -->
      <div class="map-container">
        <div id="map-container" style="display: none;"></div>

        <!-- upload image -->
        <button class="upload-button">
          <p><i class="fa-solid fa-plus"></i></p>
        </button>
        <!-- upload to map -->
        <button @click="getCurrentLocation" class="confirm-button"><p>上<br>传</p></button>
      </div>
    </div>
  </div>
</template>

<template v-if="this.$globalData.user_id==1">
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

      <!-- map container -->
      <div class="map-container">

        <div id="map-container" class="cover" ></div>

        <!-- upload image -->
        <button class="upload-button">
          <p><i class="fa-solid fa-plus"></i></p>
        </button>
        <!-- upload to map -->
        <button @click="getCurrentLocation" class="confirm-button"><p>上<br>传</p></button>
        <button @click="look" class="confirm-button"><p>查<br>看</p></button>
      </div>
    </div>
  </div>
</template>
</template>

<script>
import { ref, onMounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import axios from 'axios'
axios.defaults.withCredentials = true;
import { getCurrentInstance } from 'vue'

export default {
  name: 'AmapLocation',
  setup() {
    const address = ref('')
    const position = ref('')
    const city = ref('')
    let map = null
    let geocoder = null

    const { proxy } = getCurrentInstance()

    
    // 初始化地图
    const initMap = async () => {
      try {
        console.log(1)
        AMapLoader.reset()
        const AMap = await AMapLoader.load({
          key: '161a23575eda543abf02732874965b79', // 替换为你的实际Key
          version: '2.0', // 指定要加载的 JSAPI 的版本
          plugins: ['AMap.Geolocation', 'AMap.Geocoder'] // 需要使用的的插件列表
        })
        
        map = new AMap.Map('map-container', {
          zoom: 15, // 初始化地图级别
          resizeEnable: true
        })
        
        // 初始化地理编码器
        geocoder = new AMap.Geocoder({
          city: '全国' // 城市，默认："全国"
        })
      } catch (error) {
        console.error('地图加载失败:', error)
      }
    }

    // 获取当前位置
    const getCurrentLocation = async () => {
      if (!map) return
      
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true, // 是否使用高精度定位，默认:true
        timeout: 10000, // 超过10秒后停止定位，默认：5s
        buttonPosition: 'RB', // 定位按钮停靠位置
        buttonOffset: new AMap.Pixel(10, 20), // 定位按钮与设置的停靠位置的偏移量
        zoomToAccuracy: true // 定位成功后是否自动调整地图视野到定位点
      })

      console.log("aaaaaaaaaaa")

      
      map.addControl(geolocation)


      console.log(geolocation)
      
      geolocation.getCurrentPosition((status, result) => {
        if (status === 'complete') {
          onComplete(result)
        } else {
          onError(result)
        }
      })
    }

    const look =  async () => {
        let url = 'http://localhost:8080/api/animals/' + proxy.$globalData.cat_id +'/locations'
        const response = await axios.get(url);
        const cc = response.data
        console.log(cc)
        for(let c of cc) {
            const marker = new AMap.Marker({
                position: [c.longitude, c.latitude],
                map: map
      })
        }

    }

    // 定位成功回调
    const onComplete = (data) => {
      const { position: pos, addressComponent, formattedAddress } = data
      
      position.value = `${pos.lng}, ${pos.lat}`
      console.log(pos);
      let url = 'http://localhost:8080/api/animals/' + proxy.$globalData.cat_id +'/locations'

      axios.post(url,{latitude: pos.lat, longitude: pos.lng}).catch(error => {
        console.error("There was an error!", error);
      });


      // address.value = formattedAddress
      // city.value = addressComponent.city || addressComponent.province
      
      // 在地图上显示标记
      const marker = new AMap.Marker({
        position: [pos.lng, pos.lat],
        map: map
      })
      
      map.setCenter([pos.lng, pos.lat])
    }

    // 定位失败回调
    const onError = (error) => {
      console.error('定位失败:', error)
      alert('获取位置失败，请确保已开启定位权限')
    }

    onMounted(() => {
      document.title = '地图 - 查毛毛';
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
      initMap();

    })

    // 初始化地图

    return {
      address,
      position,
      city,
      getCurrentLocation,
      look
    }
  }
}



// import { ref, onMounted } from 'vue';

// export default {
//   name: 'Map',
//   setup () {
//     var user = ref({
//       name: '用户名字',
//       ID: '11111111',
//       role: 'supervisor'
//     });

//     onMounted(() => {
//       document.title = '地图 - 查毛毛';
//       const link = document.createElement('link');
//       link.rel = 'stylesheet';
//       link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
//       document.head.appendChild(link);
//     })
//   }

// }
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
.cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 50%;
  z-index: 1000; /* 确保覆盖其他元素 */
}
</style>