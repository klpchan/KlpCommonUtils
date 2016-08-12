package com.klpchan.commonutils.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import com.klpchan.commonutils.LogUtil;

public class DynamicListener {

	private Context mContext;

    private Handler mMainHandler;
    
    private String mTargetIntentAction;
    
    public interface onActionChangeListener{
        void onStartListen();
        void onTargetIntentReceived();
        void onListenTimeOut();
        boolean isExtraConfirmed(Intent intent);
    }
    
    private onActionChangeListener mActionChangeListener;
    
    public DynamicListener() {
		// TODO Auto-generated constructor stub
	}
    
    public DynamicListener(Context context) {
		// TODO Auto-generated constructor stub
    	mContext = context;
	}
    
    public DynamicListener(Context context,Handler handler) {
		// TODO Auto-generated constructor stub
    	mContext = context;
    	mMainHandler = handler;
	}
    
    /**
     * This runnable used when waiting time is over.
     */
    public Runnable mWaitingNetworkChangedRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.d("[mWaitingNetworkChangedRunnable]mWaitingNetworkChangedRunnable start, unregister receiver time out!");
            try {
            	//time out, just recovery.
                if (mActionChangeListener!=null){
                    mActionChangeListener.onListenTimeOut();
                }
                if (mContext != null && mTargetIntentReceiver != null) {
                    mContext.unregisterReceiver(mTargetIntentReceiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    
    /**
     * This receiver used to listen mTargetIntentAction & isExtraConfirmed return true.
     * if target intent received,it will call mActionChangeListener's action which is defined by client
     */
    public BroadcastReceiver mTargetIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction().equalsIgnoreCase(mTargetIntentAction)) {
                LogUtil.d("[mTargetIntentReceiver] Target Intent (%s) Received!",mTargetIntentAction);
                if (mActionChangeListener != null){
                    if (mActionChangeListener.isExtraConfirmed(intent)) {
						mActionChangeListener.onTargetIntentReceived();
		                if (mMainHandler != null){
		                    LogUtil.d("[mTargetIntentReceiver]Remove mWaitingNetworkChangedRunnable!");
		                    mMainHandler.removeCallbacks(mWaitingNetworkChangedRunnable);
		                }
		                try {
		                    context.unregisterReceiver(this);
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
					}
                }
            } else {
            	//should not be here
                LogUtil.d(String.format("[mTargetIntentReceiver] Intent %1s received,different with target Intent %2s!",intent.getAction(),mTargetIntentAction));
            }
        }
    };
    
    /**
     * This method used for listen special intent for particular time.
     * @param context the context of call
     * @param listenIntentAction  the intent string need to listen
     * @param listener the listener defined by client
     * @param durationMileSec how long this listener will last, after durationMileSec, it will time out
     */
    public void listenSpecialTargetActionForTime(final Context context, String listenIntentAction, final onActionChangeListener listener, int durationMileSec){
        if (context == null){
            LogUtil.e("listenSpecialTargetActionForTime context is null");
            return;
        }
        if (TextUtils.isEmpty(listenIntentAction)){
            LogUtil.e("listenSpecialTargetActionForTime : listenIntent is empty");
            return;
        }
        if (listener == null){
            LogUtil.w("listenSpecialTargetActionForTime : actionChangeListener is null");
        }
        if (durationMileSec < 0){
            LogUtil.e("listenSpecialTargetActionForTime : durationMileSec is negative");
            return;
        }

        mContext = context;
        mActionChangeListener = listener;
        mTargetIntentAction = listenIntentAction;
        
        if (mActionChangeListener != null) {
			mActionChangeListener.onStartListen();
		}

        IntentFilter filter = new IntentFilter(listenIntentAction);
        mContext.registerReceiver(mTargetIntentReceiver, filter);

        mMainHandler = new Handler(mContext.getMainLooper());
        if (mMainHandler != null){
            mMainHandler.postDelayed(mWaitingNetworkChangedRunnable, durationMileSec);
        }
    }
}
