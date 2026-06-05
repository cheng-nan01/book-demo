-- ============================================================
-- 书店管理系统 - 数据库建表脚本
-- 库名 bookstore 由 JDBC URL 参数 createDatabaseIfNotExist 自动创建
-- 所有表使用 CREATE TABLE IF NOT EXISTS，重复执行安全
-- ============================================================

-- ---------- 用户表（系统登录）----------
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名',
    password    VARCHAR(100) NOT NULL              COMMENT '密码（明文存储，生产需改 BCrypt）',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ---------- 客户表（买书的人）----------
CREATE TABLE IF NOT EXISTS customers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    name        VARCHAR(50) NOT NULL              COMMENT '客户姓名',
    gender      VARCHAR(2)  DEFAULT NULL          COMMENT '性别：男/女',
    phone       VARCHAR(20)                       COMMENT '手机号',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ---------- 图书表（库存）----------
CREATE TABLE IF NOT EXISTS books (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    isbn        VARCHAR(20)    NOT NULL UNIQUE    COMMENT 'ISBN 号，唯一',
    title       VARCHAR(100)   NOT NULL           COMMENT '书名',
    author      VARCHAR(50)                       COMMENT '作者',
    publisher   VARCHAR(50)                       COMMENT '出版社',
    price       DECIMAL(10,2)  NOT NULL           COMMENT '零售价',
    cost_price  DECIMAL(10,2)  DEFAULT 0.00       COMMENT '进货成本价',
    stock       INT            DEFAULT 0          COMMENT '当前库存数量',
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- ---------- 进货表（入库记录）----------
CREATE TABLE IF NOT EXISTS purchases (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    book_id         BIGINT         NOT NULL       COMMENT '外键 → books.id',
    quantity        INT            NOT NULL       COMMENT '进货数量',
    unit_price      DECIMAL(10,2)  NOT NULL       COMMENT '进货单价',
    total_amount    DECIMAL(10,2)  NOT NULL       COMMENT '进货总额 = unit_price × quantity',
    purchase_date   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '进货时间',
    CONSTRAINT fk_purchase_book FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货表';

-- ---------- 销售表（订单主表）----------
CREATE TABLE IF NOT EXISTS sales (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    customer_id   BIGINT                           COMMENT '外键 → customers.id（可为空，散客）',
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额 = 所有明细 subtotal 之和',
    total_cost    DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总成本 = 所有明细 unitCost×quantity 之和',
    sale_date     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单主表';

-- ---------- 销售明细表（订单中的每本书）----------
CREATE TABLE IF NOT EXISTS sale_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增',
    sale_id     BIGINT         NOT NULL            COMMENT '外键 → sales.id（ON DELETE CASCADE：删订单自动删明细）',
    book_id     BIGINT         NOT NULL            COMMENT '外键 → books.id',
    quantity    INT            NOT NULL            COMMENT '购买数量',
    unit_price  DECIMAL(10,2)  NOT NULL            COMMENT '销售单价（取自 books.price）',
    unit_cost   DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '销售成本（取自 books.cost_price）',
    subtotal    DECIMAL(10,2)  NOT NULL            COMMENT '小计 = unit_price × quantity',
    CONSTRAINT fk_saleitem_sale FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_saleitem_book FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售明细表';

-- ===== 索引：加速常用查询 =====
CREATE INDEX idx_sales_date     ON sales(sale_date);       -- 按日期范围查销售 + 月度统计 GROUP BY
CREATE INDEX idx_sales_customer ON sales(customer_id);     -- 按客户查销售 + 删除客户前校验
CREATE INDEX idx_saleitems_sale ON sale_items(sale_id);    -- 查订单明细
CREATE INDEX idx_saleitems_book ON sale_items(book_id);    -- 图书销量排行 + 删除图书前校验
CREATE INDEX idx_purchases_book ON purchases(book_id);     -- 查图书进货记录 + 删除图书前校验
CREATE INDEX idx_purchases_date ON purchases(purchase_date); -- 按日期范围查进货
CREATE INDEX idx_books_title    ON books(title);           -- 书名模糊搜索
CREATE INDEX idx_books_author   ON books(author);          -- 作者模糊搜索
