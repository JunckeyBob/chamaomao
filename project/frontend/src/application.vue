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
      <button class="left-button">
        未处理
      </button>
      <button class="right-button">
        已处理
      </button>
    </div>

    <!-- content container -->
    <div class="content-container">
      <!-- applications -->
      <div class="application">
        <p style="margin-left: 20px; float: left">对领养</p>
        <p style="margin-left: 5px; color: #ffc23f; float: left">猫猫名字1</p>
        <p style="margin-left: 5px; float: left">的申请</p>
        <p style="margin-left: 400px; float: left; color: #ffc23f">2023-10-01</p>
        <button class="delete-button">撤回</button>
      </div>

      <div class="application">
        <p style="margin-left: 20px; float: left">对领养</p>
        <p style="margin-left: 5px; color: #ffc23f; float: left">猫猫名字2</p>
        <p style="margin-left: 5px; float: left">的申请</p>
        <p style="margin-left: 400px; float: left; color: #ffc23f">2024-2-3</p>
        <button class="delete-button">撤回</button>
      </div>
    </div>
  </div>

</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'Application',
  setup () {
    var user = ref({
      name: '用户名字',
      ID: '11111111',
      role: 'supervisor'
    });
    var applications = ref([
      { id: 1, catId: 1, cat: '猫猫名字1', state: '已处理', date: '2024-2-1' }, 
      { id: 2, catId: 2, cat: '猫猫名字2', state: '待处理', date: '2024-2-2' }
    ]);
    var done = ref(false);






    onMounted(() => {
      document.title = '申请列表';
      const link = document.createElement('link');
      link.rel = 'stylesheet';
      link.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css';
      document.head.appendChild(link);
    });

    return {
      user,
      applications,
    };
  },
  methods: {
    queryApplications () {
      // 
    }
  }
}
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