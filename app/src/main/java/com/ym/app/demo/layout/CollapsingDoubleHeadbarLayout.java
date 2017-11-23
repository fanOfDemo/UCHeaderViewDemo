package com.ym.app.demo.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.ym.app.demo.R;

import java.util.ArrayList;

/**
 * Created by wengyiming on 2017/11/23.
 * 仿照CollapsingToolbarLayout 实现的UC浏览器首页头部动画效果
 */

public class CollapsingDoubleHeadbarLayout extends FrameLayout {


    private static final int[] APPCOMPAT_CHECK_ATTRS = {
            android.support.v7.appcompat.R.attr.colorPrimary
    };
    private ArrayList<OnOffsetChangedListener> mListeners;

    static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }

    private boolean mRefreshToolbar = true;
    private boolean mSetMinHeight = true;
    private int topLayoutId;
    private int bootomLayoutId;
    private View topLayout;
    private View bootomLayout;
    WindowInsetsCompat mLastInsets;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;


    public CollapsingDoubleHeadbarLayout(Context context) {
        this(context, null);
    }

    public CollapsingDoubleHeadbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingDoubleHeadbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        checkAppCompatTheme(context);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CollapsingDoubleHeadbarLayout, defStyleAttr,
                android.support.design.R.style.Widget_Design_CollapsingToolbar);

        topLayoutId = a.getResourceId(R.styleable.CollapsingDoubleHeadbarLayout_topLayoutId, -1);
        bootomLayoutId = a.getResourceId(R.styleable.CollapsingDoubleHeadbarLayout_bootomLayoutId, -1);

        a.recycle();

        setWillNotDraw(false);

        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        return onWindowInsetChanged(insets);
                    }
                });


    }


    private void ensureTopBottomLayout() {
        if (!mRefreshToolbar) {
            return;
        }

        // First clear out the current Toolbar
        topLayout = null;
        bootomLayout = null;

        if (topLayoutId != -1) {
            // If we have an ID set, try and find it and it's direct parent to us
            topLayout = findViewById(topLayoutId);
            LayoutParams layoutParams = (LayoutParams) topLayout.getLayoutParams();
            layoutParams.gravity = Gravity.TOP;
        }

        if (bootomLayoutId != -1) {
            // If we have an ID set, try and find it and it's direct parent to us
            bootomLayout = findViewById(bootomLayoutId);
            LayoutParams layoutParams = (LayoutParams) bootomLayout.getLayoutParams();
            layoutParams.gravity = Gravity.BOTTOM;
        }
        mRefreshToolbar = false;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ensureTopBottomLayout();
        // Add an OnOffsetChangedListener if possible
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new CollapsingDoubleHeadbarLayout.OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);

            // We're attached, so lets request an inset dispatch
            ViewCompat.requestApplyInsets(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }
        if (mListeners != null && mListeners.size() > 0) {
            for (OnOffsetChangedListener listener : mListeners) {
                removeOnOffsetChangedListener(listener);
            }
        }
        super.onDetachedFromWindow();
    }


    private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        OffsetUpdateListener() {
        }

        @Override
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            ensureTopBottomLayout();
            setMinHeight();
            float per = 0;
            if (topLayout != null && bootomLayout != null) {
                per = (float) -verticalOffset / (float) (layout.getHeight() - topLayout.getHeight() - bootomLayout.getHeight());
                topLayout.setTranslationY(topLayout.getHeight() * (per - 1) - verticalOffset);
                bootomLayout.setTranslationY(topLayout.getHeight() * (1 - per));
                bootomLayout.setAlpha(per);
                topLayout.setAlpha(per);
            } else if (topLayout != null) {
                per = (float) -verticalOffset / (float) (layout.getHeight() - topLayout.getHeight());
                topLayout.setTranslationY(topLayout.getHeight() * (per - 1) - verticalOffset);
                topLayout.setAlpha(per);
            } else if (bootomLayout != null) {
                per = (float) -verticalOffset / (float) (layout.getHeight() - bootomLayout.getHeight());
                bootomLayout.setTranslationY(topLayout.getHeight() * (1 - per));
                bootomLayout.setAlpha(per);
            }


            dispatchOffsetUpdates(per);
        }
    }

    void dispatchOffsetUpdates(float offset) {
        // Iterate backwards through the list so that most recently added listeners
        // get the first chance to decide
        if (mListeners != null) {
            for (int i = 0, z = mListeners.size(); i < z; i++) {
                final OnOffsetChangedListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when an {@link CollapsingDoubleHeadbarLayout}'s vertical
     * offset changes.
     */
    public interface OnOffsetChangedListener {
        /**
         * Called when the {@link CollapsingDoubleHeadbarLayout}'s layout offset has been changed. This allows
         * child views to implement custom behavior based on the offset (for instance pinning a
         * view at a certain y value).
         *
         * @param doubleHeadbarLayout the {@link CollapsingDoubleHeadbarLayout} which offset has changed
         * @param perOffset           the vertical offset for the parent {@link CollapsingDoubleHeadbarLayout}, in px
         */
        void onOffsetChanged(CollapsingDoubleHeadbarLayout doubleHeadbarLayout, float perOffset);
    }

    /**
     * Add a listener that will be called when the offset of this {@link CollapsingDoubleHeadbarLayout} changes.
     *
     * @param listener The listener that will be called when the offset changes.]
     */
    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove the previously added {@link CollapsingDoubleHeadbarLayout.OnOffsetChangedListener}.
     *
     * @param listener the listener to remove.
     */
    public void removeOnOffsetChangedListener(CollapsingDoubleHeadbarLayout.OnOffsetChangedListener listener) {
        if (mListeners != null && listener != null) {
            mListeners.remove(listener);
        }
    }


    private void setMinHeight() {
        if (!mSetMinHeight) return;
        if (topLayout != null && bootomLayout != null) {
            CollapsingDoubleHeadbarLayout.this.setMinimumHeight(topLayout.getHeight() + bootomLayout.getHeight());
        } else if (topLayout != null) {
            CollapsingDoubleHeadbarLayout.this.setMinimumHeight(topLayout.getHeight());
        } else if (bootomLayout != null) {
            CollapsingDoubleHeadbarLayout.this.setMinimumHeight(bootomLayout.getHeight());
        }
        mSetMinHeight = false;
    }


    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;

        if (ViewCompat.getFitsSystemWindows(this)) {
            // If we're set to fit system windows, keep the insets
            newInsets = insets;
        }

        // If our insets have changed, keep them and invalidate the scroll ranges...
        if (!ObjectsCompat.equals(mLastInsets, newInsets)) {
            mLastInsets = newInsets;
            requestLayout();
        }


        // Consume the insets. This is done so that child views with fitSystemWindows=true do not
        // get the default padding functionality from View
        return insets.consumeSystemWindowInsets();
    }


}
