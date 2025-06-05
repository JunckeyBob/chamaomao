<!-- 
  TODO!!!:
  1. 从home获取用户的基本信息
  2. 从后端获取所有的申请
  3. 在点击“已处理”按钮的时候，显示已经处理的申请，所有已经处理的申请的模板在./template/application/pending.html中，对于supervisor，模板在./template/application/pending-su.html中
  4. 在点击“未处理”按钮的时候，显示未处理的申请，所有未处理的申请的模板在./template/application/application.html中，对于supervisor，模板在./template/application/application-su.html中
  5. 点击撤回按钮，撤回申请
  6. 对于supervisor，点击查看按钮，下载申请表
  7. 对于supervisor，点击拒绝或接受，更新申请状态
  8. 具体的要看后端的实现
 -->

<template>
  <!-- main content -->
  <div class="home-container">
    <!-- head with back button -->
    <div class="head-container">
      <button class="left-button" @click="this.num = 1;updated()">
        未处理
      </button>
      <button class="right-button" @click="this.num = 2;updated()">
        已处理
      </button>
    </div>

    <!-- content container -->
    <div class="content-container">
      <!-- applications -->
      <div class="application"         
        v-for="cat in names" 
         >
        <p style="margin-left: 20px; float: left">对领养</p>
        <p style="margin-left: 5px; color: #ffc23f; float: left">{{cat[1]}}</p>
        <p style="margin-left: 5px; float: left">的申请</p>
        <p style="margin-left: 400px; float: left; color: #ffc23f"></p>
        <button v-if="this.num==1 && this.$globalData.user_id==2 " class="delete-button" @click="undoapl(cat[0].adoptionId)">撤回</button>

        <button v-if="this.num==1 && this.$globalData.user_id==1 " class="check-button" @click="downloadFile(cat[0].details)"><i class="fa-solid fa-download"></i></button>

        <button v-if="this.num==1 && this.$globalData.user_id==1 " class="check-button" @click="approve(cat[0].adoptionId)"><i class="fa-solid fa-check"></i></button>

         <button v-if="this.num==1 && this.$globalData.user_id==1 " class="check-button" @click="reject(cat[0].adoptionId)"><i class="fa-solid fa-xmark"></i></button>

        <p v-if="this.num==2 && cat[0].status=='REJECTED'" style="margin-right: 40px; float: right; color: #ffc23f">领养失败</p>
        <p v-if="this.num==2 && cat[0].status=='APPROVED'" style="margin-right: 40px; float: right; color: #ffc23f">领养成功</p>
      </div>

    </div>
  </div>

</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
axios.defaults.withCredentials = true; 

export default {
  name: 'Application',
  setup () {
    onMounted(() => {
      document.title = '申请列表';
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
    });
  },
  data () {
    return {
      apls: [],
      names: [],
      num : 1
    }
  },
  mounted() {
    console.log(this.num);
    this.updated();
  },
  methods: {
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
      async updated() {
        console.log(this.$globalData.user_id)
        if(this.$globalData.user_id == 2){
            this.fetchData();
        }
        else this.fetchDataall();
      },
      async fetchDataall() {
          let url = 'http://localhost:8080/api/adoption-applications/admin/all';
          const response = await axios.get(url);
          this.apls = response.data;
          this.deal()       
      },
      async fetchData() {
        try {
          const response = await axios.get('http://localhost:8080/api/adoption-applications/my-applications');
          this.apls = response.data;
          console.log(this.apls);
        } catch (error) {
          console.error('获取数据失败:', error);
        }
        this.deal()
    },
      async deal(){
      this.names = []
      for(let c of this.apls){
        if(c.status == 'PENDING_REVIEW' && this.num ==1 || (c.status == 'APPROVED' || c.status == 'REJECTED')&& this.num ==2 ) {
        try {
          let url = "http://localhost:8080/api/animals/" + c.animalId
          const nam = await axios.get(url);
          this.names.push([c, nam.data.name])
        }
      catch (error) {
        console.error('获取数据失败:', error);

    }}}
  },
      async undoapl(id){
       console.log(id)
        try {
          let url = "http://localhost:8080/api/adoption-applications/"+id+"/cancel"
          await axios.put(url);
      } catch (error) {
        console.error('删除失败:', error);
      }
      this.fetchData();
      console.log(this.names)
  },
    async approve(id) {
      let url = "http://localhost:8080/api/adoption-applications/"+id+"/review"
      try{
        await axios.put(url, {status: "APPROVED", reviewDetails: ""});
      }catch (error) {
        console.error('批准失败:', error);
      }
      this.updated();
    },
    async reject(id) {
      let url = "http://localhost:8080/api/adoption-applications/"+id+"/review"
      try{
        await axios.put(url, {status: "REJECTED", reviewDetails: ""});
      }catch (error) {
        console.error('拒绝失败:', error);
      }
      this.updated();
    },

    alertt(str){
      alert(str);
    }


}}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cherry+Bomb+One&family=Kiwi+Maru&family=Noto+Sans+SC:wght@100..900&family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&family=ZCOOL+KuaiLe&display=swap');



.home-container {
  width: 100%;
  height: 100%;
  background-color: #fff3d8;
}

.head-container {
  display: block;
  width: 100%;
  height: 50px;
  padding-top: 20px;
  background-color: #ffc23f;
}

.content-container {
  display: block;
  width: 100%;
  min-height: 65%;
}

.left-button {
  float: left;
  background-color: #fff3d8;
  color: #ffc23f;
  border: none;
  border-radius: 15px 15px 0 0;
  align-content: center;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
  margin-left: 4%;
  margin-right: 2%;
  margin-top: 4px;
  height: 46px;
  width: 44%;
}

.right-button {
  float: left;
  color: #fff3d8;
  background-color: #ffc23f;
  border: none;
  border-radius: 15px 15px 0 0;
  align-content: center;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
  margin-left: 2%;
  margin-right: 4%;
  margin-top: 4px;
  height: 46px;
  width: 44%;
}

.application {
  color: #ff9600;
  margin-left: 4%;
  margin-right: 4%;
  margin-top: 35px;
  width: 92%;
  height: 100px;
  border-color: #ff9600;
  border-width: 5px;
  border-radius: 15px;
  border-style: dotted;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
}

.delete-button {
  float: right;
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 15px;
  align-content: center;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
  margin-right: 20px;
  margin-top: 25px;
  height: 50px;
  width: 100px;
}

.check-button {
  float: right;
  background-color: #ff9600;
  color: #fff3d8;
  border: none;
  border-radius: 15px;
  align-content: center;
  font-family: "ZCOOL KuaiLe", sans-serif;
  font-size: 30px;
  margin-right: 20px;
  margin-top: 25px;
  height: 50px;
  width: 50px;
}

</style>