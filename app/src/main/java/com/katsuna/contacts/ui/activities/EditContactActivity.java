package com.katsuna.contacts.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.commons.domain.Address;
import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Description;
import com.katsuna.commons.domain.Email;
import com.katsuna.commons.domain.Name;
import com.katsuna.commons.domain.Phone;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.Constants;
import com.katsuna.commons.utils.DataAction;
import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.contacts.R;
import com.katsuna.commons.data.ContactsInfoCache;
import com.katsuna.contacts.ui.controls.KatsunaWizardText;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends PhotoActivity {

    private KatsunaWizardText[] mTelephones;
    private KatsunaWizardText[] mAllControls;
    private KatsunaWizardText mName;
    private KatsunaWizardText mSurname;
    private KatsunaWizardText mDescription;
    private KatsunaWizardText mTelephone1;
    private KatsunaWizardText mTelephone2;
    private KatsunaWizardText mTelephone3;
    private KatsunaWizardText mEmail;
    private KatsunaWizardText mAddress;
    private RoundedImageView mPhoto;
    private Contact mContact;
    private TextView mAddPhotoText;
    private CardView mContactContainerCard;
    private boolean mCreateMode;
    private int mPrimaryColor1;
    private int mPrimaryColor2;
    private int mSecondaryColor3;
    private LayerDrawable mDoneDrawableOk;
    private Drawable mWarningDrawable;
    private int mBlack54;
    private int mBlack34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        initControls();
        loadContact();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustColorProfile();
        readIncomingNumberToAdd();
        if (mCreateMode) {
            mName.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mName.requestFocusOnEditText();
                }
            }, 100);
        }
    }

    private void readIncomingNumberToAdd() {
        Intent i = getIntent();
        String action = i.getAction();
        if (Constants.ADD_TO_CONTACT_ACTION.equals(action)) {
            String number = i.getStringExtra(Constants.ADD_TO_CONTACT_ACTION_NUMBER);
            if (!TextUtils.isEmpty(number)) {
                String output = "";
                if (TextUtils.isEmpty(mTelephone1.getText())) {
                    mTelephone1.setText(number);
                    mTelephone1.requestFocus();
                } else if (TextUtils.isEmpty(mTelephone2.getText())) {
                    mTelephone2.setText(number);
                    mTelephone2.requestFocus();
                } else if (TextUtils.isEmpty(mTelephone3.getText())) {
                    mTelephone3.setText(number);
                    mTelephone3.requestFocus();
                } else {
                    output = getResources().getString(R.string.no_free_telephone_fields);
                }
                Toast.makeText(this, output, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void showPopup(boolean b) {
        // no op here
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_close_black54_24dp);

        mBlack54 = ContextCompat.getColor(EditContactActivity.this, R.color.common_black54);
        mBlack34 = ContextCompat.getColor(EditContactActivity.this, R.color.common_black34);

        mContactContainerCard = findViewById(R.id.contact_container_card);
        mName = findViewById(R.id.name);
        mSurname = findViewById(R.id.surname);
        mDescription = findViewById(R.id.description);
        mTelephone1 = findViewById(R.id.telephone1);
        mTelephone2 = findViewById(R.id.telephone2);
        mTelephone3 = findViewById(R.id.telephone3);
        mTelephones = new KatsunaWizardText[]{mTelephone1, mTelephone2, mTelephone3};
        mEmail = findViewById(R.id.email);
        mAddress = findViewById(R.id.address);
        assignBehavior(mName, mSurname);
        assignBehavior(mSurname, mDescription);
        assignBehavior(mDescription, mTelephone1);
        assignBehavior(mTelephone1, mTelephone2);
        assignBehavior(mTelephone2, mTelephone3);
        assignBehavior(mTelephone3, mEmail);
        assignBehavior(mEmail, mAddress);
        assignBehavior(mAddress, null);

        mAllControls = new KatsunaWizardText[]{mName, mSurname, mDescription, mTelephone1,
                mTelephone2, mTelephone3, mEmail, mAddress};

        mPhoto = findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        mAddPhotoText = findViewById(R.id.add_photo_text);
        mAddPhotoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void assignBehavior(KatsunaWizardText wizardText, KatsunaWizardText nextWizardText) {
        KatsunaWizardBehavior behavior = new KatsunaWizardBehavior(wizardText, nextWizardText);
        wizardText.setOnFocus(behavior);
        wizardText.setTextChangedListener(behavior);
    }

    private void loadContact() {
        long contactId = getIntent().getLongExtra("contactId", 0);

        if (contactId == 0) {
            mCreateMode = true;
            mContact = new Contact();
            mContact.initialize();

            // handle incoming creation intents
            Intent incomingIntent = getIntent();
            if (incomingIntent.getAction() != null &&
                    incomingIntent.getAction().equals(Constants.CREATE_CONTACT_ACTION)) {
                mTelephone1.setText(incomingIntent.getStringExtra("number"));
            }
            if (incomingIntent.getAction() != null &&
                    incomingIntent.getAction().equals(Intent.ACTION_INSERT)) {
                mTelephone1.setText(incomingIntent.getStringExtra(ContactsContract.Intents.Insert.PHONE));
                mName.setText(incomingIntent.getStringExtra(ContactsContract.Intents.Insert.NAME));
            }

            setTitle(R.string.common_create_contact);

            return;
        }
        ContactProvider contactProvider = new ContactProvider(this);

        mContact = contactProvider.getContact(contactId);

        if (mContact.isEmpty()) {
            Toast.makeText(this, R.string.common_no_contact, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //set data on fields
        mName.setText(mContact.getName().getName());
        mSurname.setText(mContact.getName().getSurname());
        if (mContact.getDescription() != null) {
            mDescription.setText(mContact.getDescription().getDescription());
        }

        loadPhoneNumbers();
        loadEmail();
        loadAddress();
        if (mContact.getPhoto() != null) {
            loadPhoto(mContact.getPhotoUri());
        }
    }

    private void loadPhoneNumbers() {
        for (int i = 0; i < mTelephones.length; i++) {
            if (mContact.getPhone(i) != null) {
                mTelephones[i].setText(mContact.getPhone(i).getNumber());
            }
        }
    }

    private void loadEmail() {
        if (mContact.getEmail() != null) {
            mEmail.setText(mContact.getEmail().getAddress());
        }
    }

    private void loadAddress() {
        if (mContact.getAddress() != null) {
            mAddress.setText(mContact.getAddress().getFormattedAddress());
        }
    }

    private void updateContact() {
        if (inputIsValid()) {
            mContact.setName(getNameForUpdate());
            mContact.setPhones(getPhonesForUpdate());
            mContact.setEmail(getEmailForUpdate());
            mContact.setAddress(getAddressForUpdate());
            mContact.setDescription(getDescriptionForUpdate());
            if (mPhoto.getDrawable() != null) {
                Bitmap bitmap = ((RoundedDrawable) mPhoto.getDrawable()).getSourceBitmap();
                mContact.setPhoto(bitmap);
            }

            ContactProvider contactProvider = new ContactProvider(EditContactActivity.this);
            if (mCreateMode) {
                contactProvider.addContact(mContact);
            } else {
                // invalidate cache to reflect new changes
                ContactsInfoCache.invalidateContact(mContact.getId());
                contactProvider.updateContact(mContact);
            }

            Intent intent = new Intent();
            intent.putExtra("contactId", mContact.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean inputIsValid() {
        boolean output = true;
        String popupMessage = "";
        if (mName.getText().length() == 0 && mSurname.getText().length() == 0) {
            mName.setError(mPrimaryColor1);
            popupMessage = getString(R.string.missing_required_contact_name_fields);
            output = false;
        }
        if (mTelephone1.getText().length() == 0) {
            mTelephone1.setError(mPrimaryColor1);
            if (popupMessage.length() > 0) {
                popupMessage += "\n";
            }
            popupMessage += getString(R.string.missing_required_contact_telephone_field);
            output = false;
        }

        // adjust presentation for not required fields
        mSurname.setCardHolderColor(mPrimaryColor2);
        mDescription.setCardHolderColor(mPrimaryColor2);
        mTelephone2.setCardHolderColor(mPrimaryColor2);
        mTelephone3.setCardHolderColor(mPrimaryColor2);
        mEmail.setCardHolderColor(mPrimaryColor2);
        mAddress.setCardHolderColor(mPrimaryColor2);

        if (!output) {
            KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
            builder.setTitle(getString(R.string.common_missing_fields));
            builder.setMessage(popupMessage);
            builder.setView(R.layout.common_katsuna_alert);
            builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
            builder.setCancelHidden(true);
            builder.setOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            builder.create().show();
        }

        return output;
    }

    private Name getNameForUpdate() {
        Name name = mContact.getName();
        name.setName(mName.getText());
        name.setSurname(mSurname.getText());
        return name;
    }

    private List<Phone> getPhonesForUpdate() {
        List<Phone> phones = new ArrayList<>();

        for (int i = 0; i < mTelephones.length; i++) {
            Phone phone = null;
            if (mContact.getPhone(i) == null) {
                if (!TextUtils.isEmpty(mTelephones[i].getText())) {
                    phone = new Phone(mTelephones[i].getText());
                    phone.setDataAction(DataAction.CREATE);
                }
            } else {
                phone = mContact.getPhone(i);
                if (TextUtils.isEmpty(mTelephones[i].getText())) {
                    phone.setDataAction(DataAction.DELETE);
                } else {
                    phone.setNumber(mTelephones[i].getText());
                    phone.setDataAction(DataAction.UPDATE);
                }
            }

            //add only if phone is set
            if (phone != null) {
                //1st phone is always primary
                if (i == 0) {
                    phone.setPrimary(true);
                }
                phones.add(phone);
            }
        }

        return phones;
    }

    private Email getEmailForUpdate() {
        Email email = null;

        if (mContact.getEmail() == null) {
            if (!TextUtils.isEmpty(mEmail.getText())) {
                email = new Email();
                email.setDataAction(DataAction.CREATE);
                email.setAddress(mEmail.getText());
            }
        } else {
            email = mContact.getEmail();
            if (TextUtils.isEmpty(mEmail.getText())) {
                email.setDataAction(DataAction.DELETE);
            } else {
                email.setDataAction(DataAction.UPDATE);
                email.setAddress(mEmail.getText());
            }
        }

        return email;
    }

    private Address getAddressForUpdate() {
        Address address = null;

        if (mContact.getAddress() == null) {
            if (!TextUtils.isEmpty(mAddress.getText())) {
                address = new Address();
                address.setDataAction(DataAction.CREATE);
                address.setFormattedAddress(mAddress.getText());
            }
        } else {
            address = mContact.getAddress();
            if (TextUtils.isEmpty(mAddress.getText())) {
                address.setDataAction(DataAction.DELETE);
            } else {
                address.setDataAction(DataAction.UPDATE);
                address.setFormattedAddress(mAddress.getText());
            }
        }

        return address;
    }

    private Description getDescriptionForUpdate() {
        Description description = null;

        if (mContact.getDescription() == null) {
            if (!TextUtils.isEmpty(mDescription.getText())) {
                description = new Description(mDescription.getText());
                description.setDataAction(DataAction.CREATE);
            }
        } else {
            description = mContact.getDescription();
            if (TextUtils.isEmpty(mDescription.getText())) {
                description.setDataAction(DataAction.DELETE);
            } else {
                description.setDataAction(DataAction.UPDATE);
                description.setDescription(mDescription.getText());
            }
        }

        return description;
    }

    @Override
    void loadPhoto(Uri uri) {
        Picasso.with(this).load(uri).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerCrop()
                .into(mPhoto);
        showAddPhotoInstructions(false);
    }

    @Override
    void removePhoto() {
        mPhoto.setImageDrawable(null);
        mContact.setPhoto(null);
        showAddPhotoInstructions(true);
    }

    private void showAddPhotoInstructions(boolean flag) {
        mAddPhotoText.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    private void adjustColorProfile() {
        UserProfile userProfile = mUserProfileContainer.getActiveUserProfile();
        mPrimaryColor1 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_1,
                userProfile.colorProfile);
        mPrimaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2,
                userProfile.colorProfile);
        int mSecondaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_COLOR_2,
                userProfile.colorProfile);
        mSecondaryColor3 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_COLOR_3,
                userProfile.colorProfile);

        mContactContainerCard.setCardBackgroundColor(ColorStateList.valueOf(mSecondaryColor2));

        for (KatsunaWizardText wizardText : mAllControls) {
            wizardText.setTextColor(mPrimaryColor2);
        }

        if (mPhoto.getDrawable() == null) {
            mPhoto.setBackground(getAddPhotoDrawable());
        }
        adjustWizardTextFieldsStep1();
    }

    /*
    Step 1 is when the activity is resumed. No edit is started yet.
     */
    private void adjustWizardTextFieldsStep1() {
        createNextItemDrawables();
        createWarningDrawable();

        for (KatsunaWizardText wizardText : mAllControls) {
            if (TextUtils.isEmpty(wizardText.getText())) {
                wizardText.setCardHolderColor(mSecondaryColor3);
            } else {
                wizardText.setCardHolderColor(mPrimaryColor2);
            }
            wizardText.setImageDrawable(mDoneDrawableOk);
            wizardText.setWarningDrawable(mWarningDrawable);
        }
    }

    private Drawable getAddPhotoDrawable() {
        // calc color and icon
        ColorProfile colorProfile = mUserProfileContainer.getActiveUserProfile().colorProfile;
        int secondaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_COLOR_2,
                colorProfile);

        // adjust circle
        GradientDrawable circleDrawable =
                (GradientDrawable) getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(secondaryColor2);
        }

        // adjust icon
        Drawable icon = getDrawable(R.drawable.ic_add_a_photo_black54_24dp);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = getResources().getDimensionPixelSize(R.dimen.add_a_photo_inset);
        layerDrawable.setLayerInset(1, diff, diff, diff, diff);
        return layerDrawable;
    }

    private void createNextItemDrawables() {
        GradientDrawable circleDrawable =
                (GradientDrawable) getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(mPrimaryColor2);
        }

        // adjust icon
        Drawable icon = getDrawable(R.drawable.ic_done_white_24dp);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = getResources().getDimensionPixelSize(R.dimen.done_icon_inset);
        layerDrawable.setLayerInset(1, diff, diff, diff, diff);
        mDoneDrawableOk = layerDrawable;
    }

    private void createWarningDrawable() {
        // adjust icon
        Drawable icon = getDrawable(R.drawable.ic_info_white_24dp);
        DrawUtils.setColor(icon, mPrimaryColor1);

        mWarningDrawable = icon;
    }

    private void setupFab() {
        mFab2 = findViewById(R.id.edit_contact_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });
    }

    private class KatsunaWizardBehavior implements View.OnFocusChangeListener, TextWatcher {

        private final KatsunaWizardText mKatsunaWizardText;
        private final KatsunaWizardText mNextKatsunaWizardText;

        KatsunaWizardBehavior(KatsunaWizardText wizardText, KatsunaWizardText nextWizardText) {
            mKatsunaWizardText = wizardText;
            mNextKatsunaWizardText = nextWizardText;

            wizardText.setImageOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNextKatsunaWizardText != null) {
                        mNextKatsunaWizardText.requestFocus();
                    }
                }
            });
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mKatsunaWizardText.setCardHolderColor(mPrimaryColor2);
                mKatsunaWizardText.setUnderlineColor(mPrimaryColor2);
                if (!TextUtils.isEmpty(mKatsunaWizardText.getText())) {
                    showNextImageControl(true);
                }
            } else {
                mKatsunaWizardText.setImageVisibility(View.GONE);
                if (TextUtils.isEmpty(mKatsunaWizardText.getText())) {
                    if (mKatsunaWizardText.isRequired()) {
                        mKatsunaWizardText.setCardHolderColor(mPrimaryColor1);
                        mKatsunaWizardText.setUnderlineColor(mPrimaryColor1);
                        mKatsunaWizardText.showMissingHint(true);
                    } else {
                        mKatsunaWizardText.setCardHolderColor(mSecondaryColor3);
                        mKatsunaWizardText.setUnderlineColor(mBlack34);
                        mKatsunaWizardText.showMissingHint(false);
                    }
                } else {
                    mKatsunaWizardText.setCardHolderColor(mPrimaryColor2);
                    mKatsunaWizardText.setUnderlineColor(mBlack34);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(mKatsunaWizardText.getText())) {
                showNextImageControl(false);
            } else {
                showNextImageControl(true);
                if (mKatsunaWizardText.isRequired()) {
                    mKatsunaWizardText.clearError(mPrimaryColor2, mBlack54);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private void showNextImageControl(boolean flag) {
            if (mNextKatsunaWizardText == null) return;
            if (mKatsunaWizardText.isFocused()) {
                mKatsunaWizardText.setImageVisibility(flag ? View.VISIBLE : View.GONE);
            }
        }
    }
}
