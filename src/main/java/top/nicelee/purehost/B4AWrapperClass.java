package B4AWrapperClass;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.objects.ActivityWrapper;
import anywheresoftware.b4a.objects.EditTextWrapper;
import anywheresoftware.b4a.objects.IntentWrapper;
import anywheresoftware.b4a.objects.collections.Map;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.Pixel;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.keywords.Common.DesignerCustomView;
import anywheresoftware.b4a.objects.LabelWrapper;
import anywheresoftware.b4a.objects.PanelWrapper;
import anywheresoftware.b4a.objects.ViewWrapper;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.IOnActivityResult;
import anywheresoftware.b4a.BA.Permissions;
import anywheresoftware.b4a.keywords.Bit;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;



import java.io.IOException;
import top.nicelee.purehost.MainActivity;
import top.nicelee.purehost.vpn.LocalVpnService;

import android.content.Intent;
import android.os.Bundle;

@Version(1.0f)
@Permissions(values={"android.permission.INTERNET"})
@ShortName("PureHostLib")
@ActivityObject
//@DependsOn(values = {"arcgis-android-api","arcgis-android-app-toolkit"})
//@Permissions(values = {"android.permission.INTERNET","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.ACCESS_FINE_LOCATION"})

public class B4AWrapperClass extends AbsObjectWrapper<MainActivity> {

    @Hide
	public static BA ba;
	@Hide
	public static String eventName;
	private static MainActivity cv;
	private IOnActivityResult ion;
	Intent intent;
	Context context;
	
	public void Initialize(BA paramBA, String paramString) {
	    eventName = paramString.toLowerCase(BA.cul);
		BA.Log("eventName:" +eventName );
	    ba = paramBA;
	    MainActivity cv = new MainActivity();
	    String str = paramString.toLowerCase(BA.cul);
	    BA.Log("str:" +str );
	    setObject(cv);
		intent = new Intent(BA.applicationContext, MainActivity.class);	
		BA.Log("intent:" +intent );
        int layoutId = BA.applicationContext.getResources().getIdentifier("activity_main", "layout", BA.packageName);
        BA.Log("layoutId_activity_main:" +Bit.ToHexString(layoutId) );
		LocalVpnService Instance=new LocalVpnService();
		LocalVpnService.Instance=Instance;
	}	

    public Intent GetIntent()
    {        
        return intent;
    }
	
	public void SetConfigFilePath(String FilePath)
    {
        MainActivity.path= FilePath;
    }
	
    public Intent GetVpnServiceIntent()
    {
        return  VpnService.prepare(BA.applicationContext);
    }
	
	public void setContentView(BA pBA, String LayoutName){
        pBA.activity.setContentView(BA.applicationContext.getResources().getIdentifier(LayoutName, "layout", BA.packageName));
    }	

}
