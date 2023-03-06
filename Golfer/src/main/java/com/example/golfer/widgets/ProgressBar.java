package com.example.golfer.widgets;

import com.example.golfer.Utilities;
import javafx.scene.Node;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;

public class ProgressBar extends Group
{
    private Rectangle progressBar;
    private Scale scale;
    private State state;
    private double time;

    public ProgressBar(final double width, final double height) {
        this.progressBar = new Rectangle(width, height, Color.RED);
        this.state = State.IDLE;
        this.scale = new Scale(1.0, 0.0);
        this.progressBar.getTransforms().addAll(new Translate(0.0, height), this.scale, new Translate(0.0, -height));
    }

    public void onClick() {
        super.getChildren().addAll(this.progressBar);
        this.scale.setY(0.0);
        this.state = State.CLICKED;
        this.time = 0.0;
    }

    public void elapsed(final long dns, final double max) {
        if (this.state == State.CLICKED) {
            this.time += dns;
            final double value = Utilities.clamp(this.time / max, 0.0, 1.0);
            this.scale.setY(value);
        }
    }

    public void onRelease() {
        super.getChildren().remove(this.progressBar);
        this.scale.setY(0.0);
        this.state = State.IDLE;
        this.time = 0.0;
    }

    private enum State
    {
        IDLE,
        CLICKED;


    }
}
