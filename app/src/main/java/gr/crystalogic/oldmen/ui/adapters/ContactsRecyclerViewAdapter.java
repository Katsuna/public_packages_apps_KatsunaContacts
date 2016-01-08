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
import gr.crystalogic.oldmen.utils.Step;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactListItemViewHolder> {

    private final List<ContactListItemModel> mModels;
    private final IContactsFragmentInteractionListener mListener;
    private Step mStep;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactsFragmentInteractionListener listener, Step step) {
        mModels = models;
        mListener = listener;
        mStep = step;
    }

    @Override
    public ContactListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact, parent, false);
        return new ContactListItemViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final ContactListItemViewHolder holder, int position) {
        final ContactListItemModel model = mModels.get(position);
        holder.bind(model, mStep, position);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void goToStep(Step step) {
        mStep = step;
        notifyDataSetChanged();
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
