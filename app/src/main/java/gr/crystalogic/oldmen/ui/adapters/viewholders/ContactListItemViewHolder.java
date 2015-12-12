package gr.crystalogic.oldmen.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.domain.Contact;

public class ContactListItemViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public Contact mItem;

    public ContactListItemViewHolder(View view) {
        super(view);
        mView = view;
        mIdView = (TextView) view.findViewById(R.id.id);
        mContentView = (TextView) view.findViewById(R.id.content);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mContentView.getText() + "'";
    }
}
