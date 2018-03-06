# 图片选择器
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/yongdongji/android/imagepicker/images/download.svg) ](https://bintray.com/yongdongji/android/imagepicker/_latestVersion)

## 准备工作 
1.获取依赖

**project的build.gradle**

```
allprojects {
    repositories {
        ......        
        maven { url  "https://dl.bintray.com/yongdongji/android" }
    }
}
```
**app的build.gradle**
[ ![Download](https://api.bintray.com/packages/yongdongji/android/imagepicker/images/download.svg) ](https://bintray.com/yongdongji/android/imagepicker/_latestVersion)

```gradle
compile 'www.weimu.io:silentupdate:{version_code}@aar'
```

2.增加权限

```xml
<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<!-- 存储权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```  

3.增加FileProvider【适配7.0】

> 注意：此处的```android:resource="@xml/filepaths"```自己谷歌或直接获取[demo文件](https://github.com/CaoyangLee/SilentUpdate/blob/master/app/src/main/res/xml/filepaths.xml)

```xml
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/filepaths" />
</provider>
```

## 用法

1.打开图库
```java
ImagePicker.getInstance().pickImage(MainActivity.this, needNumber, ImageSelectorActivity.MODE_MULTIPLE, true, true, false);
```

2.直接拍张
```java
ImagePicker.getInstance().takePhoto(MainActivity.this, true);
```

接收结果信息
``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
        ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
        // do something
    }
}
```
## 自定义配置
1.主题设置
主题由主项目color文件中的3个颜色进行定义
```xml
<color name="colorPrimary">#52A6FF</color>
<color name="colorPrimaryDark">#52A6FF</color>
<color name="colorAccent">#52A6FF</color>
```  
