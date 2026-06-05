# 数据库期末大作业-书店管理系统

Spring Boot + Vue 3 书店管理系统，支持图书管理、客户管理、销售管理、进货管理和数据统计。

## 技术栈

| 层级 | 技术 |
|------|------|
| **后端框架** | Spring Boot 4.0.5 |
| **ORM** | MyBatis 3.0.4（XML 映射，传统 SQL） |
| **分页** | PageHelper |
| **数据库** | MySQL 8.0 |
| **认证** | 基于 Session 的手写拦截器 |
| **前端框架** | Vue 3（Composition API） |
| **UI 组件** | Element Plus |
| **图表** | ECharts 6 |
| **状态管理** | Pinia |
| **路由** | Vue Router 4 |
| **构建工具** | Vite 8 |

## 功能模块

### 仪表盘
- 图书总数、客户总数、今日销售额、本月利润 统计卡片
- 图书销量排行榜

### 图书管理
- 图书的增删改查（ISBN、书名、作者、出版社、售价、成本价、库存）
- 按出版社筛选、按库存状态筛选（库存不足/已售罄）
- 图书进货（录入进货数量，自动更新库存）
- 查看某本书的所有销售记录，支持编辑和删除单条销售明细

### 客户管理
- 客户增删改查（姓名、性别、手机号）
- 按关键字和性别筛选

### 销售管理
- 创建销售单（选择客户和图书，自动计算金额）
- 按客户和日期范围筛选
- 销售明细编辑和删除（自动恢复库存、重算总额）

### 统计报表
- 月度销售额/成本/利润折线图
- 按月份范围筛选

## 数据库表设计

共 6 张表，使用 InnoDB 引擎，utf8mb4 字符集。所有金额字段使用 `DECIMAL(10,2)` 避免浮点精度问题。

### ER 关系图

```
users                    customers                books
┌──────────┐            ┌──────────────┐         ┌────────────────┐
│ id       │            │ id           │         │ id             │
│ username │            │ name         │◄────┐   │ isbn (UNIQUE)  │
│ password │            │ gender       │     │   │ title          │
│ createdAt│            │ phone        │     │   │ author         │
└──────────┘            │ createdAt    │     │   │ publisher      │
                        └──────────────┘     │   │ price          │
                              │              │   │ cost_price     │
                              │ customer_id  │   │ stock          │
                              ▼              │   │ createdAt      │
                        ┌──────────────┐     │   │ updatedAt      │
                        │ sales        │     │   └────────────────┘
                        │──────────────│     │         ▲
                        │ id           │     │         │ book_id
                        │ customer_id  │─────┘    ┌────┴───────────┐
                        │ total_amount │          │ purchases      │
                        │ total_cost   │          │────────────────│
                        │ sale_date    │          │ id             │
                        └──────────────┘          │ book_id        │
                              │                   │ quantity       │
                              │ sale_id           │ unit_price     │
                              ▼                   │ total_amount   │
                        ┌──────────────┐          │ purchase_date  │
                        │ sale_items   │          └────────────────┘
                        │──────────────│
                        │ id           │
                        │ sale_id      │────── sales.id (CASCADE)
                        │ book_id      │────── books.id
                        │ quantity     │
                        │ unit_price   │
                        │ unit_cost    │
                        │ subtotal     │
                        └──────────────┘
```

### 1. users — 系统用户表

登录认证使用，密码明文存储（生产环境应使用 BCrypt 加密）。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | 登录用户名 |
| `password` | VARCHAR(100) | NOT NULL | 密码 |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 注册时间 |

### 2. books — 图书表

存储图书信息和当前库存，ISBN 唯一。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `isbn` | VARCHAR(20) | NOT NULL, UNIQUE | ISBN 号，查重依据 |
| `title` | VARCHAR(100) | NOT NULL | 书名 |
| `author` | VARCHAR(50) | — | 作者 |
| `publisher` | VARCHAR(50) | — | 出版社 |
| `price` | DECIMAL(10,2) | NOT NULL | 零售价 |
| `cost_price` | DECIMAL(10,2) | DEFAULT 0.00 | 进货成本价（算利润用） |
| `stock` | INT | DEFAULT 0 | 当前库存数量 |
| `created_at` | DATETIME | NOT NULL | 录入时间 |
| `updated_at` | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 最后修改时间 |

**索引**：`title`、`author` 建有索引，用于模糊搜索加速。

### 3. customers — 客户表

记录买书客户的个人信息，性别使用中文 `男`/`女`。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `name` | VARCHAR(50) | NOT NULL | 客户姓名 |
| `gender` | VARCHAR(2) | DEFAULT NULL | 性别（男/女） |
| `phone` | VARCHAR(20) | — | 手机号 |
| `created_at` | DATETIME | NOT NULL | 创建时间 |

### 4. purchases — 进货表

记录每次图书入库，一条记录对应一次进货操作。`total_amount = unit_price × quantity`。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `book_id` | BIGINT | NOT NULL, FK → books.id | 进货图书 |
| `quantity` | INT | NOT NULL | 进货数量 |
| `unit_price` | DECIMAL(10,2) | NOT NULL | 进货单价（成本价） |
| `total_amount` | DECIMAL(10,2) | NOT NULL | 进货总额 |
| `purchase_date` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 进货时间 |

**索引**：`book_id` — 查某书的进货记录；`purchase_date` — 按日期查进货。

**业务规则**：删除图书时检查此表，存在进货记录则禁止删除。

### 5. sales — 销售订单主表

记录一次销售交易。`customer_id` 允许为空（支持散客购买）。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 订单号 |
| `customer_id` | BIGINT | FK → customers.id, NULLABLE | 客户（NULL=散客） |
| `total_amount` | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00 | 订单总金额 |
| `total_cost` | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00 | 订单总成本 |
| `sale_date` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 销售时间 |

**索引**：`customer_id` — 按客户查订单；`sale_date` — 按日期查订单 + 月度统计 GROUP BY。

**金额计算**：`total_amount` 和 `total_cost` 由 `sale_items.subtotal` 汇总而来，明细变动后通过 `recalcSaleTotals` 重新计算。

### 6. sale_items — 销售明细表

一次销售买多本书，每本书一条明细。与 `sales` 表是一对多关系。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `sale_id` | BIGINT | NOT NULL, FK → sales.id ON DELETE CASCADE | 所属订单 |
| `book_id` | BIGINT | NOT NULL, FK → books.id | 购买的图书 |
| `quantity` | INT | NOT NULL | 购买数量 |
| `unit_price` | DECIMAL(10,2) | NOT NULL | 销售单价（卖出时的价格） |
| `unit_cost` | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00 | 成本价（卖出时的成本） |
| `subtotal` | DECIMAL(10,2) | NOT NULL | 小计 = unit_price × quantity |

**索引**：`sale_id` — 查订单明细；`book_id` — 图书销量排行 + 删除图书前检查。

**级联删除**：`ON DELETE CASCADE`，删除销售单时自动删除所有明细。

**业务规则**：
- 创建时从 `books` 表读取当前售价和成本价填入
- 编辑/删除明细时自动恢复库存并重算订单总额
- 删除图书时检查此表，有销售记录则禁止删除

### 索引汇总

| 索引 | 用途 |
|------|------|
| `idx_books_title` | 书名模糊搜索 |
| `idx_books_author` | 作者模糊搜索 |
| `idx_sales_date` | 按日期范围查销售 + 月度统计 |
| `idx_sales_customer` | 按客户查销售 |
| `idx_saleitems_sale` | 查订单明细 |
| `idx_saleitems_book` | 销量排行 + 删书前校验 |
| `idx_purchases_book` | 查进货记录 + 删书前校验 |
| `idx_purchases_date` | 按日期查进货 |

## 项目结构

```
book-demo/
├── book-springboot/                # 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/
│       │   ├── config/             # 拦截器、MyBatis 配置、CORS
│       │   ├── controller/         # REST 接口
│       │   ├── dto/                # 请求/响应 DTO
│       │   ├── entity/             # 实体类
│       │   ├── mapper/             # MyBatis Mapper 接口 + XML
│       │   ├── service/            # 业务逻辑
│       │   └── util/               # Result 统一返回
│       └── resources/
│           ├── mapper/             # MyBatis XML 映射文件
│           ├── schema.sql          # 数据库建表脚本
│           └── application.properties
│
└── book-vue/                       # 前端
    └── src/
        ├── api/                    # 接口封装（axios）
        ├── components/layout/      # 侧边栏布局
        ├── router/                 # 路由配置
        ├── stores/                 # Pinia 状态管理
        ├── views/                  # 页面组件
        └── assets/                 # 样式和图片
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 20+
- MySQL 8.0+

### 1. 创建数据库

MySQL 中创建名为 `bookstore` 的数据库（或让程序自动创建）：

```sql
CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARSET utf8mb4;
```

### 2. 启动后端

```bash
cd book-springboot
mvn spring-boot:run
```

默认端口 `8080`，接口上下文路径 `/api`。

首次启动会自动执行 `schema.sql` 建表，并导入 `data.sql` 中的示例数据。

### 3. 启动前端

```bash
cd book-vue
npm install
npm run dev
```

默认端口 `5173`，已配置代理将 `/api` 请求转发到后端。

### 4. 访问系统

打开浏览器访问 `http://localhost:5173`

默认账号：`admin` / `admin123`

## API 接口

所有接口返回统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 登录 |
| POST | `/auth/register` | 注册 |
| GET | `/auth/me` | 获取当前用户 |
| POST | `/auth/logout` | 退出登录 |

### 图书
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/books` | 图书列表（支持 keyword/stockStatus/publisher 筛选） |
| POST | `/books` | 添加图书 |
| PUT | `/books/{id}` | 编辑图书 |
| DELETE | `/books/{id}` | 删除图书 |

### 客户
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/customers` | 客户列表 |
| POST | `/customers` | 添加客户 |
| PUT | `/customers/{id}` | 编辑客户 |
| DELETE | `/customers/{id}` | 删除客户 |

### 销售
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/sales` | 销售列表 |
| POST | `/sales` | 创建销售单 |
| DELETE | `/sales/{id}` | 删除销售单 |
| GET | `/sales/book/{bookId}` | 查询某图书的销售记录 |
| PUT | `/sales/items/{itemId}` | 编辑销售明细 |
| DELETE | `/sales/items/{itemId}` | 删除销售明细 |

### 统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/statistics/dashboard` | 仪表盘数据 |
| GET | `/statistics/ranking` | 销量排行榜 |
| GET | `/statistics/monthly` | 月度统计 |

## 设计说明

- **认证方式**：基于 Session，手写 `LoginInterceptor` 拦截器，无 Spring Security，简单直观
- **ORM**：全部使用 MyBatis XML 映射，SQL 采用传统写法（`WHERE 1=1` 替代 `<where>`，隐式连接替代 `JOIN`），便于理解和调试
- **统一返回**：所有接口用 `Result<T>` 包装，前端 axios 拦截器自动解包，组件直接拿到业务数据
- **前端布局**：侧边栏导航 + 玻璃拟态风格，所有操作在弹窗中完成
