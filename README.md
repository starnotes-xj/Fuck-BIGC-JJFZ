# 北京印刷学院积极分子自动化刷课脚本
[![Static Badge](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E6%98%9F%E8%AE%B0-green?logo=github&link=https%3A%2F%2Fgithub.com%2Fstarnotes-xj)](https://github.com/starnotes-xj)[![Static Badge](https://img.shields.io/badge/%E4%BD%9C%E8%80%85%E9%82%AE%E7%AE%B1-starnotes%40qq.com-green?logo=github)](https://github.com/starnotes-xj)[![Static Badge](https://img.shields.io/badge/%E5%8C%97%E4%BA%AC%E5%8D%B0%E5%88%B7%E5%AD%A6%E9%99%A2-BIGC-blue?logo=counterstrike)](https://www.bigc.edu.cn/)[![Static Badge](https://img.shields.io/badge/Selenium-version%3A4.30.0-green?logo=selenium)](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java)[![Static Badge](https://img.shields.io/badge/%20LICENSE-GPL3.0-green)](https://github.com/starnotes-xj/Fuck-BIGC-JJFZ?tab=GPL-3.0-1-ov-file#readme)

## 脚本说明

该脚本适用于[北京印刷学院线上党课学习](http://xscdx.bigc.edu.cn/)，其他学校需要根据具体的具体的CSS定位器进行重构。
该脚本的验证码识别使用[百度OCR](https://cloud.baidu.com/product/ocr)，自动答题使用[Deepseek](https://www.deepseek.com/)

**该脚本的题库有876条题目，每个课都有，也可自行下载题库查找答案**

## Release版本

下载源码之后不会使用的，可以直接下载**Release**版本，点击**Release徽标**即可跳转至**Release版本**页面进行下载。

[![Static Badge](https://img.shields.io/badge/RELEASE-v2.0-green?logo=github)](https://github.com/starnotes-xj/Fuck-BIGC-JJFZ/releases)

**docker镜像**可以在**Release**中下载，镜像也可以直接拉取，使用docker镜像则无需进行下面的准备,docker镜像便于使用

[![Docker镜像](https://img.shields.io/badge/Docker-pull-%2344cef6?logo=docker&link=https%3A%2F%2Fhub.docker.com%2Frepository%2Fdocker%2Fstarnotes%2Ffuck-bigc-jjfz%2F)](https://hub.docker.com/repository/docker/starnotes/fuck-bigc-jjfz)

在**Release**中的**tar**文件是Docker镜像

下载之后使用如下命令加载即可，然后按照下面的容器启动命令启动即可

`docker load -i fuck-bigc-jjfz.tar`

**拉取**命令如下

`docker pull starnotes/fuck-bigc-jjfz:latest`

拉取之后使用如下命令**创建并启动容器**

`docker run --name=lesson -it --network=host --shm-size=2g starnotes/fuck-bigc-jjfz /bin/bash`

上一条命令执行之后本地已经有了容器，可以直接使用如下命令来**启动容器**

`docker starnotes lesson`

然后按照容器显示的提示输入对应的用户名，密码，API等数据即可。

## 使用前的准备

安装Java环境

[![Static Badge](https://img.shields.io/badge/Java24-Download-green)](https://www.oracle.com/cn/java/technologies/downloads/)

API_KEY没有的可以去对应的官网注册账号，创建应用获取所需的**APP_ID**、**API_KEY**和**SECRET_KEY**。下列徽标可以直接点击跳转

使用前请将**百度OCR**的**APP_ID**、**API_KEY**、**SECRET_KEY**,以及**账号**和**密码**替换成自己的。**使用自动答题时需要更改DeepSeek的API_KEY更换为自己的**

[![Static Badge](https://img.shields.io/badge/%E7%99%BE%E5%BA%A6OCR-%E7%82%B9%E5%87%BB%E6%AD%A4%E5%A4%84-green?logo=baidu)](https://cloud.baidu.com/product/ocr)[![Static Badge](https://img.shields.io/badge/DeepSeek-API%E5%B9%B3%E5%8F%B0%E7%82%B9%E5%87%BB%E6%AD%A4%E5%A4%84-green)](https://platform.deepseek.com/)

### **更改处：**

> 1. 在**Main.Main.java**中修改自己的**账号**和**密码**
> 2. 在**Baidu.java**中修改自己的**APP_ID**、**API_KEY**、**SECRET_KEY**。
> 3. 在**Deepseek.java**中修改自己的**API_KEY**。
> 4. **Main.Template.java**文件**非开发者**无需修改。

## 注意事项

> 1. 使用时需要连接校园网，请勿打开VPN。
>
> 2. 在脚本运行过程中会自动跳转到未学习的课程，自动完成学习。但是自测仍然需要自己完成。
>
> 3. 在播放完视频后会自动点击播放完毕的按钮，在此过程中您会看见播放完毕的视频重新播放，但是会很快的跳转到未播放的视频，这属于该脚本的正常现象。
>
> 4. 该脚本未提供更改倍速功能，建议留出一定时间刷课。不提供更改倍速功能是因为在页面源代码中有检测异常学习的情况，所以后台应该有学习时长记录。
>
> 5. 如果发现输出了所有必修课完成，但是还是有未完成的必修课，再次运行脚本即可.
>
> 6. 使用自动答题时需要运行下列命令，使用Win+R打开运行输入以下命令
>    **chrome.exe --remote-debugging-port=9222 --user-data-dir="D:\selenium_test"**
>
>    **AutoAnswerQuestion.java**是自动答题的程序，使用上述命令后会打开一个新的chrome浏览器，在新打开的chrome浏览器中重新登录，**进入自测考试和结业考试后运行AutoAnswerQuestion即可自动答题**。在答题前需要运行Main.java来保存Deepseek的API_KEY。或者在**config/APIKeys.json**中手动添加，格式如下：
>
>    ```json
>    "deepseekapikey":"你的Deepseek API_KEY"
>    ```
>
>    **每条API_KEY之间用逗号分隔**
>    
>    Jar包版本的自动答题也需要输入上述命令打开新的浏览器进行登录，在考试界面直接用java -jar AutoAnswer.jar运行自动答题

## 其余说明

> *本项目使用**Maven**进行构建，**拉取代码后请自行使用Maven下载，管理依赖**，更改**账号密码**等信息后，可以使用**Maven***
> *进行打包，打包后的jar文件可以直接运行。*
> *本项目只支持**Chrome**浏览器，**火狐浏览器**需要自行修改代码和下载对应的**WebDriver**应用。*
> ***需要爬取题库的源码请联系作者。上述有作者联系方式***
> ***本代码遵循 GPL v3 许可证，未经作者书面许可，不得用于商业产品、不得倒卖。转载请说明出处。请尊重作者的劳动成果***
