package com.example.finalerhis;

public class UserSession {
    private static String position;

    public static String getPosition() {
        return position;
    }

    public static void setPosition(String position) {
        UserSession.position = position;
    }
}
