<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Search, Plus, Minus } from '@element-plus/icons-vue'
import { getSaleList, addSale, getSale, deleteSale } from '@/api/sale'
import { getBookList } from '@/api/book'
import { getCustomerList } from '@/api/customer'

const loading = ref(false)
const saleList = ref([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const filterStartDate = ref('')
const filterEndDate = ref('')
const filterCustomerId = ref(null)
const bookOptions = ref([])
const customerOptions = ref([])
const saleDetail = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 50,
  total: 0
})

const saleForm = reactive({
  customerId: null,
  items: [{ bookId: null, quantity: 1, price: 0, stock: null }]
})

const totalPrice = computed(() => {
  return saleForm.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

async function fetchSales() {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filterCustomerId.value) params.customerId = filterCustomerId.value
    if (filterStartDate.value) params.startDate = filterStartDate.value
    if (filterEndDate.value) params.endDate = filterEndDate.value
    const data = await getSaleList(params)
    if (Array.isArray(data)) {
      saleList.value = data
      pagination.total = data.length
    } else if (data && data.list) {
      saleList.value = data.list
      pagination.total = data.total || data.list.length
    } else {
      saleList.value = data || []
      pagination.total = saleList.value.length
    }
  } catch (error) {
    console.error('获取销售列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchSales()
}

function handleReset() {
  filterStartDate.value = ''
  filterEndDate.value = ''
  filterCustomerId.value = null
  pagination.page = 1
  fetchSales()
}

function handlePageChange(page) {
  pagination.page = page
  fetchSales()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.page = 1
  fetchSales()
}

async function fetchBooks() {
  try {
    const data = await getBookList({ page: 1, pageSize: 10000 })
    bookOptions.value = data?.list || data || []
  } catch (error) {
    console.error('获取图书列表失败:', error)
  }
}

async function fetchCustomers() {
  try {
    const data = await getCustomerList({ page: 1, pageSize: 10000 })
    customerOptions.value = data?.list || data || []
  } catch (error) {
    console.error('获取客户列表失败:', error)
  }
}

function openAddDialog() {
  saleForm.customerId = null
  saleForm.items = [{ bookId: null, quantity: 1, price: 0, stock: null }]
  dialogVisible.value = true
}

function addItem() {
  saleForm.items.push({ bookId: null, quantity: 1, price: 0, stock: null })
}

function removeItem(index) {
  if (saleForm.items.length > 1) {
    saleForm.items.splice(index, 1)
  }
}

function onBookChange(bookId, index) {
  const book = bookOptions.value.find(b => b.id === bookId)
  if (book) {
    saleForm.items[index].price = book.price
    saleForm.items[index].stock = book.stock ?? 0
  } else {
    saleForm.items[index].price = 0
    saleForm.items[index].stock = null
  }
}

async function handleSubmit() {
  try {
    const validItems = saleForm.items.filter(item => item.bookId && item.quantity > 0)
    if (validItems.length === 0) {
      ElMessage.warning('请至少选择一本图书')
      return
    }

    await addSale({
      customerId: saleForm.customerId,
      items: validItems.map(item => ({
        bookId: item.bookId,
        quantity: item.quantity
      }))
    })
    ElMessage.success('销售成功')
    dialogVisible.value = false
    fetchSales()
  } catch (error) {
    console.error('销售失败:', error)
  }
}

async function viewDetail(id) {
  try {
    const data = await getSale(id)
    saleDetail.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该销售记录吗？', '提示', {
      type: 'warning'
    })
    await deleteSale(row.id)
    ElMessage.success('删除成功')
    fetchSales()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchSales()
  fetchBooks()
  fetchCustomers()
})
</script>

<template>
  <div class="sale-manage-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><ShoppingCart /></el-icon>
        销售管理
      </h2>
    </div>

    <div class="search-section glass-panel">
      <el-form inline>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="filterStartDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="filterEndDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="客户">
          <el-select v-model="filterCustomerId" placeholder="全部客户" clearable filterable style="width: 180px">
            <el-option
              v-for="c in customerOptions"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">新建销售</el-button>
    </div>

    <div class="table-section glass-panel" v-loading="loading">
      <el-table :data="saleList" stripe>
        <el-table-column prop="name" label="客户" min-width="110" align="center">
          <template #default="{ row }">
            {{ row.name || '散客' }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="90" align="center">
          <template #default="{ row }">
            {{ row.gender || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" min-width="170" align="center">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount?.toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售时间" min-width="200" align="center">
          <template #default="{ row }">
            {{ new Date(row.saleDate).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row.id)">查看详情</el-button>
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

    <el-dialog v-model="dialogVisible" title="新建销售订单" width="800px">
      <el-form :model="saleForm" label-width="80px">
        <el-form-item label="客户">
          <el-select v-model="saleForm.customerId" placeholder="选择客户（可选）" clearable style="width: 100%">
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">
          <span class="divider-text">图书列表</span>
        </el-divider>

        <div class="sale-items-container">
          <div v-for="(item, index) in saleForm.items" :key="index" class="sale-item">
            <div class="item-row">
              <div class="item-book">
                <label class="item-label">图书{{ index + 1 }}</label>
                <el-select
                  v-model="item.bookId"
                  placeholder="选择图书"
                  filterable
                  @change="onBookChange($event, index)"
                  style="width: 100%"
                >
                  <el-option
                    v-for="book in bookOptions"
                    :key="book.id"
                    :label="book.title"
                    :value="book.id"
                  />
                </el-select>
                <span
                  v-if="item.bookId"
                  class="stock-tip"
                  :class="{ 'stock-low': item.stock !== null && item.stock < 5 }"
                >
                  当前库存：{{ item.stock ?? '...' }}
                </span>
              </div>
              <div class="item-quantity">
                <label class="item-label">数量</label>
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="999"
                  controls-position="right"
                />
              </div>
              <div class="item-price-col">
                <label class="item-label">单价</label>
                <div class="price-display">¥{{ item.price }}</div>
              </div>
              <div class="item-subtotal">
                <label class="item-label">小计</label>
                <div class="subtotal-display">¥{{ (item.price * item.quantity).toLocaleString() }}</div>
              </div>
              <div class="item-actions">
                <el-button
                  type="danger"
                  :icon="Minus"
                  :disabled="saleForm.items.length <= 1"
                  @click="removeItem(index)"
                  circle
                />
              </div>
            </div>
          </div>
        </div>

        <div class="add-book-row">
          <el-button type="primary" :icon="Plus" @click="addItem" circle />
          <span class="add-book-text">添加图书</span>
        </div>

        <el-divider />

        <div class="total-section">
          <span class="total-label">订单总额：</span>
          <span class="total-price">¥{{ totalPrice.toLocaleString() }}</span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认销售</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="销售详情" width="500px">
      <el-descriptions :column="1" border v-if="saleDetail">
        <el-descriptions-item label="客户">{{ saleDetail.name || '散客' }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ saleDetail.totalAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="销售时间">{{ new Date(saleDetail.saleDate).toLocaleString() }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin-top: 20px; margin-bottom: 12px; color: #e2e8f0;">购买明细</h4>
      <el-table :data="saleDetail?.items || []" stripe size="small">
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" width="100" align="right">
          <template #default="{ row }">¥{{ row.subtotal }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped>
.sale-manage-page {
  width: 100%;
}

.amount-text {
  font-weight: 600;
  color: #16a34a;
}

.divider-text {
  font-weight: 600;
  color: #334155;
  font-size: 15px;
}

.sale-items-container {
  max-height: 350px;
  overflow-y: auto;
  padding-right: 8px;
}

.sale-item {
  padding: 16px;
  margin-bottom: 12px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;
}

.sale-item:hover {
  border-color: rgba(59, 130, 246, 0.25);
  background: #f1f5f9;
}

.item-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
}

.item-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  margin-bottom: 6px;
}

.item-book {
  flex: 1;
  min-width: 200px;
}

.stock-tip {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #10b981;
  font-weight: 500;
}

.stock-tip.stock-low {
  color: #ef4444;
}

.item-quantity {
  width: 140px;
}

.item-quantity :deep(.el-input-number) {
  width: 100%;
}

.item-price-col {
  width: 100px;
  text-align: center;
}

.price-display {
  font-size: 16px;
  font-weight: 600;
  color: #3b82f6;
  padding: 8px 0;
}

.item-subtotal {
  width: 110px;
  text-align: center;
}

.subtotal-display {
  font-size: 16px;
  font-weight: 700;
  color: #f59e0b;
  padding: 8px 0;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 4px;
}

.add-book-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  padding-right: 4px;
}

.add-book-text {
  font-size: 13px;
  color: #3b82f6;
  font-weight: 500;
}

.total-section {
  text-align: right;
  padding: 16px;
  background: rgba(245, 158, 11, 0.06);
  border: 1px solid rgba(245, 158, 11, 0.12);
  border-radius: 8px;
}

.total-label {
  font-size: 15px;
  color: #64748b;
  margin-right: 8px;
}

.total-price {
  font-size: 28px;
  font-weight: 700;
  color: #f59e0b;
}

</style>
