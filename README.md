# 🎓 学生成绩管理系统 — 课程设计博客

---

## 📌 一、项目简介

**学生成绩管理系统**是一个基于 **Spring Boot** 的全栈 Web 应用，面向高校教务场景，提供学生信息管理、课程管理、成绩录入与查询、学习情况报表生成、成绩分布可视化等核心功能。系统支持随机生成 **10 万条**符合正态分布的测试数据，并可一键导出 Excel 报表，满足大规模数据场景下的性能验证需求。

本项目为 **Java 课程设计**作业。

> 🏫 **项目名称**：Student Score Management System（学生成绩管理系统）  
> 📅 **开发周期**：2026年6月  
> 🔗 **Git 仓库**：[https://github.com/zm2qs5nhks-collab/qlu_java_project](https://github.com/zm2qs5nhks-collab/qlu_java_project)

---

## 🛠 二、项目采用技术

| 技术分类 | 技术栈 | 版本 | 说明 |
|:---------|:-------|:-----|:-----|
| **后端框架** | Spring Boot | 4.1.0 | 项目核心框架，自动配置 + 内嵌 Tomcat |
| **持久层** | Spring Data JPA + Hibernate | 7.4.1 | ORM 映射，自动建表，简化数据库操作 |
| **模板引擎** | Thymeleaf | — | 服务端渲染，生成动态 HTML 页面 |
| **数据库** | MySQL | — | 关系型数据库，存储学生/成绩/用户数据 |
| **数据库连接池** | HikariCP | 7.0.2 | Spring Boot 默认高性能连接池 |
| **Excel 导出** | Apache POI | 5.2.5 | 生成 `.xlsx` 报表，支持样式与自动列宽 |
| **简化代码** | Lombok | — | 自动生成 Getter/Setter/构造器等样板代码 |
| **构建工具** | Maven | — | 依赖管理与项目构建 |
| **开发语言** | Java | 17 | LTS 长期支持版本 |
| **前端** | HTML5 + CSS3 + Thymeleaf | — | 响应式 Web 界面 |

### 项目架构

```
┌─────────────────────────────────────────────┐
│                Browser (Thymeleaf)           │
└──────────────────┬──────────────────────────┘
                   │ HTTP
┌──────────────────▼──────────────────────────┐
│            Controller 层 (6个)               │
│  Login / Student / Score / Course / Cls /   │
│         DataGenerator                       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│             Service 层 (6个)                 │
│  业务逻辑：CRUD / 报表生成 / 数据生成 / 认证  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│           Repository 层 (5个)                │
│       Spring Data JPA 数据访问接口           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│              MySQL 数据库                     │
│  表：student / score / course / cls /        │
│       user_account                          │
└─────────────────────────────────────────────┘
```

---

## 📋 三、功能需求分析

### 3.1 用户登录与权限验证

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 用户登录 | 用户名 + 密码验证，验证成功写入 Session | ✅ 已实现 |
| 登录拦截 | 未登录用户访问任何页面均重定向至登录页 | ✅ 已实现 |
| 退出登录 | 清除 Session，返回登录页 | ✅ 已实现 |

### 3.2 学生信息管理（CRUD）

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 添加学生 | 姓名、学号（自动生成，唯一）、性别、出生日期 | ✅ 已实现 |
| 学号唯一性 | 数据库唯一约束 + 应用层自动递增生成 `20260001` 起 | ✅ 已实现 |
| 修改学生 | 支持修改除学号外的所有字段（前端禁用学号输入框） | ✅ 已实现 |
| 删除学生 | 物理删除，同步删除关联成绩 | ✅ 已实现 |
| 班级关联 | 学生归属班级，下拉框选择 | ✅ 已实现 |
| 列表展示 | 分页展示所有学生，支持模糊搜索 | ✅ 已实现 |

### 3.3 成绩管理

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 批量录入 | 选择课程 → 展示所有学生列表 → 统一填分提交 | ✅ 已实现 |
| 按学号查询 | 精准匹配，显示姓名 + 学号 + 成绩，学号不存在则提示 | ✅ 已实现 |
| 按姓名查询 | 模糊匹配，同名多人一并显示，不存在则提示 | ✅ 已实现 |
| 单条编辑/删除 | 支持对单条成绩的修改和删除 | ✅ 已实现 |
| 原始成绩导出 Excel | 一键导出全部原始成绩为 `.xlsx` | ✅ 已实现 |

### 3.4 学习情况报表

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 报表展示 | 学号、姓名、各科成绩、各科班级均值、总分、总平均 | ✅ 已实现 |
| 降序排列 | 按总成绩从高到低排序 | ✅ 已实现 |
| 导出 TXT | 格式化文本文件 `成绩表.txt` | ✅ 已实现 |
| 导出 Excel（POI） | 含样式（表头加粗/灰底）、自动列宽、排名列 | ✅ **加分项** |
| 成绩分布图 | 按课程展示各分数段（0-59/60-69/…/90-100）柱状分布 | ✅ 已实现 |

### 3.5 测试数据生成（重要加分项）

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 随机生成 10 万学生 | 学号唯一、姓名随机组合（20姓×40名）、性别随机、出生日期随机 | ✅ 已实现 |
| 正态分布成绩 | 以 80 分为均值、10 为标准差，`Random.nextGaussian()` 生成 | ✅ 已实现 |
| 成绩裁剪 | 自动限制在 0~100 分范围内 | ✅ 已实现 |
| 导出文件 | 生成 `test_students_100k.txt`（4.8MB），支持大批量测试 | ✅ 已实现 |
| 可选入库 | 支持同时写入数据库或仅生成文件 | ✅ 已实现 |
| 进度反馈 | 每 1 万条刷新缓冲区并打印进度日志 | ✅ 已实现 |
| 批量优化 | 每 1000 条批量 `saveAll`，大幅提升写入性能 | ✅ 已实现 |

### 3.6 课程与班级管理

| 功能点 | 描述 | 实现状态 |
|:-------|:-----|:--------|
| 课程管理 | 课程名称、学分，支持增删改查 | ✅ 已实现 |
| 班级管理 | 班级名称，支持增删改查 | ✅ 已实现 |
| 预置课程 | 系统自动确保"数学"、"Java"、"体育"三门课程存在 | ✅ 已实现 |

---

## ✨ 四、项目亮点

### 🌟 亮点一：10 万级测试数据正态分布生成

这是本项目的**核心加分项**。系统通过 `Random.nextGaussian()` 生成以 80 分为均值、10 为标准差的正态分布成绩，模拟真实考试场景。同时采用**批量插入**策略（每 1000 条 `saveAll` 一次），大幅提升数据库写入性能。生成 10 万条数据全程仅需数秒。

```java
// 正态分布成绩生成核心代码
float mathScore = (float) Math.round(
    clampScore(mean + random.nextGaussian() * stdDev) * 10
) / 10;
```

### 🌟 亮点二：Apache POI Excel 报表导出

系统不仅支持 TXT 格式导出，还通过 **Apache POI** 实现了专业级 Excel 报表导出，包括：
- 表头加粗 + 灰色背景
- 自动列宽适配内容
- 排名列（按总分降序自动编号）
- 各科成绩与班级均值对比展示

### 🌟 亮点三：友好的 Web 交互界面

采用 Thymeleaf 模板引擎构建了完整的功能导航系统，按模块分类（基础数据 / 成绩管理 / 统计报表 / 测试工具），配色清晰，操作直观。

### 🌟 亮点四：严谨的架构分层

严格遵循 **Controller → Service → Repository** 三层架构，职责清晰，代码可维护性强。实体层使用 JPA 注解实现自动建表与关联映射。

### 🌟 亮点五：登录拦截器

通过 `LoginInterceptor` + `WebConfig` 实现全局登录拦截，未认证用户无法访问任何业务页面，保障数据安全。

---

## 📸 五、系统功能截图（建议补充）

> ⚠️ **说明**：以下为建议截图的页面清单。请运行项目后截取实际界面图片，替换为真实截图。

| 序号 | 截图内容 | 对应页面 |
|:-----|:---------|:---------|
| 1 | 登录页面 | `/` (login.html) |
| 2 | 系统首页导航 | `/index` |
| 3 | 学生列表 | `/student/list` |
| 4 | 添加学生 | `/student/toAdd` |
| 5 | 成绩列表 + 搜索 | `/score/list` |
| 6 | 批量录入成绩 | `/score/toAddBatch` |
| 7 | 学习情况报表 | `/score/report` |
| 8 | 成绩分布柱状图 | `/score/distribution` |
| 9 | 测试数据生成工具 | `/test/generate` |
| 10 | 导出的 Excel 报表 | 下载后的 .xlsx 文件 |

---

## 👥 六、团队成员及分工

| 姓名 | 负责模块 | 贡献说明 |
|:-----|:---------|:---------|
| 曹锦文 | 全栈开发 | 项目架构设计、数据库设计、后端接口开发、前端模板开发、POI Excel 导出、10万测试数据生成、系统测试与部署 |
| 贺俊泽 | 全栈开发 | 需求分析、功能模块开发、数据库设计与优化、前端页面开发、系统联调与测试 |

---

## 🔗 七、项目 Git 地址

- **GitHub 仓库**：[https://github.com/zm2qs5nhks-collab/qlu_java_project](https://github.com/zm2qs5nhks-collab/qlu_java_project)

### 项目结构

```
qlu_java_project/
├── student_score/
│   ├── src/main/java/com/demo/student_score/
│   │   ├── config/              # 登录拦截器 & Web配置
│   │   ├── controller/          # 6个控制器
│   │   │   ├── LoginController.java
│   │   │   ├── StudentController.java
│   │   │   ├── ScoreController.java
│   │   │   ├── CourseController.java
│   │   │   ├── ClsController.java
│   │   │   └── DataGeneratorController.java
│   │   ├── entity/              # 5个JPA实体
│   │   │   ├── Student.java
│   │   │   ├── Score.java
│   │   │   ├── Course.java
│   │   │   ├── Cls.java
│   │   │   └── UserAccount.java
│   │   ├── repository/          # 5个数据访问接口
│   │   ├── service/             # 6个业务服务
│   │   │   ├── StudentService.java
│   │   │   ├── ScoreService.java
│   │   │   ├── ScoreReportService.java   ← 报表+POI Excel导出
│   │   │   ├── CourseService.java
│   │   │   ├── ClsService.java
│   │   │   ├── DataGeneratorService.java ← 10万测试数据生成
│   │   │   └── UserAccountService.java
│   │   └── StudentScoreApplication.java
│   ├── src/main/resources/
│   │   ├── templates/           # 14个Thymeleaf模板页面
│   │   └── application.properties
│   ├── pom.xml                  # Maven依赖配置
│   └── test_students_100k.txt   # 生成的10万测试数据文件
├── 项目要求.txt
├── 要求.txt
└── README.md                    # 本博客文档
```

---

## 📊 八、Git 提交记录截图

> ⚠️ **说明**：请在 GitHub 仓库页面的 "Commits" 或 "Insights → Network" 中截取提交记录图，替换下方占位内容。

### 提交历史摘要

| 提交哈希 | 提交信息 | 日期 |
|:---------|:---------|:-----|
| `935e543` | 提交 qlu-java 项目作业代码 | 2026-06-26 |

你可以在 GitHub 仓库的 [Commits 页面](https://github.com/zm2qs5nhks-collab/qlu_java_project/commits/main) 查看完整提交历史，并截取提交记录截图放在此处。

---

## 🚀 九、如何运行

### 环境要求

- **JDK**：17+
- **MySQL**：8.0+
- **Maven**：3.6+

### 运行步骤

1. **克隆仓库**
   ```bash
   git clone https://github.com/zm2qs5nhks-collab/qlu_java_project.git
   cd qlu_java_project/student_score
   ```

2. **配置数据库**

   编辑 `src/main/resources/application.properties`：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/student_score?createDatabaseIfNotExist=true
   spring.datasource.username=你的MySQL用户名
   spring.datasource.password=你的MySQL密码
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **启动项目**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **访问系统**

   打开浏览器访问 `http://localhost:8080`，使用默认账号登录（需先在数据库 `user_account` 表中手动插入初始用户）。

---

## 🎯 十、总结与收获

通过本次课程设计，我深入实践了以下技能：

1. **Spring Boot 全栈开发**：从 Controller 到 Service 到 Repository，完整掌握了 Spring Boot 的开发流程
2. **JPA/Hibernate 数据持久化**：实体映射、关联查询、批量操作、唯一约束等
3. **Thymeleaf 模板引擎**：服务端渲染、表单绑定、条件渲染、列表遍历
4. **Apache POI**：编程式生成专业 Excel 报表
5. **数据生成与测试**：正态分布随机数生成、大批量数据的文件/数据库写入优化
6. **Web 安全基础**：Session 认证 + 拦截器模式

本项目从需求分析到编码实现，完整覆盖了一个 Web 信息管理系统的全部功能模块，是一次非常有价值的工程实践。

---

> 📅 **最后更新**：2026年6月26日  
> ✍️ **作者**：曹锦文、贺俊泽  
> 🏫 **齐鲁工业大学（QLU）** | 计科（拔尖）25-1
