<script setup>
import { ref, onMounted } from 'vue'
import { TrendCharts, Search } from '@element-plus/icons-vue'
import { getMonthlyStatistics } from '@/api/statistics'
import * as echarts from 'echarts'

const loading = ref(false)
const year = ref(new Date().getFullYear())
const startMonth = ref(1)
const endMonth = ref(12)

const chartRef = ref(null)
let chartInstance = null

const monthOptions = Array.from({ length: 12 }, (_, i) => ({
  label: `${i + 1}月`,
  value: i + 1
}))

async function fetchData() {
  try {
    loading.value = true
    const data = await getMonthlyStatistics({
      year: year.value,
      start_month: startMonth.value,
      end_month: endMonth.value
    })
    updateChart(data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

function updateChart(data) {
  if (!chartInstance) return

  const months = []
  const salesData = []
  const profitData = []

  for (let i = startMonth.value; i <= endMonth.value; i++) {
    months.push(`${i}月`)
    const item = data?.find(d => d.month === i) || {}
    salesData.push(item.salesAmount || 0)
    profitData.push(item.profit || 0)
  }

  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: '#ffffff',
      borderColor: 'rgba(0,0,0,0.08)',
      textStyle: { color: '#334155' }
    },
    legend: {
      data: ['销售额', '利润'],
      top: 10,
      textStyle: { color: '#64748b' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: 'rgba(0,0,0,0.1)' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        nameTextStyle: { color: '#64748b' },
        axisLabel: { formatter: '¥{value}', color: '#64748b' },
        splitLine: { lineStyle: { color: 'rgba(0,0,0,0.06)' } }
      },
      {
        type: 'value',
        name: '利润(元)',
        nameTextStyle: { color: '#64748b' },
        axisLabel: { formatter: '¥{value}', color: '#64748b' },
        splitLine: { lineStyle: { color: 'rgba(0,0,0,0.06)' } }
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: salesData,
        barWidth: '35%',
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#60a5fa' },
            { offset: 1, color: '#3b82f6' }
          ])
        }
      },
      {
        name: '利润',
        type: 'line',
        yAxisIndex: 1,
        data: profitData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#4ade80', width: 3 },
        itemStyle: { color: '#4ade80' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(74, 222, 128, 0.25)' },
            { offset: 1, color: 'rgba(74, 222, 128, 0.02)' }
          ])
        }
      }
    ]
  })
}

function initChart() {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
    fetchData()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})
</script>

<template>
  <div class="statistics-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><TrendCharts /></el-icon>
        统计报表
      </h2>
    </div>

    <div class="filter-section glass-panel">
      <el-form inline>
        <el-form-item label="年份">
          <el-date-picker
            v-model="year"
            type="year"
            placeholder="选择年份"
            value-format="YYYY"
            @change="fetchData"
          />
        </el-form-item>
        <el-form-item label="月份范围">
          <el-select v-model="startMonth" placeholder="开始月份" style="width: 100px">
            <el-option
              v-for="item in monthOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <span style="margin: 0 8px; color: var(--text-secondary);">-</span>
          <el-select v-model="endMonth" placeholder="结束月份" style="width: 100px">
            <el-option
              v-for="item in monthOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchData">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="chart-section glass-panel" v-loading="loading">
      <div ref="chartRef" class="chart-container"></div>
    </div>
  </div>
</template>

<style scoped>
.statistics-page {
  width: 100%;
}

.chart-section {
  min-height: 450px;
}
</style>
