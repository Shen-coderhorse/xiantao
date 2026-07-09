<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon :size="28"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount || 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon :size="28"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.productCount || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon :size="28"><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount || 0 }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon :size="28"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.totalAmount || '0.00' }}</div>
            <div class="stat-label">交易总额</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header><span>近7天交易趋势</span></template>
          <div ref="trendChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header><span>订单状态分布</span></template>
          <div ref="pieChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { Goods, List, User, Wallet } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import request from '@/utils/request'

const trendChartRef = ref(null)
const pieChartRef = ref(null)
const stats = ref({})

let trendChart = null
let pieChart = null

onMounted(() => {
  loadStats()
  loadTrend()
  window.addEventListener('resize', handleResize)
})

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

async function loadStats() {
  try {
    const res = await request.get('/admin/dashboard/stats')
    stats.value = res.data || {}

    // 初始化饼图
    if (pieChartRef.value && stats.value.orderStatusDist) {
      pieChart = echarts.init(pieChartRef.value)
      const dist = stats.value.orderStatusDist
      const pieData = [
        { name: '待付款', value: dist['待付款'] || 0 },
        { name: '待发货', value: dist['待发货'] || 0 },
        { name: '已完成', value: dist['已完成'] || 0 },
        { name: '已取消', value: dist['已取消'] || 0 }
      ].filter(item => item.value > 0)

      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 1 }],
          itemStyle: { borderRadius: 8 },
          label: { show: true, formatter: '{b}: {c}' }
        }],
        color: ['#e6a23c', '#409eff', '#67c23a', '#909399']
      })
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadTrend() {
  try {
    const res = await request.get('/admin/dashboard/trend')
    const data = res.data || {}

    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['交易额', '订单数'] },
        xAxis: {
          type: 'category',
          data: data.dates || []
        },
        yAxis: [
          { type: 'value', name: '交易额(元)' },
          { type: 'value', name: '订单数' }
        ],
        series: [
          {
            name: '交易额',
            type: 'line',
            data: data.amounts || [],
            smooth: true,
            areaStyle: { opacity: 0.3 }
          },
          {
            name: '订单数',
            type: 'bar',
            yAxisIndex: 1,
            data: data.counts || []
          }
        ],
        color: ['#409eff', '#67c23a']
      })
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

.chart-card {
  border-radius: 8px;
}
</style>
