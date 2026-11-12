package com.example.lambda.domain;

public enum Urgency {
    LOW,
    MEDIUM,
    HIGH;

    public static Urgency classify(double evaluation) {
        if (evaluation <= 4) {
            return HIGH;
        }
        return evaluation < 7 ? MEDIUM : LOW;
    }
}
