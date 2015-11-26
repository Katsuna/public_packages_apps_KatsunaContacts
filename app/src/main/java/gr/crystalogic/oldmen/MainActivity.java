package gr.crystalogic.oldmen;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gr.crystalogic.oldmen.dao.ContactDao;
import gr.crystalogic.oldmen.dao.IContactDao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IContactDao dao = new ContactDao(MainActivity.this);
                dao.getContacts();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isDefaultSmsApp(Context context) {
        Telephony.Sms.getDefaultSmsPackage(context);

        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }


}
