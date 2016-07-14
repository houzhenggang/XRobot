package com.robot.et.core.software.ros;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

public class CompressedMapTransport extends AbstractNodeMain implements MessageListener<nav_msgs.OccupancyGrid> {

    private static final GraphName TOPIC_IN = GraphName.of("map_png");
    private Subscriber<nav_msgs.OccupancyGrid> subscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("map_png");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        subscriber = connectedNode.newSubscriber(TOPIC_IN, nav_msgs.OccupancyGrid._TYPE);
        subscriber.addMessageListener(this);
    }

    @Override
    public void onNewMessage(final nav_msgs.OccupancyGrid message) {
        if (message.getInfo().getWidth() > 0 && message.getInfo().getHeight() > 0) {
            Log.e("ROS_MAP","Width:"+message.getInfo().getWidth());
            Log.e("ROS_MAP","Height:"+message.getInfo().getHeight());
            Log.e("ROS_MAP","capacity:"+message.getData().capacity());
            Log.e("ROS_MAP","Data:"+message.getData());
            ChannelBuffer buffer = message.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer.array(), buffer.arrayOffset(), buffer.readableBytes());
            //暂时在此处提交网络请求
            
        }
    }
}
