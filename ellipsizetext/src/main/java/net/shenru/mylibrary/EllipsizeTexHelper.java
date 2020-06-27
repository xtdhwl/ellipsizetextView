package net.shenru.mylibrary;

import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 参考:EllipsizedMultilineTextView和AppCompatTextHelper
 *
 * @author xtdhwl
 * @date 2020/6/27 7:55 AM
 */
public class EllipsizeTexHelper {
    private static final String TAG = "EllipsizeTexHelper";

    private static final RectF TEMP_RECTF = new RectF();

    // horizontal scrolling is activated.
    private static final int VERY_WIDE = 1024 * 1024;

    // Specify if auto-size text is needed.
    private boolean mNeedsAutoSizeText = false;

    private final TextView mTextView;
    private final Context mContext;

    private CharSequence ellipsis;
    private CharSequence originalText;

    // Cache of TextView methods used via reflection; the key is the method name and the value is
    // the method itself or null if it can not be found.
    private static ConcurrentHashMap<String, Method> sTextViewMethodByNameCache =
        new ConcurrentHashMap<>();

    class TextPosition {
        //循环次数
        int count = 0;
        //长度
        int length = 0;
    }

    EllipsizeTexHelper(TextView textView) {
        mTextView = textView;
        mContext = mTextView.getContext();
    }

    public CharSequence getEllipsis() {
        return ellipsis;
    }

    public void setEllipsis(CharSequence ellipsis) {
        this.ellipsis = ellipsis;
    }

    public void setOriginalText(CharSequence originalText) {
        this.originalText = originalText;
        this.mNeedsAutoSizeText = true;
    }

    public void autoSizeText() {
        if (this.ellipsis == null) {
            return;
        }

        if (this.originalText == null) {
            return;
        }

        if (!mNeedsAutoSizeText) {
            return;
        }

        if (mTextView.getMeasuredHeight() <= 0 || mTextView.getMeasuredWidth() <= 0) {
            return;
        }

        final boolean horizontallyScrolling = Build.VERSION.SDK_INT >= 29
            ? mTextView.isHorizontallyScrollable()
            : invokeAndReturnWithDefault(mTextView, "getHorizontallyScrolling", false);
        final int availableWidth = horizontallyScrolling
            ? VERY_WIDE
            : mTextView.getMeasuredWidth() - mTextView.getTotalPaddingLeft() - mTextView.getTotalPaddingRight();
        final int availableHeight = mTextView.getHeight() - mTextView.getCompoundPaddingBottom() -
                                    mTextView.getCompoundPaddingTop();

        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        synchronized (TEMP_RECTF) {
            TEMP_RECTF.setEmpty();
            TEMP_RECTF.right = availableWidth;
            TEMP_RECTF.bottom = availableHeight;

            if (isNeedAdjust(TEMP_RECTF)) {
                CharSequence optimalTextSize = findLargestTextSizeWhichFits(TEMP_RECTF);
                if (optimalTextSize != mTextView.getText()) {
                    setTextStr(mTextView, optimalTextSize);
                }
            }
        }
    }

    private void setTextStr(TextView textView, CharSequence str) {
        textView.setText(str);
        boolean isInLayout = false;
        if (Build.VERSION.SDK_INT >= 18) {
            isInLayout = mTextView.isInLayout();
        }

        if (mTextView.getLayout() != null) {
            // Do not auto-size right after setting the text size.
            mNeedsAutoSizeText = false;

            final String methodName = "nullLayouts";
            try {
                Method method = getTextViewMethod(methodName);
                if (method != null) {
                    method.invoke(mTextView);
                }
            } catch (Exception ex) {
                Log.w(TAG, "Failed to invoke TextView#" + methodName + "() method", ex);
            }

            if (!isInLayout) {
                mTextView.requestLayout();
            } else {
                mTextView.forceLayout();
            }

            mTextView.invalidate();
        }
    }

    private CharSequence findLargestTextSizeWhichFits(RectF availableSpace) {

        final CharSequence text = originalText;

        TextPosition position = new TextPosition();
        position.length = text.length();
        while (!suggestedSizeFitsInSpace(text, availableSpace, position)) {
            position.count++;
        }

        if (position.count == 0) {
            return text;
        }

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text.toString(), 0, position.length);
        builder.append(ellipsis);

        if (text instanceof Spanned) {
            final Spanned s = (Spanned)text;
            final Object[] spans = s.getSpans(0, s.length(), Object.class);
            final int destLen = builder.length();
            for (int i = 0; i < spans.length; i++) {
                final Object span = spans[i];
                final int start = s.getSpanStart(span);
                final int end = s.getSpanEnd(span);
                final int flags = s.getSpanFlags(span);
                if (start <= destLen) {
                    builder.setSpan(span, start, Math.min(end, destLen), flags);
                }
            }
        }
        return builder;
    }

    /**
     * 判断是否需要调整
     *
     * @param availableSpace space
     * @return true需要, 否则false
     */
    private boolean isNeedAdjust(RectF availableSpace) {
        // CharSequence builder = mTextView.getText();
        CharSequence builder = originalText;

        final int maxLines = Build.VERSION.SDK_INT >= 16 ? mTextView.getMaxLines() : -1;
        // Needs reflection call due to being private.
        Layout.Alignment alignment = invokeAndReturnWithDefault(
            mTextView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
        final StaticLayout layout = createLayout(builder, alignment, Math.round(availableSpace.right),
            maxLines);

        // Lines overflow.
        if (maxLines != -1 && (layout.getLineCount() > maxLines
                               || (layout.getLineEnd(layout.getLineCount() - 1)) != builder.length())) {
            return true;
        }
        return false;
    }

    private boolean suggestedSizeFitsInSpace(CharSequence text, RectF availableSpace, TextPosition position) {
        position.length -= position.count;

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text.toString(), 0, position.length);
        builder.append(ellipsis);

        final int maxLines = Build.VERSION.SDK_INT >= 16 ? mTextView.getMaxLines() : -1;

        // Needs reflection call due to being private.
        Layout.Alignment alignment = invokeAndReturnWithDefault(
            mTextView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
        final StaticLayout layout = createLayout(builder, alignment, Math.round(availableSpace.right),
            maxLines);
        // Lines overflow.
        if (maxLines != -1 && (layout.getLineCount() > maxLines
                               || (layout.getLineEnd(layout.getLineCount() - 1)) != builder.length())) {
            position.length = layout.getLineEnd(maxLines - 1);
            return false;
        }

        // Height overflow.
        // if (layout.getHeight() > availableSpace.bottom) {
        //     return false;
        // }

        return true;
    }

    StaticLayout createLayout(CharSequence text, Layout.Alignment alignment, int availableWidth,
        int maxLines) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return createStaticLayoutForMeasuring(text, alignment, availableWidth, maxLines);
        }
        return createStaticLayoutForMeasuringPre23(text, alignment, availableWidth);
    }

    @RequiresApi ( 23 )
    private StaticLayout createStaticLayoutForMeasuring(CharSequence text,
        Layout.Alignment alignment, int availableWidth, int maxLines) {

        final StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(
            text, 0, text.length(), mTextView.getPaint(), availableWidth);

        layoutBuilder.setAlignment(alignment)
                     .setLineSpacing(
                         mTextView.getLineSpacingExtra(),
                         mTextView.getLineSpacingMultiplier())
                     .setIncludePad(mTextView.getIncludeFontPadding())
                     .setBreakStrategy(mTextView.getBreakStrategy())
                     .setHyphenationFrequency(mTextView.getHyphenationFrequency())
                     .setMaxLines(maxLines == -1 ? Integer.MAX_VALUE : maxLines);

        try {
            // Can use the StaticLayout.Builder (along with TextView params added in or after
            // API 23) to construct the layout.
            final TextDirectionHeuristic textDirectionHeuristic = Build.VERSION.SDK_INT >= 29
                ? mTextView.getTextDirectionHeuristic()
                : invokeAndReturnWithDefault(mTextView, "getTextDirectionHeuristic",
                TextDirectionHeuristics.FIRSTSTRONG_LTR);
            layoutBuilder.setTextDirection(textDirectionHeuristic);
        } catch (ClassCastException e) {
            // On some devices this exception happens, details: b/127137059.
            Log.w(TAG, "Failed to obtain TextDirectionHeuristic, auto size may be incorrect");
        }
        return layoutBuilder.build();
    }

    @RequiresApi ( 16 )
    private StaticLayout createStaticLayoutForMeasuringPre23(CharSequence text,
        Layout.Alignment alignment, int availableWidth) {
        final float lineSpacingMultiplier = mTextView.getLineSpacingMultiplier();
        final float lineSpacingAdd = mTextView.getLineSpacingExtra();
        final boolean includePad = mTextView.getIncludeFontPadding();

        // The layout could not be constructed using the builder so fall back to the
        // most broad constructor.
        return new StaticLayout(text, mTextView.getPaint(), availableWidth,
            alignment,
            lineSpacingMultiplier,
            lineSpacingAdd,
            includePad);
    }

    private static <T> T invokeAndReturnWithDefault(@NonNull Object object,
        @NonNull final String methodName, @NonNull final T defaultValue) {
        T result = null;
        boolean exceptionThrown = false;

        try {
            // Cache lookup.
            Method method = getTextViewMethod(methodName);
            result = (T)method.invoke(object);
        } catch (Exception ex) {
            exceptionThrown = true;
            Log.w(TAG, "Failed to invoke TextView#" + methodName + "() method", ex);
        } finally {
            if (result == null && exceptionThrown) {
                result = defaultValue;
            }
        }

        return result;
    }

    @Nullable
    private static Method getTextViewMethod(@NonNull final String methodName) {
        try {
            Method method = sTextViewMethodByNameCache.get(methodName);
            if (method == null) {
                method = TextView.class.getDeclaredMethod(methodName);
                if (method != null) {
                    method.setAccessible(true);
                    // Cache update.
                    sTextViewMethodByNameCache.put(methodName, method);
                }
            }

            return method;
        } catch (Exception ex) {
            Log.w(TAG, "Failed to retrieve TextView#" + methodName + "() method", ex);
            return null;
        }
    }
}
