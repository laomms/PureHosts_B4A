package B4AWrapperClass;


import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.collections.List;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.IOnActivityResult;
import anywheresoftware.b4a.BA.Permissions;
import anywheresoftware.b4a.keywords.Bit;
import anywheresoftware.b4a.objects.IntentWrapper;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.widget.EditText;


import java.lang.reflect.Field;

import top.nicelee.purehost.MainActivity;
import top.nicelee.purehost.vpn.config.ConfigReader;
import top.nicelee.purehost.vpn.LocalVpnService;

@Version(1.0f)
@Permissions(values={"android.permission.INTERNET"})
@ShortName("PureHostLib")
@ActivityObject

public class B4AWrapperClass extends AbsObjectWrapper<MainActivity>  {

    @Hide
    public static BA ba;
    @Hide
    public static String eventName;
    private static MainActivity activityx;
    private IOnActivityResult ion;
    Intent intent;
    Context context;

    public void Initialize(BA paramBA, String paramString) {
        eventName = paramString.toLowerCase(BA.cul);
        ba = paramBA;
        MainActivity activityx = new MainActivity();
        String str = paramString.toLowerCase(BA.cul);
        setObject(activityx);
    }

    public void writehosts(String HostsContent,String ConfigPath)
    {
        ConfigReader.writeHosts(HostsContent,ConfigPath);
    }

    public String readHost(String ConfigPath)
    {
        return ConfigReader.readHosts(ConfigPath);
    }

    public void InitHosts(String ConfigPath)
    {
		ConfigReader.initHosts( ConfigPath);
    }
	
    public void SetConfigPath(String ConfigPath)
    {
        MainActivity.path= ConfigPath;
		BA.Log("FilePath:" + ConfigPath);
    }


    public void stopVPN()
    {
        LocalVpnService.Instance.stopVPN();
    }
	
    public void startVPN()
    {
        final Intent VpnServiceIntent = VpnService.prepare(BA.applicationContext);
        if (VpnServiceIntent != null) {
            ion = new IOnActivityResult() {
                @Override
                public void ResultArrived(int resultCode, Intent data) {
                    BA.Log("ResultArrived");
                    if (resultCode == Activity.RESULT_OK) {
                        BA.Log("RESULT_OK");
                        ba.activity.startService(new Intent(BA.applicationContext, LocalVpnService.class));
                    }
                    ba.raiseEvent(B4AWrapperClass.this, eventName + "_result", true, intent);
                }
            };
            ba.startActivityForResult(ion, VpnServiceIntent);
        } else {
            Intent serviceIntent = new Intent(BA.applicationContext, LocalVpnService.class);
            ba.activity.startService(serviceIntent);
        }       
    }
}
