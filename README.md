# 北京印刷学院积极分子自动化刷课脚本

![Static Badge](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E6%98%9F%E8%AE%B0-green?logo=github)![Static Badge](https://img.shields.io/badge/%E4%BD%9C%E8%80%85%E9%82%AE%E7%AE%B1-starnotes%40qq.com-green?logo=qq)![Static Badge](https://img.shields.io/badge/version-4.30.0-green?logo=selenium)

该脚本适用于**北京印刷学院线上党课学习**，其他学校需要根据具体的具体的CSS定位器进行重构。
该脚本的验证码识别使用百度OCR,[百度智能云]: https://cloud.baidu.com/product/ocr
使用前请将**百度OCR**的**APP_ID**、**API_KEY**、**SECRET_KEY**,以及**账号**和**密码**替换成自己的。
在**Main.java**中修改自己的**账号**和**密码**
在**Baidu.java**中修改自己的**APP_ID**、**API_KEY**、**SECRET_KEY**。
**Template.java**文件非开发者无需修改。
没有的可以去对应的官网注册账号，创建应用获取所需的**APP_ID**、**API_KEY**和**SECRET_KEY**。
使用时需要连接校园网，请勿打开VPN。
在脚本运行过程中会自动跳转到未学习的课程，自动完成学习。但是自测仍然需要自己完成。
在播放完视频后会自动点击播放完毕的按钮，在此过程中您会看见播放完毕的视频重新播放，但是会很快的跳转到未播放的视频，这属于该脚本的正常现象。
该脚本未提供更改倍速功能，建议留出一定时间刷课。不提供更改倍速功能是因为在页面源代码中有检测异常学习的情况，所以后台应该有学习时长记录。
如果发现输出了所有必修课完成，但是还是有未完成的必修课，再次运行脚本即可.
本项目使用**Maven**进行构建，**拉取代码后请自行使用Maven下载，管理依赖**，更改**账号密码**等信息后，可以使用**Maven**
进行打包，打包后的jar文件可以直接运行。
本项目只支持Chrome浏览器，其他浏览器需要自行修改代码和下载对应的WebDriver应用。
**本代码遵循 GPL v3 许可证，但未经作者书面许可，不得用于商业产品、不得倒卖。转载请说明出处。请尊重作者的劳动成果**
