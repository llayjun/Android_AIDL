package com.millet.mydemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class RemoteBankService extends Service {

    /**
     * 绑定服务
     *
     * @param _context
     * @param _serviceConnection
     */
    public static void bindService(Context _context, ServiceConnection _serviceConnection) {
        Log.d(TAG, "bindService pid = " + android.os.Process.myPid());
        Intent _intent = new Intent(_context, RemoteBankService.class);
        _context.bindService(_intent, _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     *
     * @param _context
     * @param _serviceConnection
     */
    public static void unBindService(Context _context, ServiceConnection _serviceConnection) {
        Log.d(TAG, "unBindService pid = " + android.os.Process.myPid());
        if (null != _serviceConnection) {
            _context.unbindService(_serviceConnection);
            _context.stopService(new Intent(_context, RemoteBankService.class));
        }
    }

    private static String TAG = RemoteBankService.class.getSimpleName();

    private RemoteClientCallBack mRemoteClientCallBack;

    private TimeHandler mTimeHandler = new TimeHandler();

    private Binder mBinder = new IRemoteBankService.Stub() {

        @Override
        public boolean despoistMoney(int _money) throws RemoteException {
            Log.d(TAG, "despoistMoney pid = " + android.os.Process.myPid());
            if (_money > 0) return true;
            return false;
        }

        @Override
        public int drawMoney(int _money) throws RemoteException {
            Log.d(TAG, "drawMoney pid = " + android.os.Process.myPid());
            mRemoteClientCallBack.transferToClientByServer("当前用户存钱成功！" + "余额：" + _money + "当前进程Id：" + android.os.Process.myPid());
            return _money;
        }

        @Override
        public User getUser() throws RemoteException {
            Log.d(TAG, "getUser pid = " + android.os.Process.myPid());
            User _user = new User(String.valueOf(System.currentTimeMillis()), "Millet", String.valueOf(android.os.Process.myPid()));
            return _user;
        }

        @Override
        public void registerClientOberser(RemoteClientCallBack _clientCallBack) throws RemoteException {
            mRemoteClientCallBack = _clientCallBack;
            Message _message = Message.obtain();
            _message.obj = _clientCallBack;
            mTimeHandler.sendMessageDelayed(_message, 10000);
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate pid = " + android.os.Process.myPid());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy pid = " + android.os.Process.myPid());
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return mBinder;
    }

    class TimeHandler extends Handler {

        public TimeHandler() {
            Looper looper = Looper.myLooper();
            if (null == looper) {
                Looper.prepare();
                Looper.loop();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object _object = msg.obj;
            if (null != _object) {
                RemoteClientCallBack clientCallBackInstance = (RemoteClientCallBack) msg.obj;
                try {
                    clientCallBackInstance.transferToClientByServer("已延期10s后发送 当前进程Id = " + Process.myPid());
                } catch (RemoteException _e) {
                    _e.printStackTrace();
                }
            }
        }
    }

}
