package com.millet.mydemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Administrator on 2017/8/28 0028.
 */

public class BookManagerService extends Service {

    private final String TAG = BookManagerService.class.getSimpleName();

    // 支持并发读写
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<com.millet.mydemo.Book> getBookList() throws RemoteException {
            SystemClock.sleep(5000); // 延迟加载
            return mBookList;
        }

        @Override
        public void addBook(com.millet.mydemo.Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int num = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "添加完成, 注册接口数: " + num);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int num = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "删除完成, 注册接口数: " + num);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "millet"));
        mBookList.add(new Book(2, "xiaomi"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return mBinder;
    }

    private int mNum = 0;

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException _e) {
                    _e.printStackTrace();
                }
                mNum++;
                if (mNum == 5) {
                    mIsServiceDestroyed.set(true);
                }
                Message msg = new Message();
                mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int bookId = 1 + mBookList.size();
            Book newBook = new Book(bookId, "新书#" + bookId);
            try {
                onNewBookArrived(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        Log.e(TAG, "发送通知的数量: " + mBookList.size());
        int num = mListenerList.beginBroadcast();
        for (int i = 0; i < num; ++i) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            Log.e(TAG, "发送通知: " + listener.toString());
            listener.onNewBookArrived(book);
        }
        mListenerList.finishBroadcast();
    }

}
