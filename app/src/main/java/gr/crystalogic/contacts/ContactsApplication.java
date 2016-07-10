package gr.crystalogic.contacts;

import gr.crystalogic.commons.framework.BaseApplication;
import gr.crystalogic.commons.utils.Log;

public class ContactsApplication extends BaseApplication {

    private static final String APP_NAME = "Contacts";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.initialize(getApplicationContext(), APP_NAME, true);
    }

    @Override
    protected void handleException(Throwable throwable) {
        Log.e(this, throwable.getMessage());
    }
}
