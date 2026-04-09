# 普通用户温馨治愈风格页面/组件设计规范（桌面端优先）

## 0. 目标与边界

* 目标：让普通用户界面“温暖、柔和、轻负担、可呼吸”；减少尖锐对比与信息压迫。

* 边界：不新增业务功能；超级管理员界面不改变（默认主题不变）；仅普通用户启用治愈主题覆盖。

## 1. Global Styles（治愈主题设计令牌）

### 1.1 主题挂载方式

* 在根节点挂载：`<body class="theme-heal">` 或 `#app.theme-heal`。

* 所有覆盖样式必须以 `.theme-heal` 作为前置作用域，保证管理员端完全不受影响。

### 1.2 色彩（建议以 CSS Variables 落地）

* 背景：`--bg-0: #FFF9F2`（暖米白）；`--bg-1: #FFFFFF`（卡片底）

* 主色：`--primary: #7DB9B6`（柔和薄荷蓝绿）

* 强调：`--accent: #F2B880`（暖杏）

* 文本：`--text-1: #2F3A3A`；`--text-2: #6B7A7A`；弱化线条：`--line: #EAE2D8`

* 状态：成功 `#7FB77E`、警告 `#E6A23C`、危险 `#E57373`（降低饱和度与对比）

### 1.3 字体与排版

* 字体：系统字体优先（保持性能与一致性）。

* 标题层级：H1 20px/600；H2 16px/600；正文 14px/400；辅助 12px/400。

* 行高：正文 1.6；表单与表格 1.4（更舒展）。

### 1.4 形状、阴影与动效

* 圆角：卡片 12px；按钮/输入 10px；Tag 999px（胶囊）。

* 阴影：`0 6px 18px rgba(47,58,58,0.08)`（柔和、少层级）。

* 过渡：hover/focus 120–180ms，使用 ease-out；避免跳动。

### 1.5 组件通用交互规范（Element-UI 覆盖目标）

* Button：主按钮为薄荷主色，hover 提升亮度与阴影；次按钮使用浅底 + 主色描边；危险按钮降低饱和度。

* Input/Select：focus 使用主色柔光外描边（2px，低透明度）；placeholder 更浅但可读。

* Dialog/Drawer：圆角 14px；遮罩更柔（alpha 降低）；标题区增加副标题位。

* Table：表头更轻（浅底）；行 hover 仅轻微底色变化；减少强边框，改为分隔线。

* Message/Notification：背景浅色 + 图标；避免纯红纯绿高对比。

## 2. 页面设计

## 2.1 登录页 /login

### Layout

* 采用“左右分栏”布局（CSS Grid）：左侧品牌治愈插画/背景，右侧登录卡片（固定宽 420–480px）。

* 桌面端默认居中对齐；窄屏（<= 768px）改为上下堆叠，登录卡片置顶。

### Meta Information

* Title：登录｜星辰

* Description：安全登录后进入系统

* OG：标题同上；图片使用品牌图（现有素材可复用）

### Page Structure

1. 顶部轻量品牌条（Logo + 产品名，弱化）。
2. 登录卡片：标题 + 简短安抚式副文案（不改变功能，仅文案与样式）。
3. 表单区：账号/密码/验证码等（保持现有字段与逻辑）。
4. 底部辅助链接：注册/忘记密码（如现有存在则仅美化）。

### Sections & Components（改造范围）

* `el-card`：治愈主题圆角与阴影；卡片内间距增加（24px）。

* `el-input`/`el-form-item`：label 与提示文字层级更清晰；错误提示改为“浅红底 + 深红字”。

* `el-button`：主按钮更温和；loading 状态用浅色遮罩。

* 背景：使用现有 `login-background.jpg` 做柔光/模糊叠层（不替换资源也可，仅加遮罩）。

## 2.2 注册页 /register

### Layout

* 与登录页一致，保持学习成本低。

### Meta Information

* Title：注册｜星辰

* Description：创建账号以开始使用

### Page Structure

* 注册步骤/字段不变；通过“分组标题 + 进度暗示（视觉）”提升舒适度（不增加实际步骤）。

### Sections & Components（改造范围）

* 表单分组：以卡片内分隔标题呈现（例如“基础信息”“安全信息”——若现有字段可自然归类）。

* 成功提示：使用治愈主题 Notification 样式。

## 2.3 普通用户首页（工作台/仪表盘）/index

### Layout

* 维持现有后台布局（侧边栏 + 顶栏 + 内容区），仅对“普通用户”降低密度与对比。

* 内容区采用 12 栅格（Element-UI Row/Col），卡片间距建议 16–20px。

### Meta Information

* Title：工作台｜星辰

* Description：查看概览与常用入口

### Page Structure

1. 顶栏：弱化分割线；搜索/通知等保持原功能。
2. 内容区：概览卡片（PanelGroup 等）→ 图表区（如存在）→ 列表区。

### Sections & Components（改造范围）

* 概览卡片：统一圆角/阴影/留白；图标改用低饱和主题色（不替换 icon 也可通过 CSS 调色）。

* 图表（ECharts）：背景透明；网格线更浅；tooltip 圆角。

* 常用入口（若现有模块）：按钮/卡片化，增加 hover 柔光。

## 2.4 个人中心 /user/profile

### Layout

* 左右布局：左侧头像与基础信息卡；右侧为资料编辑/安全设置（保持现有 tab/分区结构）。

### Meta Information

* Title：个人中心｜星辰

* Description：查看与编辑个人资料

### Page Structure

1. 头像区（现有 userAvatar 等）：卡片化展示。
2. 基本资料（userInfo）：表单分组与间距优化。
3. 安全相关（resetPwd）：危险操作区域使用浅警示色块。

### Sections & Components（改造范围）

* Tabs：指示条更圆润；选中态以主色浅底呈现。

* 表单：按钮区固定在分组底部右侧；主次按钮区分更明显。

* 上传组件（ImageUpload/Avatar）：拖拽态增加虚线框与浅底提示。

## 3. 页面/组件改造范围清单（便于评审）

### 3.1 全局（仅普通用户主题生效）

* 主题令牌（颜色/字体/圆角/阴影/间距）

* Element-UI 皮肤覆盖：Button/Input/Form/Dialog/Table/Tag/Message/Notification/Menu

* 布局组件外观：Navbar、Sidebar、TagsView、Breadcrumb（仅样式、间距、颜色）

### 3.2 页面

* `views/login.vue`（登录页治愈风）

* `views/register.vue`（注册页治愈风）

* `views/index.vue` / `views/dashboard/*`（普通用户工作台相关模块的卡片与图表样式）

* `views/system/user/profile/*`（个人中心相关样式）

### 3.3 明确不改范围

* 超级管理员所有页面：不改变现有主题色、布局、组件样式与信息密度（不加载 `.theme-heal` 覆盖）。

