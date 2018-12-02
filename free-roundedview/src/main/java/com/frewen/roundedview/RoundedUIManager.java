package com.frewen.roundedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * 文件名称: RoundedUIManager
 * 功能描述:
 *
 * @author: Frewen.Wong
 * 创建时间: 2018/12/1 22:17
 * 修改内容:
 * 修改时间:2018/12/1 22:17
 */
public class RoundedUIManager {

    public interface RoundedInterface {

        public void setRoundSize(int dimenSize);

        public void setRoundSize(int widthSize, int heightSize);

        public void drawSuper(Canvas canvas);

        public Context getContext();

        public int getWidth();

        public int getHeight();

        public RoundedUIManager getRoundedUIManager();

        public void setWillNotDraw(boolean willNotDraw);

        public void invalidate();
    }

    private static final int defaultRoundWidth = 0;
    private static final int defaultRoundHeight = 0;
    private float roundWidth;
    private float roundHeight;
    private boolean halfRound;
    private boolean adjustRoundSize = true;
    private Paint paint;
    private Paint borderPaint;
    private RoundedInterface view;
    private Context mContext;


    public RoundedUIManager(Context context, RoundedInterface view, AttributeSet attrs) {
        this.mContext = context;
        this.view = view;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        view.setWillNotDraw(false);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        // 获取像素密度
        float density = view.getContext().getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray a = view.getContext().obtainStyledAttributes(attrs, R.styleable.RoundedView);
            try {
                roundWidth = a.getDimensionPixelSize(R.styleable.RoundedView_roundedWidth, (int) (defaultRoundWidth * density));
                roundHeight = a.getDimensionPixelSize(R.styleable.RoundedView_roundedHeight, (int) (defaultRoundHeight * density));
            } catch (Exception ignore) {
                try {
                    roundWidth = a.getFraction(R.styleable.RoundedView_roundedWidth, 1, 1, roundWidth);
                    roundHeight = a.getFraction(R.styleable.RoundedView_roundedHeight, 1, 1, roundHeight);
                } catch (Exception ignored) {
                }
            }

            halfRound = a.getBoolean(R.styleable.RoundedView_halfRounded, false);
            adjustRoundSize = a.getBoolean(R.styleable.RoundedView_adjustRoundedSize, true);

            int borderWidth = a.getDimensionPixelSize(R.styleable.RoundedView_borderedWidth, 0);
            int borderColor = a.getColor(R.styleable.RoundedView_borderedColor, Color.BLACK);

            if (borderWidth > 0) {
                borderPaint = new Paint();
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(borderWidth);
                borderPaint.setColor(borderColor);
                borderPaint.setAntiAlias(true);
            }
        } else {
            roundWidth = (int) (defaultRoundWidth * density);
            roundHeight = (int) (defaultRoundHeight * density);
        }
    }

    /**
     * @param borderWidth
     * @param borderColor
     */
    public void setBorderPaint(int borderWidth, int borderColor) {
        if (borderPaint == null) {
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setAntiAlias(true);
        }
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        view.invalidate();
    }

    public void setRoundSize(int dimenSize) {
        setRoundSize(dimenSize, dimenSize);
    }

    public void setRoundSize(int dimenWidth, int dimenHeight) {
        this.roundWidth = mContext.getResources().getDimensionPixelOffset(dimenWidth);
        this.roundHeight = mContext.getResources().getDimensionPixelOffset(dimenHeight);
        view.invalidate();
    }

    public void draw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();

        if (width <= 0 || height <= 0) {
            return;
        }
        if (roundHeight < 1) {
            roundHeight = (int) (height * roundHeight);
        }

        if (roundWidth < 1) {
            roundWidth = (int) (width * roundWidth);
        }

        if (halfRound) {
            roundHeight = height / 2;
            roundWidth = width / 2;
        }

        if (adjustRoundSize) {
            roundWidth = Math.min(roundWidth, roundHeight);
            roundHeight = Math.min(roundWidth, roundHeight);
        }

        if (roundHeight == 0 && roundWidth == 0 || roundHeight < 0 || roundWidth < 0) {
            view.drawSuper(canvas);
            drawBorder(canvas);
            return;
        }
        // saveFlags参数说明：
        // 1.ALL_SAVE_FLAG（默认）：保存全部状态
        // 2. CLIP_SAVE_FLAG：保存剪辑区
        // 3. CLIP_TO_LAYER_SAVE_FLAG：剪裁区作为图层保存
        // 4. FULL_COLOR_LAYER_SAVE_FLAG：保存图层的全部色彩通道
        // 5. HAS_ALPHA_LAYER_SAVE_FLAG：保存图层的alpha(不透明度)通道
        // 6. MATRIX_SAVE_FLAG：保存Matrix信息(translate, rotate, scale, skew)

        // 每调用一次save（），都会在栈顶添加一条状态信息（入栈）
        int count = canvas.save();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        int count2 = canvas2.saveLayer(0, 0, width, height, null, ALL_SAVE_FLAG);
        view.drawSuper(canvas2);

        clipLeftUp(canvas2);
        clipRightUp(canvas2);
        clipLeftDown(canvas2);
        clipRightDown(canvas2);

        canvas2.restoreToCount(count2);
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
        drawBorder(canvas);
        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        // 设置borderPaint，画边界。
        if (null != borderPaint) {
            int halfStroke = (int) (borderPaint.getStrokeWidth() / 2 + .5f);
            canvas.drawRoundRect(new RectF(halfStroke, halfStroke, getWidth() - halfStroke, getHeight() - halfStroke),
                    roundWidth, roundHeight, borderPaint);
        }
    }

    private void clipLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        RectF arc = new RectF(0, 0, roundWidth * 2, roundHeight * 2);
        float startAngle = -90;
        float sweepAngle = -90;
        path.arcTo(arc, startAngle, sweepAngle);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void clipLeftDown(Canvas canvas) {
        int height = getHeight();

        Path path = new Path();
        path.moveTo(0, height - roundHeight);
        path.lineTo(0, height);
        path.lineTo(roundWidth, height);
        RectF arc = new RectF(0, height - roundHeight * 2, roundWidth * 2, getHeight());
        float startAngle = 90;
        float sweepAngle = 90;
        path.arcTo(arc, startAngle, sweepAngle);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void clipRightDown(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();

        Path path = new Path();
        path.moveTo(width - roundWidth, height);
        path.lineTo(width, height);
        path.lineTo(width, height - roundHeight);
        RectF arc = new RectF(width - roundWidth * 2, height - roundHeight * 2, width, height);
        float startAngle = 0;
        float sweepAngle = 90;
        path.arcTo(arc, startAngle, sweepAngle);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void clipRightUp(Canvas canvas) {
        int width = getWidth();

        Path path = new Path();
        path.moveTo(width, roundHeight);
        path.lineTo(width, 0);
        path.lineTo(width - roundWidth, 0);
        RectF arc = new RectF(width - roundWidth * 2, 0, width, roundHeight * 2);
        float startAngle = -90;
        float sweepAngle = 90;
        path.arcTo(arc, startAngle, sweepAngle);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap      需要转化成圆角的位图
     * @param roundWidth  圆角的度数，数值越大，圆角越大
     * @param roundHeight 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int roundWidth, int roundHeight) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int count = canvas.save();
        final Paint paint = new Paint();
        final RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundWidth, roundHeight, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restoreToCount(count);
        return output;
    }

    public int getHeight() {
        return view.getHeight();
    }

    public int getWidth() {
        return view.getWidth();
    }
}