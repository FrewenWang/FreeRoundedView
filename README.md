# FreeRoundedView
实现自定义圆角View。共有两种方法。一种是在xml文件中进行定义。一种是通过代码实现

#### 代码实现 ####

通过代码实现。我们可以通过draw放对任何ViewGroup和View进行圆角重绘。实现圆角功能。
如：RoundedRelativeLayout、RoundedImageView

#### xml文件实现 ####
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#99CCFF" />

    <corners
        android:bottomLeftRadius="10dp"
        android:bottomRightRadius="10dp"
        android:topLeftRadius="10dp"
        android:topRightRadius="10dp" />

    <stroke
        android:width="1dp"
        android:color="#000000" />

</shape>

```
相对比较简单，灵活