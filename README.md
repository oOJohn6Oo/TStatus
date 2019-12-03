
# T系列——状态管理TStatus

   * 支持Activity状态管理
   * 支持Fragment状态管理
   * 支持  View  状态管理
   * 兼容CoordinatorLayout状态

# 使用

   > 鉴于代码过于轻量化，可以直接CV到自己仓库
   但... 懒人就应该只要一行代码，所以依然可以在jitpack上找到

   ## 1. import or CV
   ## 2. `可选的` 调用一次`TStatus.init()`作为全局统一的status样式
   ## 3. `val myStatus = TStatus.Builder().etc.`创建TStatus对象
   ## 4. `myStatus.showLoading() / showEmpty() / showServerError() / showNetError() / showContent()`切换状态
