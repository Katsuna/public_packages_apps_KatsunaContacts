package gr.crystalogic.contacts.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.contacts.R;
import gr.crystalogic.contacts.domain.Contact;
import gr.crystalogic.contacts.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.contacts.ui.adapters.viewholders.ContactForSelectionViewHolder;

public class ContactsSelectionAdapter extends RecyclerView.Adapter<ContactForSelectionViewHolder> {

    private final List<ContactListItemModel> mModels;

    public ContactsSelectionAdapter(List<ContactListItemModel> models) {
        mModels = models;
    }

    @Override
    public ContactForSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_for_selection, parent, false);
        return new ContactForSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactForSelectionViewHolder holder, int position) {
        holder.bind(mModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void removeItem(Contact contact) {
        int position = -1;
        for (int i = 0; i < mModels.size(); i++) {
            if (mModels.get(i).getContact().getId().equals(contact.getId())) {
                position = i;
                break;
            }
        }

        //position found
        if (position > -1) {
            mModels.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<ContactListItemModel> getModels() {
        return mModels;
    }

    public void animateTo(List<ContactListItemModel> models) {
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
