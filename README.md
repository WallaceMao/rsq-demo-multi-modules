# Grails多模块开发项目与集成测试demo

## 1 程序结构
项目按照主子程序的结构，mainServer为主程序，rsqBizTodo、rsqDaoTodo和rsqUtil分别为提供相应功能的子模块

### 1.1 web server主程序
mainServer项目，提供各子模块运行的环境。
**注意：各子模块依赖的其他jar包或者plugin，可以直接在mainServer的BuildConfig.groovy中配置**

mainServer的主要元素为controller，提供的功能包括：

- try catch捕获执行异常
- 通过过滤器（filter）对request和response进行处理
- 解析request参数、head等
- 调用service方法，执行业务逻辑
- 处理返回值或者跳转

mainServer层提供api，不能编写业务逻辑

mainServer主模块建议只进行functional test，基本接口直接进行api test，需要web浏览器支持的可以依靠Web Driver进行web功能测试

### 1.2 biz子模块
所有的系统业务逻辑都写在biz子模块中。根据具体功能可以划分为多个biz子模块。本项目中只有rsqBizTodo这个子模块，用于处理todo功能的基本操作。

biz子模块封装复杂的业务逻辑，biz子模块可以拆分成更优雅的架构

由于biz模块的依赖关系比较复杂，biz子模块建议只进行integration test

### 1.3 dao子模块（包括domain模块）
dao子模块提供对数据库访问的接口。无论是使用SQL语句还是ORM框架操作数据库，都需要通过dao子模块提供的接口进行操作。
为减少层级和模块数量，domain放在dao子模块中

dao子模块的代码要尽量细粒度、轻量级，封装最基本的数据库操作。业务逻辑复杂的操作都放在biz子模块中

dao子模块一般比较轻，只依赖数据库或者domain进行基本的数据操作，dao子模块建议只进行unit test

### 1.4 util工具子模块
日事清相关的工具类

工具类的开发需要保证尽量减少副作用，尽量不依赖于Grails Application Context环境

相关的工具类建议进行unit test

### 1.5 程序依赖顺序
按照Grails对多模块开发的支持，开发模式或者打包时，mainServer主程序首先会加载BuildConfig.groovy中配置的所有jar包依赖和plugin依赖（不包括子模块），用来作为底层支持；之后会将BuildConfig.groovy中通过grails.plugin.location配置的各子模块的源码编译至主程序中。所以各子模块之间的互相依赖不需要做明确配置。
但是在IDE环境下，为方便开发时IDE可以自动提示查找使用的类，可以将子模块A所依赖的另一个子模块B配置到A子模块的BuildConfig.groovy中

## 2 开发指南
在使用IDEA进行项目开发时，有两种方式：
- 主子模块方式：加载主模块，自动引入各子模块
- 插件方式：直接打开某个子模块

### 2.1 主子模块方式开发
在IDEA中直接引入主模块，IDEA会自动解析引入相关的子模块，并以同级目录的方式显示

#### 依赖引入

1 jar包依赖
由于子模块的jar包依赖都已经在主模块的BuildConfig.groovy中引入，所以子模块中可以不配置jar包依赖。

而对于某些没有在主模块引入的jar包，子模块需要在BuildConfig.groovy中引入。同时，由于在主子模式下，IDEA无法自动加载依赖，需要手动在Project Struture -> Project Settings -> Modules -> Dependencies中引入jar包

2 plugin依赖
由于子模块的plugin依赖都已经在主模块的BuildConfig.groovy中引入，所以子模块中可以不配置plugin依赖。

而对于某些没有在主模块引入的plugin，子模块需要在BuildConfig.groovy中引入。

3 子模块依赖
由于所有子模块在主模块中都已经配置了grails.plugin.location，所以，Grails编译时不需要引入子模块。

不过，由于在主子模式下，IDEA无法找到所依赖的子模块，所以为了使用代码提示功能，需要在BuildConfig.groovy中配置grails.plugin.location

### 2.2 插件方式开发
由于子模块是插件，可以按照插件的方式开发。直接在IDEA中将子模块所在的项目引入。由于plugin也是grails程序的一种，所以可以按照grails的结构来写插件代码。

#### 依赖引入
与Grails Web Application类似，直接在BuildConfig.groovy中引入jar包和plugin的依赖，不会出现IDEA无法找到的情况。子模块依赖直接在BuildConfig.groovy中使用grails.plugin.location引入

#### 插件安装方式
以插件模式开发的子模块，没有真实模拟主模块的环境，所以需要依赖测试来保证插件功能的正确性和完整性。测试通过后，可以通过

    grails maven-install

命令在本地安装插件，或者提交到Grails plugin的公共库中

## 3 测试指南
在不过多影响开发速度的情况下，同时保证一定的测试覆盖率，需要对不同的模块进行不同类型的测试。

详细的测试过程参考另一个demo项目：[rsq-demo-tester](https://github.com/WallaceMao/rsq-demo-tester)

### 3.1 单元测试

在dao子模块中进行

    grails test-app --echoOut unit:

### 3.2 集成测试

在biz子模块中进行

    grails test-app -Dgeb.env=local --echoOut integration:

### 3.3 功能测试

在web server主模块中进行

    grails test-app -Dgeb.env=local --echoOut functional: xxx.xxx.xx.**.*

## 4 部署指南

### 4.1 开发环境下
直接在web server主模块下运行，启动Grails Web Application

### 4.2 生产环境打包
在web server主模块下，使用grails war命令进行打包

## 5 参考
[Grails测试](http://docs.grails.org/2.5.4/guide/testing.html)
[Grails插件开发](http://docs.grails.org/2.5.4/guide/plugins.html)