
package com.example.golfer.objects;

import javafx.scene.Node;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;

public class Fence extends Group
{
    private Rectangle left;
    private Rectangle right;
    private Rectangle top;
    private Rectangle bottom;

    public Fence(final double width, final double height, final double fenceWidth, final ImagePattern texture) {
        this.left = new Rectangle(fenceWidth, height, texture);
        this.right = new Rectangle(fenceWidth, height, texture);
        this.right.getTransforms().addAll(new Translate(width - fenceWidth, 0.0));
        this.top = new Rectangle(width - 2.0 * fenceWidth, fenceWidth, texture);
        this.top.getTransforms().addAll(new Translate(fenceWidth, 0.0));
        this.bottom = new Rectangle(width - 2.0 * fenceWidth, fenceWidth, texture);
        this.bottom.getTransforms().addAll(new Translate(fenceWidth, height - fenceWidth));
        super.getChildren().addAll(this.left, this.right, this.top, this.bottom);
    }

    public boolean handleCollision(final Ball ball) {
        final boolean collidedWithLeft = ball.handleCollision(this.left.getBoundsInParent());
        final boolean collidedWithRight = ball.handleCollision(this.right.getBoundsInParent());
        final boolean collidedWithTop = ball.handleCollision(this.top.getBoundsInParent());
        final boolean collidedWithBottom = ball.handleCollision(this.bottom.getBoundsInParent());
        final boolean result = collidedWithLeft || collidedWithRight || collidedWithTop || collidedWithBottom;
        return result;
    }
}
