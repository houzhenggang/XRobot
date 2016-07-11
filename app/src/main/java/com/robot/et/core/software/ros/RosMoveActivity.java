package com.robot.et.core.software.ros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.robot.et.R;
import com.robot.et.config.BroadcastAction;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.net.URI;


public class RosMoveActivity extends RosActivity {

    private MoveControler mover;
    private NodeConfiguration nodeConfiguration;

    public RosMoveActivity(){
        super("XRobot","XRobot", URI.create("http://192.168.8.3:11311"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ros_controler);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE);
        mFilter.addAction(BroadcastAction.ACTION_WAKE_UP_AND_MOVE);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        mover = new MoveControler();
        mover.isPublishVelocity(false);
        nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(mover, nodeConfiguration);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private String direction;
        private String data;//获取的语音板的角度(String)
        private double degree;//获取的语音板的角度(double)

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE)) {
                direction=intent.getStringExtra("direction");
                Log.i("ROS_MOVE","手机控制时，得到的direction参数："+direction);
                if (null==direction|| TextUtils.equals("", direction)) {
                    return;
                }
                doMoveAction(direction);
            }else if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_AND_MOVE)){
                Log.i("ROS_MOVE","语音唤醒时，当前机器人的角度："+mover.getCurrentDegree());
                data=intent.getStringExtra("degree");
                if (null==data||TextUtils.equals("", data)){
                    return;
                }
                degree=Double.valueOf(data);
                doTrunAction(degree);
            }
        }
    };

    private void doMoveAction(String message) {
            mover.isPublishVelocity(true);
        if (TextUtils.equals("1", message)) {
            Log.i("ROS_MOVE", "机器人移动方向:向前");
            mover.execMoveForword();
        } else if (TextUtils.equals("2", message)) {
            Log.i("ROS_MOVE", "机器人移动方向:向后");
            mover.execMoveBackForward();
        } else if (TextUtils.equals("3", message)) {
            Log.i("ROS_MOVE", "机器人移动方向:向左");
            mover.execTurnLeft();
        } else if (TextUtils.equals("4", message)) {
            Log.i("ROS_MOVE", "机器人移动方向:向右");
            mover.execTurnRight();
        } else if (TextUtils.equals("5", message)) {
            mover.execStop();
        }
    }
    public void doTrunAction(double degree){
        mover.isPublishVelocity(true);
        if (degree==0 && degree==360){
            return;
        }else if (degree>180){
            mover.execTurnLeft();
            mover.setDegree(degree-360);
        }else{
            mover.execTurnRight();
            mover.setDegree(degree);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
