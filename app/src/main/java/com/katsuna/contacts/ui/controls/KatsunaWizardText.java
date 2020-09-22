/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.contacts.ui.controls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.contacts.R;

public class KatsunaWizardText extends LinearLayout {
    private ImageView mIcon;
    private Drawable mIconDrawable;
    private EditText mEditText;
    private ImageView mImageView;
    private String mHint;
    private int mInputType;
    private View mCardHolder;
    private boolean mRequired;
    private float mMarginTop;
    private float mMarginBottom;
    private String mRequiredMissing;
    private ImageView mWarningView;

    public KatsunaWizardText(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        initializeViews(context);
    }

    public KatsunaWizardText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(context, attrs);
        initializeViews(context);
    }

    public KatsunaWizardText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(context, attrs);
        initializeViews(context);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.KatsunaWizardText);
        mIconDrawable = typedArray.getDrawable(R.styleable.KatsunaWizardText_field_icon);
        mHint = typedArray.getString(R.styleable.KatsunaWizardText_android_hint);
        mInputType = typedArray.getInt(R.styleable.KatsunaWizardText_android_inputType,
                EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        mRequired = typedArray.getBoolean(R.styleable.KatsunaWizardText_field_required, false);
        mRequiredMissing = typedArray.getString(R.styleable.KatsunaWizardText_field_required_missing);
        mMarginTop = typedArray.getDimension(R.styleable.KatsunaWizardText_field_margin_top, 0);
        mMarginBottom = typedArray.getDimension(R.styleable.KatsunaWizardText_field_margin_bottom, 0);

        typedArray.recycle();
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.katsuna_wizard_text, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCardHolder = findViewById(R.id.card_holder);
        View mEditTextWrapper = findViewById(R.id.edit_text_wrapper);

        if (mIconDrawable != null) {
            mIcon = findViewById(R.id.icon);
            mIcon.setImageDrawable(mIconDrawable);
            mIcon.setVisibility(VISIBLE);
        }

        mEditText = findViewById(R.id.edit_text);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setImageVisibility(hasFocus ? VISIBLE : GONE);
            }
        });
        mEditText.setHint(mHint);
        mEditText.setInputType(mInputType);

        if (mMarginTop > 0) {
            MarginLayoutParams lp = (MarginLayoutParams) mEditTextWrapper.getLayoutParams();
            lp.topMargin = (int) mMarginTop;
        }
        if (mMarginBottom > 0) {
            MarginLayoutParams lp = (MarginLayoutParams) mEditTextWrapper.getLayoutParams();
            lp.bottomMargin = (int) mMarginBottom;
        }

        mImageView = findViewById(R.id.image_view);
        mWarningView = findViewById(R.id.warning_view);
    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setTextColor(int color) {
        mEditText.setTextColor(color);
    }

    public void setError(int color) {
        DrawUtils.setColor(mIcon.getDrawable(), color);
        showMissingHint(true);
        mCardHolder.setBackgroundColor(color);
        mWarningView.setVisibility(VISIBLE);
    }

    public void clearError(int cardHolderColor, int color) {
        DrawUtils.setColor(mIcon.getDrawable(), color);
        showMissingHint(false);
        mCardHolder.setBackgroundColor(cardHolderColor);
        mWarningView.setVisibility(GONE);
    }

    public void setOnFocus(OnFocusChangeListener listener) {
        mEditText.setOnFocusChangeListener(listener);
    }

    public void setTextChangedListener(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public void showMissingHint(boolean flag) {
        if (flag) {
            if (!TextUtils.isEmpty(mRequiredMissing)) {
                mEditText.setHint(mRequiredMissing);
                mEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.common_black87));
            }
        } else {
            mEditText.setHint(mHint);
            mEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.common_black34));
        }
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setWarningDrawable(Drawable drawable) {
        mWarningView.setImageDrawable(drawable);
    }


    public void setImageVisibility(int visibility) {
        mImageView.setVisibility(visibility);
    }

    public void setImageOnClickListener(OnClickListener listener) {
        mImageView.setOnClickListener(listener);
    }

    public void setCardHolderColor(int color) {
        mCardHolder.setBackgroundColor(color);
    }


    public boolean isRequired() {
        return mRequired;
    }

    @Override
    public boolean isFocused() {
        return mEditText.isFocused();
    }

    public void setUnderlineColor(int color) {
        mEditText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void requestFocusOnEditText() {
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}
