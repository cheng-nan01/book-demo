<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Notebook, Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getBookList, addBook, updateBook, deleteBook } from '@/api/book'
import { addPurchase } from '@/api/purchase'
import { getSalesByBookId, updateSaleItem, deleteSaleItem } from '@/api/sale'

const loading = ref(false)
const bookList = ref([])
const keyword = ref('')
const stockStatus = ref('')
const publisherFilter = ref('')

const allPublishers = computed(() => {
  const pubs = new Set(bookList.value.map(b => b.publisher).filter(Boolean))
  return [...pubs].sort()
})
const dialogVisible = ref(false)
const dialogTitle = ref('添加图书')
const purchaseDialogVisible = ref(false)

const pagination = reactive({
  page: 1,
  pageSize: 50,
  total: 0
})

const bookForm = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  price: null,
  costPrice: null,
  stock: 0
})

const purchaseForm = reactive({
  bookId: null,
  quantity: 1,
  unitPrice: null
})

const bookRules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const bookFormRef = ref(null)

async function fetchBooks() {
  try {
    loading.value = true
    const data = await getBookList({
      keyword: keyword.value,
      stockStatus: stockStatus.value || undefined,
      publisher: publisherFilter.value || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    bookList.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  pagination.page = page
  fetchBooks()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.page = 1
  fetchBooks()
}

function handleSearch() {
  fetchBooks()
}

function handleReset() {
  keyword.value = ''
  stockStatus.value = ''
  publisherFilter.value = ''
  pagination.page = 1
  fetchBooks()
}

function openAddDialog() {
  dialogTitle.value = '添加图书'
  Object.assign(bookForm, {
    id: null,
    isbn: '',
    title: '',
    author: '',
    publisher: '',
    price: null,
    costPrice: null,
    stock: 0
  })
  dialogVisible.value = true
}

function openEditDialog(row) {
  dialogTitle.value = '编辑图书'
  Object.assign(bookForm, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await bookFormRef.value.validate()
    if (bookForm.id) {
      await updateBook(bookForm.id, bookForm)
      ElMessage.success('更新成功')
    } else {
      await addBook(bookForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchBooks()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这本书吗？', '提示', {
      type: 'warning'
    })
    await deleteBook(row.id)
    ElMessage.success('删除成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

function openPurchaseDialog(row) {
  purchaseForm.bookId = row.id
  purchaseForm.quantity = 1
  purchaseForm.unitPrice = row.costPrice
  purchaseDialogVisible.value = true
}

// ===== 销售记录 =====
const saleHistoryVisible = ref(false)
const saleHistoryList = ref([])
const saleHistoryBook = ref(null)
const saleHistoryKeyword = ref('')
const saleHistoryStartDate = ref('')
const saleHistoryEndDate = ref('')
const editItemVisible = ref(false)
const editItemForm = reactive({ saleId: null, bookId: null, quantity: 1, unitPrice: 0 })

const filteredSaleHistory = computed(() => {
  let list = saleHistoryList.value
  if (saleHistoryKeyword.value) {
    const kw = saleHistoryKeyword.value.toLowerCase()
    list = list.filter(s => (s.name || '散客').toLowerCase().includes(kw))
  }
  if (saleHistoryStartDate.value) {
    list = list.filter(s => s.saleDate >= saleHistoryStartDate.value + 'T00:00:00')
  }
  if (saleHistoryEndDate.value) {
    list = list.filter(s => s.saleDate <= saleHistoryEndDate.value + 'T23:59:59')
  }
  return list
})

async function openSaleHistory(row) {
  saleHistoryBook.value = row
  saleHistoryList.value = []
  saleHistoryKeyword.value = ''
  saleHistoryStartDate.value = ''
  saleHistoryEndDate.value = ''
  saleHistoryVisible.value = true
  const data = await getSalesByBookId(row.id)
  saleHistoryList.value = data || []
}

function openEditItem(saleId, item) {
  editItemForm.saleId = saleId
  editItemForm.bookId = item.bookId
  editItemForm.quantity = item.quantity
  editItemForm.unitPrice = item.unitPrice
  editItemVisible.value = true
}

async function saveEditItem() {
  await updateSaleItem(editItemForm.saleId, editItemForm.bookId, {
    quantity: editItemForm.quantity,
    unitPrice: editItemForm.unitPrice
  })
  ElMessage.success('修改成功')
  editItemVisible.value = false
  openSaleHistory(saleHistoryBook.value)
}

async function handleDeleteItem(saleId, bookId) {
  try {
    await ElMessageBox.confirm('确定要从销售单中移除该图书记录吗？', '提示', { type: 'warning' })
    await deleteSaleItem(saleId, bookId)
    ElMessage.success('删除成功')
    openSaleHistory(saleHistoryBook.value)
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败:', error)
  }
}

async function handlePurchase() {
  try {
    await addPurchase(purchaseForm)
    ElMessage.success('进货成功')
    purchaseDialogVisible.value = false
    fetchBooks()
  } catch (error) {
    console.error('进货失败:', error)
  }
}

onMounted(() => {
  fetchBooks()
})
</script>

<template>
  <div class="book-manage-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Notebook /></el-icon>
        图书管理
      </h2>
    </div>

    <div class="search-section glass-panel">
      <el-form inline>
        <el-form-item label="搜索">
          <el-input
            v-model="keyword"
            placeholder="书名/作者"
            clearable
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="库存">
          <el-select v-model="stockStatus" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="库存不足 (<10)" value="low" />
            <el-option label="已售罄" value="zero" />
          </el-select>
        </el-form-item>
        <el-form-item label="出版社">
          <el-select v-model="publisherFilter" placeholder="全部" clearable filterable style="width: 140px" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option v-for="p in allPublishers" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">添加图书</el-button>
    </div>

    <div class="table-section glass-panel" v-loading="loading">
      <el-table :data="bookList" stripe>
        <el-table-column prop="isbn" label="ISBN号" min-width="160" />
        <el-table-column prop="title" label="书名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" min-width="100" />
        <el-table-column prop="publisher" label="出版社" min-width="140" />
        <el-table-column prop="price" label="售价" width="90" align="right">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" width="90" align="right">
          <template #default="{ row }">
            <span class="cost-price-text">¥{{ row.costPrice || row.cost_price || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock > 10 ? 'success' : row.stock > 0 ? 'warning' : 'danger'">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="success" link @click="openPurchaseDialog(row)">进货</el-button>
            <el-button type="warning" link @click="openSaleHistory(row)">记录</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="bookFormRef" :model="bookForm" :rules="bookRules" label-width="80px">
        <el-form-item label="ISBN号" prop="isbn">
          <el-input v-model="bookForm.isbn" placeholder="输入13位ISBN" maxlength="17" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成本价">
          <el-input-number
            v-if="!bookForm.id"
            v-model="bookForm.costPrice"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
          <el-input
            v-else
            :model-value="'¥' + (bookForm.costPrice || 0)"
            disabled
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="purchaseDialogVisible" title="图书进货" width="400px">
      <el-form :model="purchaseForm" label-width="80px">
        <el-form-item label="数量">
          <el-input-number v-model="purchaseForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input :model-value="'¥' + (purchaseForm.unitPrice || 0)" disabled style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePurchase">确定</el-button>
      </template>
    </el-dialog>

    <!-- 销售记录对话框 -->
    <el-dialog v-model="saleHistoryVisible" :title="'销售记录 - ' + (saleHistoryBook?.title || '')" width="1000px">
      <div class="sale-history-filters" v-if="saleHistoryList.length">
        <el-input v-model="saleHistoryKeyword" placeholder="搜索客户名" clearable style="width: 180px" />
        <el-date-picker v-model="saleHistoryStartDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" style="width: 150px" />
        <span style="color:#999">—</span>
        <el-date-picker v-model="saleHistoryEndDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 150px" />
        <span style="color:#64748b;font-size:13px">共 {{ filteredSaleHistory.length }} 条</span>
      </div>
      <el-table :data="filteredSaleHistory" stripe size="small" v-if="saleHistoryList.length">
        <el-table-column prop="name" label="客户" width="100" align="center">
          <template #default="{ row }">{{ row.name || '散客' }}</template>
        </el-table-column>
        <el-table-column prop="salespersonName" label="销售员" width="80" align="center">
          <template #default="{ row }">{{ row.salespersonName || '-' }}</template>
        </el-table-column>
        <el-table-column label="购买详情" min-width="250">
          <template #default="{ row }">
            <div v-for="item in row.items" :key="item.id" class="sale-item-mini">
              <span>{{ item.title }} x{{ item.quantity }} @ ¥{{ item.unitPrice }}</span>
              <span class="subtotal-tag">小计 ¥{{ item.subtotal }}</span>
              <el-button type="primary" link size="small" @click="openEditItem(row.id, item)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteItem(row.id, item.bookId)">删除</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="saleDate" label="销售时间" width="170" align="center">
          <template #default="{ row }">{{ new Date(row.saleDate).toLocaleString() }}</template>
        </el-table-column>
      </el-table>
      <div v-else style="text-align:center;color:#999;padding:40px">暂无销售记录</div>
    </el-dialog>

    <!-- 编辑销售明细对话框 -->
    <el-dialog v-model="editItemVisible" title="编辑销售明细" width="380px">
      <el-form label-width="80px">
        <el-form-item label="数量">
          <el-input-number v-model="editItemForm.quantity" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="editItemForm.unitPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editItemVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.book-manage-page {
  width: 100%;
}

.price-text {
  font-weight: 600;
  color: #f59e0b;
}

.cost-price-text {
  color: #64748b;
}

.sale-history-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.sale-item-mini {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.subtotal-tag {
  color: #16a34a;
  font-weight: 600;
  font-size: 13px;
}

</style>
