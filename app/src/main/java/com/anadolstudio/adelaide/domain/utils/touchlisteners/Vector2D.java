package com.anadolstudio.adelaide.domain.utils.touchlisteners;

import android.graphics.PointF;

class Vector2D extends PointF {

    static float getAngle(Vector2D vector1, Vector2D vector2) {
        vector1.normalize();
        vector2.normalize();
        double degrees = (180.0 / Math.PI) * (Math.atan2(vector2.y, vector2.x) - Math.atan2(vector1.y, vector1.x));
        return (float) degrees;
    }

    Vector2D() {
        super();
    }

    public Vector2D(float x, float y) {
        super(x, y);
    }

    private void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }
}