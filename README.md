- [概述](#概述)
- [Android SDK 下载](#android-sdk-下载)
- [使用 Android Studio 进行本地集成（Gradle 编译环境）-支持 Java 和 Kotlin](#使用---android-studio-进行本地集成gradle-编译环境-支持-java-和-kotlin)
- [配置完成 开始数据上报](#配置完成-开始数据上报)
- [使用 jitpack 仓库进行远程集成](#使用-jitpack-仓库进行远程集成)
- [手动配置集成（不推荐）](#手动配置集成不推荐)
# 概述
SDK接入属于量U系统的前端部分，负责采集设备信息进行脱敏传输，目前支持 Android 和 iOS，以后会支持 H5 和小程序
Android SDK有两种集成方式，一种是自动打包SDK，另一种是手动配置集成

# Android SDK 下载

[gitee](https://gitee.com/digitalevers/Lionu-Android-SDK-Src)
# 自动打包SDK集成（推荐）
自动打包SDK不需要开发者配置域名等参数，直接下载的SDK已经是定制好的分包，直接调用SDK的方法即可，接入过程方便快捷

- 安装量U系统过程中，系统需要开发者填写SDK上报域名等信息
  图片
- 系统安装完成，添加应用后，每一条应用App信息的右边都会有一个下载SDK的按钮
图片
- 开发者点击下载SDK，此时系统会将此前填写的SDK上报域名、应用AppID等信息全部打包进SDK，下载后进行集成即可

##### 使用   Android Studio 进行本地集成（Gradle 编译环境）-支持 Java 和 Kotlin
- 手动导入，将下载好的 lionsu.aar 库文件放入项目的 libs 目录下
图片
- 修改 Project 层面的 build.gradle
修改前
```
allprojects {
    repositories {
        .....
        google()
        jcenter()
    }
}
```
修改后
```
allprojects {
    repositories {
        .....
        google()
        jcenter()
        //下面是添加的内容
        flatDir {
            dirs 'libs'
        }
        //
    }
}
```
> 注意：修改的是 allprojects 中的 repositories 节点，不是 buildscript 的 repositories 节点
- 修改 App Module 层面的 build.gradle
修改前
```
.....
dependencies {
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.google.android.material:material:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'androidx.navigation:navigation-fragment:2.2.2'
    implementation 'androidx.navigation:navigation-ui:2.2.2'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
```
修改后
```
.....
dependencies {
    //下面是添加的内容
    implementation(name: 'lionsu', ext: 'aar')
    //
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.google.android.material:material:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'androidx.navigation:navigation-fragment:2.2.2'
    implementation 'androidx.navigation:navigation-ui:2.2.2'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
```
- 修改 AndroidManifest.xml 在 application 节点内添加内容进行初始化
```
<provider
    android:name="com.android.reportx.util.ApplicationProvider"
    android:authorities="${applicationId}.contextprovider"
    android:exported="false" />
<meta-data
    android:name="CHANNEL_KEY"
    android:value="default" />
```
##### 配置完成 开始数据上报
- 在代码开头引入包
```
import com.android.reportx.util.RP;  //也可选中 RP 文本按 Alt+Enter 自动导入
```
- 上报启动激活 请在您的主界面（通常是MainActivity）的 onCreate 函数中调用以下代码：
```
RP.launch(MainActivity.this)
```
- 上报注册事件 请在注册完成的回调中调用以下代码：
RP.reg(this)
```
- 上报付费事件 请在支付完成的回调中调用以下代码：
```
RP.pay(this, amount)    //amount 为订单的支付金额 单位为 分
```
> 注意：对于适配了Android 6.0以上 (API >= 23) 的App，建议开发者在获得了动态权限之后，再调用SDK上报代码，否则SDK获取设备信息可能受影响。

##### 使用 jitpack 仓库进行远程集成
- 修改 Project 层面的 build.gradle
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' } //添加这一行
		}
	}
```
- 修改 App Module 层面的 build.gradle
```
	dependencies {
            .....
	        implementation 'com.github.shuzibanshou:Lionsu-Android-SDK:1.2' //添加这一行
	}
```
- 之后的流程和本地集成方式一样

# 手动配置集成（不推荐）
开发者也可直接修改 lionsu.aar 进行参数的配置，直接用解压工具打开 lionsu.arr，找到 assets 目录下的 lion-u-config.json 文件
```
{
  "host": "",
  "appid": "",
  "planid": ""
}
```
直接将上报域名和 AppID 填入并保存，计划ID默认为空，如果有分包，填入分包ID即可
接下来的流程和自动打包集成一样
但是强烈不推荐这种手动配置集成方式


