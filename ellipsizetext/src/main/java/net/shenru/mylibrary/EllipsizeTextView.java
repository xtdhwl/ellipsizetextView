package net.shenru.mylibrary;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 自定义省略号textview
 *
 * @author xtdhwl
 * @date 2020/6/16 6:58 AM
 */
public class EllipsizeTextView extends AppCompatTextView {

    // private static final String TAG = EllipsizeTextView.class.getSimpleName();

    private EllipsizeTexHelper mTexHelper;

    public EllipsizeTextView(Context context) {
        this(context, null);
        init();
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTexHelper = new EllipsizeTexHelper(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mTexHelper.autoText();
    }

    /**
     * @param text
     * @param ellipsis
     */
    public void setText(final CharSequence text, final CharSequence ellipsis) {
        mTexHelper.setEllipsis(ellipsis);
        mTexHelper.setOriginalText(text);
        setEllipsize(null);
        setText(text);
        // mTexHelper.setTextStr(this, text);
        mTexHelper.autoText();
    }
}
