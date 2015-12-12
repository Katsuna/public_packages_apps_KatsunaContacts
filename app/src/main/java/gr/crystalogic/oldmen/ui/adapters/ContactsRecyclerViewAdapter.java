package gr.crystalogic.oldmen.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactListItemViewHolder;
import gr.crystalogic.oldmen.ui.listeners.IContactsFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactListItemModel} and makes a call to the
 * specified {@link IContactsFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactListItemViewHolder> {

    private final List<ContactListItemModel> mModels;
    private final IContactsFragmentInteractionListener mListener;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactsFragmentInteractionListener listener) {
        mModels = models;
        mListener = listener;
    }

    @Override
    public ContactListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ContactListItemViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final ContactListItemViewHolder holder, int position) {
        final ContactListItemModel model = mModels.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
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

    public ContactListItemModel removeItem(int position) {
        final ContactListItemModel model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ContactListItemModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ContactListItemModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
