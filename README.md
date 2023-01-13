# DBQuery-Online

网页版数据库管理工具，支持数据查询，数据模拟，数据转移 ，数据下载，集群分库分表/多数据源合并等功能。

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled.png)

## Background

为了提高效率，安全性，便捷性等特点，网页版数据库查询工具理应成为各中大型公司DBA平台的标准产品之一，不过结合本人自身的经历以及调研后发现，八成的公司都缺少这样一个标准化产品，开发人员在各种不可抗力的原因下无法使用Navicat，只能通过命令行的形式操作DB，效率十分低下，故本人开源此项目供有相同困惑的人使用。本项目中除了基础的数据查询/更新，还包含了很多日常开发中可能使用到的工具型产品，另外在交互上也做了很多优化，后面会有简单的介绍。

## Usage

### 基础使用

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled.jpeg)

**注：点击执行等操作时：请用光标选中SQL后执行，一次只能执行一条SQL。**

### 集群使用

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%201.png)

### 自定义数据源

输入IP+端口+user+pass即可使用

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%202.png)

## Interactive

1、为保护DB，select 语句如不含 limit，默认返回limit 100，如需更多返回结果可在SQL中自行指定limit。

2、页面支持分Tab自动保存，下次打开页面的时候可以看见历史的SQL。

3、返回结果集列过长时，经常需要左右滑动查看结果集，容易造成页面后退，故本页面已禁止页面前进后退的功能，可以大胆的左右滑动。

4、在执行SQL时候，如不选中SQL，点击Execute会执行当前行当前光标前的SQL。

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%203.png)

5、单击表格的单元项可快捷复制单元格内容。

6、双击返回表格的一行时可以自动复制成Insert语句，方便在DB造一些数据。

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%204.png)

7、在 `SqlController` 中的 `getSqlService()` 方法中可以配置线上环境DB和线下环境DB的映射，通过将结果集导入线下环境的功能可以便捷拷贝导入数据。

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%205.png)

## Structure

![Untitled](DBQuery-Online%20ea01e0ac6b334cfa9e5267ec0cfe1408/Untitled%206.png)

## More

理论上线上环境的DB应当只支持Select操作，并在Select时需要做索引检测，本项目中为了方便使用已去掉相关功能，如需请自主实现。
