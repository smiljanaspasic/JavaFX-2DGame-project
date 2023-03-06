package com.example.golfer.objects;

import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Rectangle;

public class Obstacle extends Rectangle
{
    public Obstacle(final double width, final double height, final Translate position) {
        super(width, height, Color.GRAY);
        super.getTransforms().addAll(position);
    }
}