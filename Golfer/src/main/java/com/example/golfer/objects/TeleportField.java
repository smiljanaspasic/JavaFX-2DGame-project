package com.example.golfer.objects;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class TeleportField extends Circle {

    private TeleportField pair;
    private boolean visited=false;
    public TeleportField(double x, double y) {
        super();
        super.setCenterX(x);
        setCenterY(y);
        setRadius(10);
        Stop stops[] = {
                new Stop(0, Color.ROYALBLUE),
                new Stop(1, Color.MEDIUMPURPLE)
        };

        RadialGradient radialGradient = new RadialGradient(
                0, 0,
                0.5, 0.5,
                0.5, true,
                CycleMethod.REPEAT, stops
        );

        super.setFill(radialGradient);

    }

    public void setMyPair(TeleportField pair) {
        this.pair = pair;
    }

    public boolean getVisited() {
        return visited;
    }
    public void setVisited(boolean b) {
        visited=b;
    }
    public boolean handleCollision(Circle ball) {
        visited=false;
        Bounds ballBounds = ball.getBoundsInParent();

        double ballX = ballBounds.getCenterX();
        double ballY = ballBounds.getCenterY();
        double ballRadius = ball.getRadius();

        Bounds teleporterBounds = super.getBoundsInParent();

        Bounds holeBounds = super.getBoundsInParent();

        if (ball instanceof Coin) {
            return ballBounds.intersects(teleporterBounds);
        }

        double telX = teleporterBounds.getCenterX();
        double telY = teleporterBounds.getCenterY();
        double telRadius = super.getRadius();

        double distanceX = telX - ballX;
        double distanceY = telY - ballY;

        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        boolean result = distanceSquared < (telRadius * telRadius);

        double newPosX;
        double newPosY;

        Bounds pairBounds = pair.getBoundsInParent();

        if (result) {
            if (ball instanceof Ball) {

                    newPosY = pairBounds.getCenterY()*0.85 - 2 * telRadius;
                    newPosX = pairBounds.getCenterX();

                if(pair.visited==false) {
                ((Ball) ball).getPosition().setX(newPosX);
                ((Ball) ball).getPosition().setY(newPosY);
                pair.visited=true;

            }
            }
        }
        return result;
    }
}