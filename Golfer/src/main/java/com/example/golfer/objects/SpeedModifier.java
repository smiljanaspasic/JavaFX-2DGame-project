package com.example.golfer.objects;

import javafx.geometry.Bounds;
import com.example.golfer.Utilities;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Rectangle;

public class SpeedModifier extends Rectangle
{
    public static SpeedModifier ice(final double width, final double height, final Translate position) {
        return new SpeedModifier(width, height, Color.LIGHTBLUE, position);
    }

    public static SpeedModifier mud(final double width, final double height, final Translate position) {
        return new SpeedModifier(width, height, Color.BROWN, position);
    }

    public SpeedModifier(final double width, final double height, final Color color, final Translate position) {
        super(width, height, color);
        super.getTransforms().addAll(position);
    }

    public boolean handleCollision(Circle ball) {
        final Bounds myBounds = super.getBoundsInParent();
        final double minX = myBounds.getMinX();
        final double maxX = myBounds.getMaxX();
        final double minY = myBounds.getMinY();
        final double maxY = myBounds.getMaxY();
        final Bounds ballBounds = ball.getBoundsInParent();
        final double ballX = ballBounds.getCenterX();
        final double ballY = ballBounds.getCenterY();
        final double radius = ball.getRadius();
        final double closestX = Utilities.clamp(ballX, minX, maxX);
        final double closestY = Utilities.clamp(ballY, minY, maxY);
        final double distanceX = ballX - closestX;
        final double distanceY = ballY - closestY;
        final double distanceSquared = distanceX * distanceX + distanceY * distanceY;
        final boolean collisionDetected = distanceSquared <= radius * radius;
        return collisionDetected;
    }
}
