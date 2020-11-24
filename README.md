# PureHosts_B4A

source project: https://github.com/nICEnnnnnnnLee/PureHost

![image](https://github.com/laomms/PureHosts_B4A/blob/main/gif.gif)  

https://youtu.be/dkObvoK_RNk

to use:

```vb

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("Layout")
	hosts.Initialize("hosts")
	ConfigPath=File.DirInternal
	hosts.SetConfigPath(ConfigPath)
	If File.Exists(File.DirInternal, "is") = False Then
		File.WriteString(File.DirInternal, "is", "")
		hosts.InitHosts(ConfigPath)
	End If
	AutoCompleteEditText1.Text= hosts.readHost(ConfigPath)
End Sub

Sub Button1_Click	
	hosts.startVPN()
End Sub

Sub Button2_Click
	hosts.stopVPN
End Sub

Sub Button3_Click
	AutoCompleteEditText1.Text=hosts.readHost(ConfigPath)
End Sub

Sub Button4_Click
	hosts.writehosts(AutoCompleteEditText1.Text,ConfigPath)
End Sub

Sub hosts_result(MethodName As Object, agr As Object) As Object
	Log ("arrived")
	Return Null
End Sub

```   
