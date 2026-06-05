<script setup>
import { ref, onMounted } from 'vue'
import { User, Refresh, ArrowRight, TrendCharts, Notebook, ShoppingCart } from '@element-plus/icons-vue'
import { getDashboardData, getRankingList } from '@/api/statistics'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const dashboardData = ref({
  bookCount: 0,
  customerCount: 0,
  todaySales: 0,
  monthProfit: 0
})

const rankingList = ref([])
const loading = ref(false)

const statCards = [
  {
    title: '图书总数',
    value: 'bookCount',
    icon: Notebook,
    color: '#60a5fa',
    gradient: 'linear-gradient(135deg, rgba(96,165,250,0.2) 0%, rgba(147,197,253,0.1) 100%)',
    border: 'rgba(96,165,250,0.3)'
  },
  {
    title: '客户总数',
    value: 'customerCount',
    icon: User,
    color: '#38bdf8',
    gradient: 'linear-gradient(135deg, rgba(56,189,248,0.2) 0%, rgba(125,211,252,0.1) 100%)',
    border: 'rgba(56,189,248,0.3)'
  },
  {
    title: '今日销售',
    value: 'todaySales',
    icon: ShoppingCart,
    color: '#fbbf24',
    gradient: 'linear-gradient(135deg, rgba(251,191,36,0.2) 0%, rgba(252,211,77,0.1) 100%)',
    border: 'rgba(251,191,36,0.3)',
    prefix: '¥'
  },
  {
    title: '本月利润',
    value: 'monthProfit',
    icon: TrendCharts,
    color: '#4ade80',
    gradient: 'linear-gradient(135deg, rgba(74,222,128,0.2) 0%, rgba(134,239,172,0.1) 100%)',
    border: 'rgba(74,222,128,0.3)',
    prefix: '¥'
  }
]

const rankIcons = ['\u{1f947}', '\u{1f948}', '\u{1f949}']

async function fetchData() {
  try {
    loading.value = true
    const [dashboard, ranking] = await Promise.all([
      getDashboardData(),
      getRankingList({ limit: 5 })
    ])
    dashboardData.value = dashboard || dashboardData.value
    rankingList.value = ranking || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

function goToStatistics() {
  router.push('/statistics')
}

function formatValue(card) {
  const val = dashboardData.value[card.value]
  if (card.prefix) {
    return `${card.prefix}${val?.toLocaleString() || 0}`
  }
  return val || 0
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="dashboard-container">
    <header class="welcome-header">
      <div class="welcome-content">
        <div class="avatar-wrapper">
          <el-icon class="avatar-icon"><User /></el-icon>
        </div>
        <div class="welcome-text">
          <h2>{{ userStore.username || '用户' }}，欢迎回来！</h2>
          <p>祝您今天工作顺利～</p>
        </div>
      </div>
      <el-button
        type="primary"
        :icon="Refresh"
        circle
        @click="fetchData"
        class="refresh-btn"
      />
    </header>

    <div class="stat-cards" v-loading="loading">
      <div
        v-for="(card, index) in statCards"
        :key="index"
        class="stat-card"
      >
        <div class="card-icon-wrapper" :style="{ background: card.gradient, borderColor: card.border }">
          <el-icon class="card-icon" :style="{ color: card.color }"><component :is="card.icon" /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">{{ card.title }}</div>
          <div class="card-value">{{ formatValue(card) }}</div>
        </div>
      </div>
    </div>

    <div class="ranking-section glass-panel">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><TrendCharts /></el-icon>
          图书销量排行榜
        </h3>
        <el-button type="primary" link class="more-btn" @click="goToStatistics">
          查看更多
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="ranking-table-wrapper">
        <el-table
          :data="rankingList"
          v-loading="loading"
        >
          <el-table-column label="排名" width="80" align="center">
            <template #default="{ $index }">
              <span class="rank-badge" :class="{ 'top-three': $index < 3 }">
                {{ $index < 3 ? rankIcons[$index] : $index + 1 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="书名" min-width="200" />
          <el-table-column prop="author" label="作者" min-width="150" />
          <el-table-column prop="salesCount" label="销量" min-width="120" align="center">
            <template #default="{ row }">
              <el-tag type="success" effect="plain" class="sales-tag">
                {{ row.salesCount || 0 }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="salesAmount" label="销售额" min-width="150" align="right">
            <template #default="{ row }">
              <span class="amount-text">¥{{ (row.salesAmount || 0).toLocaleString() }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  width: 100%;
}

/* Welcome header */
.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 24px 28px;
  background: linear-gradient(135deg, #eff6ff 0%, #f0f9ff 50%, #eef2ff 100%);
  border: 1px solid rgba(59, 130, 246, 0.12);
  border-radius: 16px;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(99, 102, 241, 0.1) 100%);
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 28px;
  color: #3b82f6;
}

.welcome-text h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #0f172a;
}

.welcome-text p {
  margin: 4px 0 0;
  font-size: 14px;
  color: #64748b;
}

.refresh-btn {
  background: rgba(0, 0, 0, 0.04) !important;
  border-color: rgba(0, 0, 0, 0.08) !important;
  color: #64748b !important;
}

.refresh-btn:hover {
  background: rgba(0, 0, 0, 0.08) !important;
  color: #334155 !important;
}

/* Stat cards */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.25s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: rgba(59, 130, 246, 0.2);
}

.card-icon-wrapper {
  width: 52px;
  height: 52px;
  border: 1px solid;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-icon {
  font-size: 26px;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 6px;
}

.card-value {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
  font-family: 'Inter', 'Segoe UI', sans-serif;
}

/* Ranking */
.ranking-section {
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.section-title .el-icon {
  color: #3b82f6;
}

.more-btn {
  font-size: 14px;
  font-weight: 500;
  color: #3b82f6 !important;
}

.ranking-table-wrapper {
  border-radius: 10px;
  overflow: hidden;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 16px;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.04);
  color: #64748b;
}

.rank-badge.top-three {
  font-size: 20px;
  background: transparent;
}

.sales-tag {
  border-radius: 6px;
  font-weight: 500;
}

.amount-text {
  font-weight: 600;
  color: #16a34a;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }

  .welcome-header {
    padding: 20px;
  }

  .welcome-text h2 {
    font-size: 18px;
  }
}
</style>
