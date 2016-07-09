package com.robot.et.business;

import android.os.Bundle;

import com.robot.et.R;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


public class RosControler extends RosActivity {


    public RosControler(){
        super("Robot","Robot");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ros_controler);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

    }
}
