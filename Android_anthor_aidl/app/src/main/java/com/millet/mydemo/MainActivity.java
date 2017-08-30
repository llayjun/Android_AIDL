package com.millet.mydemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mButtonBind, mButtonUnBind, mButtonDrawMoney;

    private IRemoteBankService mIRemoteBankService;

    private RemoteClientCallBack mRemoteClientCallBack = new RemoteClientCallBack.Stub() {
        @Override
        public void transferToClientByServer(final String _transferData) throws RemoteException {
            Log.d(TAG, "transferData = " + _transferData + " client Pid = " + android.os.Process.myPid());
            //如果是service通过handler调用的这个的，由于service的进程调用，所以这个回调不是在
            //主线程而是工作线程中，直接更新或toast会抛出如下异常，所以定要在主线程中更新
            //Uncaught remote exception! (Exceptions are not yet supported across processes.)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, _transferData, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName _componentName, IBinder _iBinder) {
            Log.d(TAG, "onServiceConnected pid = " + android.os.Process.myPid());//建立连接成功
            mIRemoteBankService = IRemoteBankService.Stub.asInterface(_iBinder);//跨进程的处理方式
            //mIRemoteBankService = (IRemoteBankService)_iBinder;//统一进程的处理方式
            try {
                mIRemoteBankService.registerClientOberser(mRemoteClientCallBack);
            } catch (RemoteException _e) {
                _e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName _componentName) {
            Log.d(TAG, "onServiceDisconnected pid = " + android.os.Process.myPid());//断开连接
            mIRemoteBankService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonBind = (Button) findViewById(R.id.bind_service_button);
        mButtonUnBind = (Button) findViewById(R.id.unbind_service_button);
        mButtonDrawMoney = (Button) findViewById(R.id.draw_money);
        mButtonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                RemoteBankService.bindService(MainActivity.this, mServiceConnection);
            }
        });
        mButtonUnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                RemoteBankService.unBindService(MainActivity.this, mServiceConnection);
            }
        });
        mButtonDrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                try {
                    mIRemoteBankService.drawMoney(100);
                } catch (RemoteException _e) {
                    _e.printStackTrace();
                }
            }
        });
    }
}
