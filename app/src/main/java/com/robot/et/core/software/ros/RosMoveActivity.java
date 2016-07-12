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
        super("XRobot","XRobot", URI.create("http://192.168.3.1:11311"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ros_controler);
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
        private int data;//获取的Brocast传递的角度(int)

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
                Log.i("ROS_WAKE_UP","语音唤醒时，当前机器人的角度："+mover.getCurrentDegree());
                data=intent.getIntExtra("degree",0);
                Log.i("ROS_WAKE_UP","语音唤醒时，获取的角度："+data);
                if (data == 0 || data == 360){
                    //原地不动
                    return;
                }
                doTrunAction(mover.getCurrentDegree(),Double.valueOf(data));
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
            Log.i("ROS_MOVE", "机器人移动方向:停止");
            mover.execStop();
        }
    }
    public void doTrunAction(double currentDegree,double degree){
        mover.isPublishVelocity(true);
        double temp;
        if (currentDegree+degree<=180){
            temp=currentDegree+degree;
        }else {
            temp=currentDegree+degree-360;
        }
        if ((degree > 0 && degree < 180)){
            mover.execTurnRight();
            mover.setDegree(temp);
        }else{
            mover.execTurnLeft();
            mover.setDegree(temp);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
