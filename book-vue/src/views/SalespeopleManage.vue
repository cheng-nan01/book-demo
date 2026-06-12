<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getSalespeople, addSalesperson, updateSalesperson, deleteSalesperson } from '@/api/salespeople'

const loading = ref(false)
const list = ref([])
const keyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加销售员')

const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  gender: '',
  phone: '',
  hireDate: ''
})

async function fetchList() {
  loading.value = true
  try {
    const params = { page: pagination.page, pageSize: pagination.pageSize }
    if (keyword.value) params.keyword = keyword.value
    const data = await getSalespeople(params)
    if (data && data.list) {
      list.value = data.list
      pagination.total = data.total || data.list.length
    } else {
      list.value = data || []
      pagination.total = list.value.length
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

function handleReset() {
  keyword.value = ''
  pagination.page = 1
  fetchList()
}

function handlePageChange(page) {
  pagination.page = page
  fetchList()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.page = 1
  fetchList()
}

function openAddDialog() {
  dialogTitle.value = '添加销售员'
  form.id = null
  form.name = ''
  form.gender = ''
  form.phone = ''
  form.hireDate = ''
  dialogVisible.value = true
}

function openEditDialog(row) {
  dialogTitle.value = '编辑销售员'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请输入姓名')
    return
  }
  try {
    if (form.id) {
      await updateSalesperson(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await addSalesperson(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    // axios 拦截器已处理
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该销售员吗？', '提示', { type: 'warning' })
    await deleteSalesperson(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="salespeople-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><UserFilled /></el-icon>
        销售员管理
      </h2>
    </div>

    <div class="search-section glass-panel">
      <el-form inline>
        <el-form-item label="搜索">
          <el-input v-model="keyword" placeholder="姓名/电话" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">添加销售员</el-button>
    </div>

    <div class="table-section glass-panel" v-loading="loading">
      <el-table :data="list" stripe>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="phone" label="电话" min-width="130" />
        <el-table-column prop="hireDate" label="入职时间" width="120" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="输入电话" />
        </el-form-item>
        <el-form-item label="入职时间">
          <el-date-picker v-model="form.hireDate" type="date" placeholder="选择入职日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.salespeople-page {
  width: 100%;
  max-width: 1000px;
}
</style>
