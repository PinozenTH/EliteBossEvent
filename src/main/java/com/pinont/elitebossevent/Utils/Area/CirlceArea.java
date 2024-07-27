package com.pinont.elitebossevent.Utils.Area;

public class CirlceArea {
    private final double x;
    private final double y;
    private final double radius;

    public CirlceArea(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean isInside(double x, double y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2) <= Math.pow(radius, 2);
    }
}
