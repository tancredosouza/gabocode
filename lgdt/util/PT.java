package lgdt.util;

import java.lang.Math;

public class PT {
    public double x, y;

    public PT(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PT add(PT p) {
        return new PT(x + p.x, y + p.y);
    }

    public PT subtract(PT p) {
        return new PT(x - p.x, y - p.y);
    }

    public double dot(PT p) {
        return x * p.x + y * p.y;
    }

    public double cross(PT p) {
        return x * p.y - y * p.x;
    }

    public double angle(PT p) {
        return Math.atan2(cross(p), dot(p));
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public PT rotate(double angle) {
        double nx = x * Math.cos(angle) - y * Math.sin(angle),
               ny = x * Math.sin(angle) + y * Math.cos(angle);
        return new PT(nx, ny);
    }

    public PT rotateDegree(double angle) {
        return rotate(Math.PI * angle / 180);
    }

    public PT normalize() {
        double size = length();
        return new PT(x / size, y / size);
    }

    public PT scale(double a) {
        return new PT(x * a, y * a);
    }

    public double distance(PT p) {
        double dx = this.x - p.x, dy = this.y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }
}