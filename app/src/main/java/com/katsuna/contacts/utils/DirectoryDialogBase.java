package com.katsuna.contacts.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorAdjuster;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.katsuna.contacts.R;

public class DirectoryDialogBase {

    protected final Context m_context;

    protected UserProfileContainer mProfileContainer;
    protected LinearLayout.LayoutParams mLayoutParamsMini;
    protected int mFullMargin;
    protected int mHalfMargin;

    public DirectoryDialogBase(Context context) {
        m_context = context;
    }

    protected void init() {
        // profile and common dimens init
        mProfileContainer = ProfileReader.getKatsunaUserProfile(m_context);
        mLayoutParamsMini = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mFullMargin = m_context.getResources()
                .getDimensionPixelSize(R.dimen.export_dialog_full_margin);
        mHalfMargin = m_context.getResources()
                .getDimensionPixelSize(R.dimen.export_dialog_half_margin);
        mLayoutParamsMini.setMargins(mFullMargin, mHalfMargin, mFullMargin, mHalfMargin);
    }

    protected TextView createTitle(int titleId) {
        TextView title = new TextView(m_context);
        title.setAllCaps(false);
        title.setLayoutParams(mLayoutParamsMini);
        title.setText(titleId);

        // adjust profile
        OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                mProfileContainer.getOpticalSizeProfile());
        SizeAdjuster.adjustText(m_context, title, opticalParams);
        return title;
    }

    protected TextView createDescription(int descriptionId) {
        TextView description = new TextView(m_context);
        description.setAllCaps(false);
        description.setLayoutParams(mLayoutParamsMini);
        description.setText(descriptionId);

        // adjust profile
        OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.SUBHEADER,
                mProfileContainer.getOpticalSizeProfile());
        SizeAdjuster.adjustText(m_context, description, opticalParams);
        return description;
    }

    protected void adjustButtons(Button positiveButton, Button negativeButton) {
        // adjust button container
        adjustButtonContainer(negativeButton);

        // adjust button
        adjustNegativeButton(negativeButton);
        adjustButtonMargins(negativeButton);
        adjustPositiveButton(positiveButton);
    }

    private void adjustButtonMargins(Button button) {
        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) button.getLayoutParams();
        lp.setMargins(mFullMargin, 0, mFullMargin, 0);
    }

    protected void adjustButtonContainer(Button button) {
        int bgColor = ContextCompat.getColor(m_context, R.color.common_grey300);

        LinearLayout buttonsContainer = ((LinearLayout) button.getParent());
        buttonsContainer.setBackgroundColor(bgColor);
        buttonsContainer.setPadding(mFullMargin, mFullMargin, mFullMargin, mFullMargin);

    }

    protected void adjustPositiveButton(Button button) {
        button.setAllCaps(false);

        OpticalParams opticalParams =
                SizeCalc.getOpticalParams(SizeProfileKey.ACTION_BUTTON,
                        mProfileContainer.getOpticalSizeProfile());
        SizeAdjuster.adjustButton(m_context, button, opticalParams);


        SizeAdjuster.adjustText(m_context, button, opticalParams);
        ColorAdjuster.adjustSecondaryButton(m_context, mProfileContainer.getColorProfile(),
                button);
    }

    protected void adjustNegativeButton(Button button) {
        button.setAllCaps(false);
        OpticalParams opticalParams =
                SizeCalc.getOpticalParams(SizeProfileKey.ACTION_BUTTON,
                        mProfileContainer.getOpticalSizeProfile());
        SizeAdjuster.adjustButton(m_context, button, opticalParams);
        SizeAdjuster.adjustText(m_context, button, opticalParams);
        ColorAdjuster.adjustPrimaryButton(m_context, mProfileContainer.getColorProfile(),
                button);
    }

}
