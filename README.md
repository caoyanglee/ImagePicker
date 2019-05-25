# 图片选择器
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://jitpack.io/v/caoyanglee/ImagePicker.svg)](https://jitpack.io/#caoyanglee/ImagePicker)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

## 演示
![](https://github.com/CaoyangLee/ImagePicker/blob/master/img/gif_demo.gif)

## 准备工作 
1.获取依赖

**project的build.gradle**

```
allprojects {
    repositories {
        ......       
        maven { url "https://jitpack.io" } 
    }
}
```
**app的build.gradle**
[![](https://jitpack.io/v/caoyanglee/ImagePicker.svg)](https://jitpack.io/#caoyanglee/ImagePicker)

```gradle

implementation 'com.github.caoyanglee:ImagePicker:{latestVersion}'

```

```gradle
//以下为此aar所依赖的第三方
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:recyclerview-v7:28.0.0'
implementation 'com.android.support:design:28.0.0'
implementation 'com.github.bumptech.glide:glide:4.8.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.8.0'
implementation 'com.github.chrisbanes:PhotoView:2.1.3'
implementation 'com.theartofdev.edmodo:android-image-cropper:2.7.0'
implementation 'top.zibin:Luban:1.1.3'
```

2.增加权限

```xml
<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<!-- 存储权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
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

1.1.打开图库 多选
```kotlin
ImagePicker.pickImage(activity);
```

**多参数调用**
```kotlin
/**
 * @param activity
 * @param maxSelectNum 最大选择图片数          default=9
 * @param enableCamera 是否启用摄像头          default=true
 * @param enablePreview 是否打开预览           default=true
 * @param showIsCompress 是否显示使用原图按钮   default=true
 */
ImagePicker.pickImage(activity, 9, true, true, true);
```

1.2.打开图库 单选
```kotlin
ImagePicker.pickImage4One(activity);
```

**多参数调用**
```kotlin
/**
 * @param activity
 * @param enableCamera 是否启用摄像头          default=true
 * @param enableCrop   是否打开裁剪            default=true
 * @param cropAspectRatioX   裁剪宽高比 宽     default=0
 * @param cropAspectRatioY   裁剪宽高比 高     default=0
 * @param showIsCompress 是否显示使用原图按钮   default=true
 */
ImagePicker.pickImage4One(activity,true,true,9,16,true);
```


1.3.直接拍照
```kotlin
ImagePicker.takePhoto(activity);
```

**多参数调用**
```kotlin
/**
 * @param activity
 * @param enableCrop 是否启用裁剪 default=false
 * @param cropAspectRatioX   裁剪宽高比 宽     default=0
 * @param cropAspectRatioY   裁剪宽高比 高     default=0
 */
ImagePicker.takePhoto(activity,ture,9,16);
```

2.接收结果信息
``` kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data==null)return
    if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_IMAGE) {
        val pics = data?.getSerializableExtra(ImagePicker.REQUEST_OUTPUT) as ArrayList<String>
       // do something
    }
}
```

1.3可拖拽下拉退出的图片预览
```kotlin
ImagePreviewActivity.start(context,imageList)
```

