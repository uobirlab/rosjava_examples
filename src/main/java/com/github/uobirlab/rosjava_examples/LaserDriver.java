package com.github.uobirlab.rosjava_examples;

// import geometry_msgs.Twist;
import geometry_msgs.Vector3;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

//import sensor_msgs.LaserScan;

public class LaserDriver extends AbstractNodeMain {

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("laser_driver");
	}

	@Override
	public void onStart(final ConnectedNode _node) {

		// Use a logger instead of the usual System.out.println to get send
		// output to ROS.
		//
		// This is final so we can use it in the anonymous inner class below
		final Log logger = _node.getLog();

		// Create an object used to publish Twist messages to the cmd_vel topic
		// in order to control the robot
		final Publisher<geometry_msgs.Twist> publisher = _node.newPublisher("cmd_vel", geometry_msgs.Twist._TYPE);

		// Subscribe to laser scans published on the base_scan topic
		Subscriber<sensor_msgs.LaserScan> subscriber = _node.newSubscriber("/base_scan",																		   sensor_msgs.LaserScan._TYPE);

		// Add a listener that is called each time a new laser scan is received.
		subscriber.addMessageListener(new MessageListener<sensor_msgs.LaserScan>() {
			@Override
			public void onNewMessage(sensor_msgs.LaserScan _scan) {
				float[] ranges = _scan.getRanges();
				float maxRange = Float.MIN_VALUE;
				for (int i = 0; i < ranges.length; i++) {
					if (ranges[i] > maxRange) {
						maxRange = ranges[i];
					}
				}
				logger.info("The longest range I found was: " + maxRange);
				logger.info("I hope this is less than: " + _scan.getRangeMax());
			
				//create a new Twist message
				geometry_msgs.Twist twist = publisher.newMessage();
				//set the desired forward velocity of the robot to 0.2 m/s.
				geometry_msgs.Vector3 linear = twist.getLinear();
				linear.setX(0.2);
				twist.setLinear(linear);
				//publish method to robot
				publisher.publish(twist);
			}

		});
	}
}
