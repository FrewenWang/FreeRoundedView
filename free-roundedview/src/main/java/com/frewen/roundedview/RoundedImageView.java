package com.frewen.roundedview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 文件名称: RoundedImageView
 * 功能描述: 圆角ImageView 其他的View都是可以的。例如：TextView、Button等
 *
 * @author: Frewen.Wong
 * 创建时间: 2018/12/1 23:10
 */
@SuppressLint("AppCompatCustomView")
public class RoundedImageView extends ImageView implements RoundedUIManager.RoundedInterface {

    private Context mContext;
    RoundedUIManager mUIRoundedManager;

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public RoundedImageView(Context context) {
        super(context);
        this.mContext = context;
        init(null);

    }

    private void init(AttributeSet attrs) {
        mUIRoundedManager = new RoundedUIManager(mContext, this, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // we have to remove the hardware acceleration if we want the clip
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        mUIRoundedManager.draw(canvas);
    }

    @Override
    public void setRoundSize(int dimenSize) {
        mUIRoundedManager.setRoundSize(dimenSize);
    }

    @Override
    public void setRoundSize(int widthSize, int hightSize) {
        mUIRoundedManager.setRoundSize(widthSize, hightSize);
    }

    @Override
    public void drawSuper(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public RoundedUIManager getRoundedUIManager() {
        return mUIRoundedManager;
    }

    @Override
    public void refreshDrawableState() {
        super.refreshDrawableState();
        invalidate();
    }
}
