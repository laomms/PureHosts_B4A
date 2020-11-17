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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




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
	
	public void Initialize(BA paramBA, String paramString) {
	    eventName = paramString.toLowerCase(BA.cul);
	    ba = paramBA;

	    MainActivity cv = new MainActivity();
	    String str = paramString.toLowerCase(BA.cul);
	
	    setObject(cv);
		intent = new Intent(BA.applicationContext, MainActivity.class);	
	
	}	

    public void startVPN() {
        getObject().startVPN();
    }
	
	public void stopVPN() {
        getObject().stopVPN();
    }
	
    public void genHostFirst() {
        getObject().genHostFirst();
    }
	
	public void startVService() {
        ion = new IOnActivityResult() {

            @Override
            public void ResultArrived(int arg0, Intent data) {
                BA.Log("ResultArrived");
                if (data != null) {
                    String text = data.getStringExtra(MainActivity.INPUT_SERVICE);
                    BA.Log("Text read: " + text);
                    if (B4AWrapperClass.ba.subExists(B4AWrapperClass.eventName + "_scanned_text")) {
                        B4AWrapperClass.ba.raiseEventFromDifferentThread(this, null, 0, B4AWrapperClass.eventName + "_SetHosts", true, new Object[] {text});
                    }

                } else {
                    //statusMessage.setText(R.string.ocr_failure);
                    BA.Log("No Text captured, intent data is null");
                }
            }
        };
        ba.startActivityForResult(ion, intent);
    }
}
