package com.contenedoressatur.android.choferesandroid.notificaciones;

/**
 * Creado por user el 24/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class NotificationData
{
    public static final String TEXT = "TEXT";

    private String imageName;
    private int id;
    private String title;
    private String textMessage;
    private String sound;

    public NotificationData() {}

    public NotificationData(String imageName, int id, String title, String textMessage, String sound) {
        this.imageName = imageName;
        this.id = id;
        this.title = title;
        this.textMessage = textMessage;
        this.sound = sound;
    }

    public  String getImageName(String imageName) { return imageName; }
    public int getId () { return  id; }
    public String getTitle() { return title; }
    public String getTextMessage() { return textMessage; }
    public String getSound() { return sound; }

    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setTextMessage(String textMessage) { this.textMessage = textMessage; }
    public void setSound(String sound) { this.sound = sound; }




}
