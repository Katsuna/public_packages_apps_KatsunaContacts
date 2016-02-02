package gr.crystalogic.oldmen.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.oldmen.ui.adapters.viewholders.ContactForSelectionViewHolder;

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

}
