// IRemoteBankService.aidl
package com.millet.mydemo;

// Declare any non-default types here with import statements
import com.millet.mydemo.User;
import com.millet.mydemo.RemoteClientCallBack;

interface IRemoteBankService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

            /**客户端直接调用方法---类似客户端发送请求，拿到数据**/
            /**存钱**/
            boolean despoistMoney(int _money);
            /**取款**/
            int drawMoney(int _money);
            /**当前存款用户**/
            User getUser();

            /**客户端注册回调接口，用以服务器端主动通过回调方法，向客户端发送数据---类似服务端主动推送数据给客户端**/
            void registerClientOberser(RemoteClientCallBack _clientCallBack);
}
