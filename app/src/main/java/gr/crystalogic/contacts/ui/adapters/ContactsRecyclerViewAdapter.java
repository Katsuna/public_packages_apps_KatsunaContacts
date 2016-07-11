package gr.crystalogic.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.commons.entities.Profile;
import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.contacts.ui.adapters.viewholders.ContactSelectedViewHolder;
import gr.crystalogic.contacts.ui.adapters.viewholders.ContactViewHolder;
import gr.crystalogic.contacts.ui.listeners.IContactInteractionListener;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;

    private final List<ContactListItemModel> mModels;
    private final IContactInteractionListener mListener;
    private int mSelectedContactPosition = -1;
    private final Profile mProfile;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactInteractionListener listener, Profile profile) {
        mModels = models;
        mListener = listener;
        mProfile = profile;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CONTACT_NOT_SELECTED;
        if (position == mSelectedContactPosition) {
            viewType = CONTACT_SELECTED;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case CONTACT_NOT_SELECTED:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
                viewHolder = new ContactViewHolder(view, mListener, mProfile);
                break;
            case CONTACT_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected, parent, false);
                viewHolder = new ContactSelectedViewHolder(view, mListener, mProfile);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ContactListItemModel model = mModels.get(position);

        switch (viewHolder.getItemViewType()) {

            case CONTACT_NOT_SELECTED:
                ContactViewHolder imageViewHolder = (ContactViewHolder) viewHolder;
                imageViewHolder.bind(model, position);
                break;

            case CONTACT_SELECTED:
                ContactSelectedViewHolder contactSelectedViewHolder = (ContactSelectedViewHolder) viewHolder;
                contactSelectedViewHolder.bind(model.getContact());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void selectContactAtPosition(int position) {
        mSelectedContactPosition = position;
        notifyItemChanged(position);
    }

    public int getPositionByContactId(String contactId) {
        int position = -1;
        for (int i = 0; i < mModels.size(); i++) {
            if (mModels.get(i).getContact().getId().equals(contactId)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void animateTo(List<ContactListItemModel> models) {
        mSelectedContactPosition = -1;
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ContactListItemModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final ContactListItemModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContactListItemModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContactListItemModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContactListItemModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContactListItemModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, ContactListItemModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContactListItemModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
