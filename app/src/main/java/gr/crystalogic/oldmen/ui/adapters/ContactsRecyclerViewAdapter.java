package gr.crystalogic.oldmen.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactSelectedViewHolder;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactViewHolder;
import gr.crystalogic.oldmen.ui.listeners.IContactInteractionListener;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTACT_NOT_SELECTED = 1;
    private static final int CONTACT_SELECTED = 2;

    private final List<ContactListItemModel> mModels;
    private final IContactInteractionListener mListener;
    private int mSelectedContactPosition = -1;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models, IContactInteractionListener listener) {
        mModels = models;
        mListener = listener;
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
                viewHolder = new ContactViewHolder(view, mListener);
                break;
            case CONTACT_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selected, parent, false);
                viewHolder = new ContactSelectedViewHolder(view, mListener);
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
}
