package com.example.golfer.objects;

import com.example.golfer.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Coin extends Circle {

    public int coinEffect;
    private double time;

    public Coin(double radius, Translate position, int effect){
        super(radius);
        Color coinColor = null;
        this.coinEffect=effect;


        time=Math.random()*7 + 5;

        if(coinEffect==1){ //ZUTI==POENI
            coinColor=Color.YELLOW;
        }else if(coinEffect==2){ // NARANDZASTI==ZIVOTI
            coinColor=Color.ORANGE;
        }else if(coinEffect==3){ // PLAVI==VREME
            coinColor=Color.TEAL;
        }

        super.setFill(coinColor);

        super.getTransforms().addAll(position);

    }

    public boolean timesUp(double passed){
        if(time<=0)
            return true;

        time-=passed;
        return false;
    }

    public boolean handleCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();

        double ballX = ballBounds.getCenterX();
        double ballY = ballBounds.getCenterY();
        double ballRadius = ball.getRadius();

        Bounds holeBounds = super.getBoundsInParent();

        double x = holeBounds.getCenterX();
        double y = holeBounds.getCenterY();
        double rad = super.getRadius();

        double distanceX = x - ballX;
        double distanceY = y - ballY;

        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        boolean result = ballBounds.intersects(holeBounds);

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

        return collisionDetected;
    }


}

