package com.example.golfer.widgets;

import javafx.scene.text.Font;
import javafx.scene.Node;
import javafx.scene.transform.Translate;
import javafx.scene.transform.Transform;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.Group;

public class Status extends Group
{
    private Circle[] lives;
    private int numberOfLives;
    private int points;
    private Text text;

    public Status(final int numberOfLives, final double ballRadius, final double windowWidth) {
        this.lives = new Circle[numberOfLives];
        for (int i = 0; i < this.lives.length; ++i) {
            this.lives[i] = new Circle(ballRadius, Color.RED);
            this.lives[i].getTransforms().addAll(new Translate(windowWidth - (i + 1) * 3 * ballRadius, 2.0 * ballRadius));
            super.getChildren().addAll(this.lives[i]);
        }
        this.numberOfLives = numberOfLives;
        (this.text = new Text(Integer.toString(this.points))).setFont(Font.font(20.0));
        this.text.getTransforms().addAll(new Translate(10.0, 15.0));
        super.getChildren().addAll(this.text);
    }

    public boolean hasLives() {
        return this.numberOfLives > 0;
    }

    public void removeLife() {
        --this.numberOfLives;
        this.getChildren().remove(this.lives[this.numberOfLives]);
    }

    public void addLife(final double ballRadius, final double windowWidth) {
        ++this.numberOfLives;
        this.getChildren().removeAll(this.lives);
        this.lives = new Circle[numberOfLives];
        for (int i = 0; i < this.lives.length; ++i) {
            this.lives[i] = new Circle(ballRadius, Color.RED);
            this.lives[i].getTransforms().addAll(new Translate(windowWidth - (i + 1) * 3 * ballRadius, 2.0 * ballRadius));
            super.getChildren().addAll(this.lives[i]);
        }
    }
    public void addPoints(final int points) {
        this.points += points;
        this.text.setText(Integer.toString(this.points));
    }
}
