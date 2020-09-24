package com.example.mytestproject;

import android.graphics.Path;

import java.util.ArrayList;

public class Line {
    ArrayList<ViewPoint> points = new ArrayList<ViewPoint>();
    private Path path = new Path();

    public ArrayList<ViewPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<ViewPoint> points) {
        this.points = points;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}

