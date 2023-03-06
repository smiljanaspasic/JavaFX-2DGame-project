package com.example.golfer.objects;

import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import com.example.golfer.Utilities;
import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.geometry.Point2D;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Circle;


public class Ball extends Circle {
	private Translate position;
	private Point2D speed;
	private Scale scale;
	private State state;
	
	public Ball ( double radius, Translate position, Point2D speed ) {
		super ( radius, Color.RED );
		
		this.position = position;
		this.speed = speed;
		this.scale = new Scale(1.0, 1.0);
		this.state = State.MOVING;
		
		super.getTransforms ( ).addAll ( this.position ,this.scale);
	}
	
	public boolean update ( double ds, double left, double right, double top, double bottom, double dampFactor, double minBallSpeed ) {
		boolean result = false;
		if(this.state== State.MOVING) {
			double newX = this.position.getX() + this.speed.getX() * ds;
			double newY = this.position.getY() + this.speed.getY() * ds;

			double radius = super.getRadius();

			double minX = left + radius;
			double maxX = right - radius;
			double minY = top + radius;
			double maxY = bottom - radius;

			this.position.setX(Utilities.clamp(newX, minX, maxX));
			this.position.setY(Utilities.clamp(newY, minY, maxY));

			if (newX < minX || newX > maxX) {
				this.speed = new Point2D(-this.speed.getX(), this.speed.getY());
			}

			if (newY < minY || newY > maxY) {
				this.speed = new Point2D(this.speed.getX(), -this.speed.getY());
			}

			this.speed = this.speed.multiply(dampFactor);

			double ballSpeed = this.speed.magnitude();

			if (ballSpeed < minBallSpeed) {
				result = true;
			}
		}
		return result;
	}

	public boolean handleCollision(final Bounds bounds) {
		final Bounds myBounds = this.getBoundsInParent();
		final double centerX = myBounds.getCenterX();
		final double centerY = myBounds.getCenterY();
		final double radius = this.getRadius();
		final double minX = bounds.getMinX();
		final double maxX = bounds.getMaxX();
		final double minY = bounds.getMinY();
		final double maxY = bounds.getMaxY();
		final double closestX = Utilities.clamp(centerX, minX, maxX);
		final double closestY = Utilities.clamp(centerY, minY, maxY);
		final double distanceX = centerX - closestX;
		final double distanceY = centerY - closestY;
		final double distanceSquared = distanceX * distanceX + distanceY * distanceY;
		final boolean collisionDetected = distanceSquared <= radius * radius;
		if (collisionDetected) {
			final boolean goingUp = this.speed.getY() < 0.0;
			final boolean goingDown = this.speed.getY() > 0.0;
			final boolean goingRight = this.speed.getX() > 0.0;
			final boolean goingLeft = this.speed.getX() < 0.0;
			final double epsilon = 3.0;
			final boolean isMinY = Utilities.areEqual(closestY, minY, 3.0);
			final boolean isMaxY = Utilities.areEqual(closestY, maxY, 3.0);
			final boolean isMinX = Utilities.areEqual(closestX, minX, 3.0);
			final boolean isMaxX = Utilities.areEqual(closestX, maxX, 3.0);
			final boolean invertY = (isMinY && goingDown) || (isMaxY && goingUp);
			final boolean invertX = (isMinX && goingRight) || (isMaxX && goingLeft);
			if (invertY) {
				this.speed = new Point2D(this.speed.getX(), -this.speed.getY());
			}
			else if (invertX) {
				this.speed = new Point2D(-this.speed.getX(), this.speed.getY());
			}
		}
		return collisionDetected;
	}
	public void initializeFalling(EventHandler<ActionEvent> onFinishHandler) {
		this.state = State.FALLING;
		KeyFrame keyframes[]={
				new KeyFrame(Duration.seconds(0.0) , new KeyValue(this.scale.xProperty(),1,Interpolator.LINEAR)),
				new KeyFrame(Duration.seconds(0.0) , new KeyValue(this.scale.yProperty(),1,Interpolator.LINEAR)),
				new KeyFrame(Duration.seconds(2.0) , new KeyValue(this.scale.xProperty(),0,Interpolator.LINEAR)),
				new KeyFrame(Duration.seconds(2.0) , new KeyValue(this.scale.xProperty(),0,Interpolator.LINEAR)),


		};
		Timeline animation = new Timeline(keyframes);
		animation.setOnFinished(onFinishHandler);
		animation.play();
	}

	public double getSpeedMagnitude() {
		return this.speed.magnitude();
	}
	public Translate getPosition() {
		return position;
	}
	private enum State
	{
		MOVING,
		FALLING; }
}
