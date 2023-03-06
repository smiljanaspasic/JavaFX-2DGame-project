package com.example.golfer.objects;

import javafx.geometry.Bounds;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Ufo extends Circle {

    public Ufo(double radius, Translate position, ImagePattern pattern) {

        super(radius);

        super.setFill(pattern);

        super.getTransforms().addAll(
                position
        );

    }

    public boolean handleCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();

        double ballX = ballBounds.getCenterX();
        double ballY = ballBounds.getCenterY();
        double ballRadius = ball.getRadius();

        Bounds enemyBounds = super.getBoundsInParent();

        double ufoX = enemyBounds.getCenterX();
        double ufoY = enemyBounds.getCenterY();
        double holeRadius = super.getRadius();

        double distanceX = ufoX - ballX;
        double distanceY = ufoY - ballY;

        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        boolean result = distanceSquared < (holeRadius * holeRadius);

        return result;
    }

}

