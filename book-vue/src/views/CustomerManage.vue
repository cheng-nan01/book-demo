<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Search, Plus } from '@element-plus/icons-vue'
import { getCustomerList, addCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const loading = ref(false)
const customerList = ref([])
const keyword = ref('')
const gender = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加客户')

const pagination = reactive({
  page: 1,
  pageSize: 50,
  total: 0
})

const customerForm = reactive({
  id: null,
  name: '',
  gender: '',
  phone: ''
})

const customerRules = {
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }]
}

const customerFormRef = ref(null)

async function fetchCustomers() {
  try {
    loading.value = true
    const data = await getCustomerList({
      keyword: keyword.value,
      gender: gender.value || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    if (Array.isArray(data)) {
      customerList.value = data
      pagination.total = data.length
    } else if (data && data.list) {
      customerList.value = data.list
      pagination.total = data.total || data.list.length
    } else {
      customerList.value = data || []
      pagination.total = customerList.value.length
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  pagination.page = page
  fetchCustomers()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.page = 1
  fetchCustomers()
}

function openAddDialog() {
  dialogTitle.value = '添加客户'
  Object.assign(customerForm, {
    id: null,
    name: '',
      gender: '',
    phone: ''
  })
  dialogVisible.value = true
}

function openEditDialog(row) {
  dialogTitle.value = '编辑客户'
  Object.assign(customerForm, { id: row.id, name: row.name, gender: row.gender || '', phone: row.phone })
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await customerFormRef.value.validate()
    if (customerForm.id) {
      await updateCustomer(customerForm.id, customerForm)
      ElMessage.success('更新成功')
    } else {
      await addCustomer(customerForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCustomers()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
      type: 'warning'
    })
    await deleteCustomer(row.id)
    ElMessage.success('删除成功')
    fetchCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchCustomers()
})
</script>

<template>
  <div class="customer-manage-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><User /></el-icon>
        客户管理
      </h2>
    </div>

    <div class="search-section glass-panel">
      <el-form inline>
        <el-form-item label="搜索">
          <el-input
            v-model="keyword"
            placeholder="客户姓名/电话"
            clearable
            style="width: 180px"
            @keyup.enter="fetchCustomers"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="gender" placeholder="全部" clearable style="width: 90px" @change="fetchCustomers">
            <el-option label="全部" value="" />
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchCustomers">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">添加客户</el-button>
    </div>

    <div class="table-section glass-panel" v-loading="loading">
      <el-table :data="customerList" stripe>
        <el-table-column prop="name" label="姓名" min-width="110" align="center" />
        <el-table-column prop="gender" label="性别" width="90" align="center">
          <template #default="{ row }">
            {{ row.gender || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" min-width="170" align="center" />
        <el-table-column prop="createdAt" label="注册时间" min-width="150" align="center">
          <template #default="{ row }">
            {{ new Date(row.createdAt).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
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
          :page-sizes="[20, 50, 100, 200]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form ref="customerFormRef" :model="customerForm" :rules="customerRules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="customerForm.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="customerForm.gender" placeholder="请选择性别" clearable style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="customerForm.phone" placeholder="请输入联系电话" />
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
.customer-manage-page {
  width: 100%;
}
</style>
