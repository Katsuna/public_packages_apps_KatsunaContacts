package gr.crystalogic.oldmen.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gr.crystalogic.oldmen.R;
import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;
import gr.crystalogic.oldmen.domain.Contact;
import gr.crystalogic.oldmen.fragments.ActionsFragment;
import gr.crystalogic.oldmen.fragments.ContactsFragment;
import gr.crystalogic.oldmen.fragments.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener, ActionsFragment.OnFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isDefaultSmsApp(Context context) {
        Telephony.Sms.getDefaultSmsPackage(context);

        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }


    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.e(TAG, item.toString());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, uri.toString());
    }
}
