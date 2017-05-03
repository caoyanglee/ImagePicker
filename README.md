1) 调用ImageSelectorActivity

```java
ImageSelectorActivity.start(MainActivity.this, maxSelectNum, mode, isShow,isPreview,isCrop);
```
same this

```java
public static void start(Activity activity, int maxSelectNum, int mode, boolean isShow, boolean enablePreview, boolean enableCrop) {
    Intent intent = new Intent(activity, ImageSelectorActivity.class);
    intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
    intent.putExtra(EXTRA_SELECT_MODE, mode);
    intent.putExtra(EXTRA_SHOW_CAMERA, isShow);
    intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
    intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
    activity.startActivityForResult(intent, REQUEST_IMAGE);
}
```
2)接收结果信息

``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
        ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
        // do something
    }
}
```
