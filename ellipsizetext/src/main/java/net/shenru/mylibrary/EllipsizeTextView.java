package net.shenru.mylibrary;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 自定义省略view
 *
 * @author xtdhwl
 * @date 2020/6/16 6:58 AM
 */
public class EllipsizeTextView extends AppCompatTextView {
//
//    private static final String ELLIPSIS_NORMAL = "\u2026"; // HORIZONTAL ELLIPSIS (…)
//    //
////
////    public static final int ALL_AVAILABLE = -1;
//    private int mMaxLines;
//
//    Runnable myRunnable = null;

    public EllipsizeTextView(Context context) {
        this(context, null);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public void setMaxLines(int maxlines) {
//        super.setMaxLines(maxlines);
//        mMaxLines = maxlines;
//    }
//
//
//    /**
//     * Ellipsize just the last line of text in this view and set the text to the
//     * new ellipsized value.
//     *
//     * @param text Text to set and ellipsize
//     */
//    public void setText(final CharSequence text, CharSequence ellipsis) {
//        if (myRunnable != null) {
//            removeCallbacks(myRunnable);
//        }
//        myRunnable = () -> {
//            try {
//                doSetText(text, ellipsis, getEllipsisWidth(ellipsis), true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (TBInit.isDebug()) {
//                    throw e;
//                }
//                setText(text);
//            }
//        };
//        post(myRunnable);
//    }
//
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (myRunnable != null) {
//            removeCallbacks(myRunnable);
//            myRunnable = null;
//        }
//    }
//
//    private CharSequence doSetText(final CharSequence text, CharSequence ellipsis, int offset, boolean check) {
//        if (text == null || text.length() == 0) {
//            return text;
//        }
//        setEllipsize(null);
//        setText(text);
//
//        int lineCount = getLineCount();
////        Logger.d("mMaxLines:%s,lineCount:%s", mMaxLines, lineCount);
//        if (lineCount < mMaxLines || TextUtils.isEmpty(ellipsis)) {
//            return text;
//        }
//        boolean isAddEllipsis = false;
//
////        if (avail == ALL_AVAILABLE) {
////            return text;
////        }
//        Layout layout = getLayout();
//        if (layout == null) {
//            final int w = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
//            layout = new StaticLayout(text, 0, text.length(), getPaint(), w, Layout.Alignment.ALIGN_NORMAL,
//                    1.0f, 0f, false);
//        }
//        // find the last line of text and chop it according to available space
//        final int lastLineStart = layout.getLineStart(mMaxLines - 1);
//
//        int textLast = getTextLast(text, lastLineStart);
//        CharSequence lastLineCharSequence = text.subSequence(lastLineStart, textLast);
//
//
//        final CharSequence remainder = TextUtils.ellipsize(lastLineCharSequence, getPaint(), getAvail(ellipsis, offset), TextUtils.TruncateAt.END);
//        // assemble just the text portion, without spans
//        final SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(text.toString(), 0, lastLineStart);
//        if (!TextUtils.isEmpty(remainder)) {
//            //结果一样, 并且长度相同
//            if (remainder == lastLineCharSequence && textLast == text.length()) {
//                String string = remainder.toString();
//                builder.append(string);
//            } else {
//                isAddEllipsis = true;
//                String string = remainder.toString();
//                final int fromIndex = string.length() - ELLIPSIS_NORMAL.length();
//                int indexOf = string.lastIndexOf(ELLIPSIS_NORMAL);
//                if (indexOf > -1 && indexOf >= fromIndex) {
//                    string = string.substring(0, indexOf);
//                }
//                builder.append(string);
//                builder.append(ellipsis);
//            }
//        }
//        // Now copy the original spans into the assembled string, modified for any ellipsizing.
//        //
//        // Merely assembling the Spanned pieces together would result in duplicate CharacterStyle
//        // spans in the assembled version if a CharacterStyle spanned across the lastLineStart
//        // offset.
//        if (text instanceof Spanned) {
//            final Spanned s = (Spanned) text;
//            final Object[] spans = s.getSpans(0, s.length(), Object.class);
//            final int destLen = builder.length();
//            for (int i = 0; i < spans.length; i++) {
//                final Object span = spans[i];
//                final int start = s.getSpanStart(span);
//                final int end = s.getSpanEnd(span);
//                final int flags = s.getSpanFlags(span);
//                if (start <= destLen) {
//                    builder.setSpan(span, start, Math.min(end, destLen), flags);
//                }
//            }
//        }
//        setText(builder);
//
////        if (isAddEllipsis && check) {
////            if (!checkText(builder, layout, ellipsis)) {
////                doSetText(text, ellipsis, getAvail(ellipsis, 0), false);
////            }
////        }
//        return builder;
//    }
//
//    private boolean checkText(final CharSequence checkText, Layout layout, CharSequence ellipsis) {
//        final int lastLineStart = layout.getLineStart(mMaxLines - 1);
//        final int lastLineEnd = layout.getLineEnd(mMaxLines - 1);
//        final int textLength = checkText.length();
//        CharSequence lastLineCharSequence = checkText.subSequence(lastLineStart, textLength);
//
//        String string = lastLineCharSequence.toString();
//        int indexOf = string.lastIndexOf(ellipsis.toString());
//        final int fromIndex = string.length() - ellipsis.length();
//        if (indexOf > -1 && indexOf >= fromIndex) {
//            Logger.d("lastLineStart:%s,lastLineEnd:%s,textLength:%s,checkText:%s,lastLineCharSequence:%s. true", lastLineStart, lastLineEnd, textLength, checkText, lastLineCharSequence);
//            return true;
//        }
//        Logger.d("lastLineStart:%s,lastLineEnd:%s,textLength:%s,checkText:%s,lastLineCharSequence:%s. false", lastLineStart, lastLineEnd, textLength, checkText, lastLineCharSequence);
//        return false;
//    }
//
//    private int getAvail(CharSequence ellipsis, int offset) {
//        if (ellipsis == null) {
//            return offset;
//        }
//        return (int) (getWidth() - (getPaint().measureText(ellipsis, 0, ellipsis.length()) + offset));
//    }
//
//    private int getEllipsisWidth(CharSequence ellipsis) {
//        if (ellipsis == null) {
//            return 0;
//        }
//        return (int) getPaint().measureText(ellipsis, 0, ellipsis.length());
//    }
//
//    private int getTextLast(final CharSequence text, int fromIndex) {
//        int indexOf = text.toString().lastIndexOf("\n");
//        if (indexOf > -1 && indexOf >= fromIndex) {
//            return indexOf;
//        }
//        return text.length();
//    }
}
