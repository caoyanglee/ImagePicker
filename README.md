# 图片选择器
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/yongdongji/android/imagepicker/images/download.svg) ](https://bintray.com/yongdongji/android/imagepicker/_latestVersion)

## 演示
![](https://github.com/CaoyangLee/ImagePicker/blob/master/img/gif_demo.gif)

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
implementation 'www.weimu.io:imagepicker:{version_code}@aar'
//此aar所依赖的第三方
implementation 'com.android.support:appcompat-v7:27.1.0'
implementation 'com.android.support:recyclerview-v7:27.1.0'
implementation 'com.android.support:design:27.1.0'
implementation 'com.github.bumptech.glide:glide:4.6.1'
annotationProcessor 'com.github.bumptech.glide:compiler:4.6.1'
implementation 'com.github.chrisbanes:PhotoView:2.0.0'
implementation 'com.isseiaoki:simplecropview:1.1.7'
```

2.增加权限

```xml
<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<!-- 存储权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```  

3.增加FileProvider【适配7.0】

> 注意：此处的```android:resource="@xml/filepaths"```自己谷歌或直接获取[demo文件](https://github.com/CaoyangLee/ImagePicker/blob/master/app/src/main/res/xml/filepaths.xml)

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

1.1.打开图库
```java
ImagePicker.getInstance().pickImage(activity);
```

**多参数调用**
```java
/**
 * @param activity
 * @param maxSelectNum 最大选择图片数          default=9
 * @param mode 图库模式【单选】【多选】         default=ImageSelectorActivity.MODE_MULTIPLE
 * @param enableCamera 是否启用摄像头          default=true
 * @param enablePreview 是否打开预览           default=true
 * @param enableCrop 是否进行裁剪【单选可用】   default=false
 */
ImagePicker.getInstance().pickImage(activity, 9, ImageSelectorActivity.MODE_MULTIPLE, true, true, false);
```


1.2.直接拍张
```java
ImagePicker.getInstance().takePhoto(activity);
```

**多参数调用**
```java
/**
 * @param activity
 * @param enableCrop 是否启用裁剪 default=false
 */
ImagePicker.getInstance().takePhoto(activity,false);
```

2.接收结果信息
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
