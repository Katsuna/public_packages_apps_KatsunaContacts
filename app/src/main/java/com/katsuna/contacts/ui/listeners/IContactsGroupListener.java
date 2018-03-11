package com.katsuna.contacts.ui.listeners;

import com.katsuna.commons.entities.UserProfileContainer;

public interface IContactsGroupListener {
    void selectContactsGroup(int position);

    UserProfileContainer getUserProfileContainer();
}
