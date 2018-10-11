package j.z.unlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context,WssService.class);
        context.startService(intent);
        Log.i("Autostart", "started");
    }
}
