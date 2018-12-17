package com.example.mikeygresl.template;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private String MID;
    private String timestamp;
    private String type;
    private String sender_id;
    private String rec_id;
    private CharSequence content;
    private String URL;

    public Message(String MID, String type, String sender_id, String rec_id, CharSequence content, String URL) {
        this.MID = MID;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        this.timestamp = simpleDateFormat.format(new Date());
        this.type = type;
        this.sender_id = sender_id;
        this.rec_id = rec_id;
        this.content = content;
        this.URL = URL;
    }

    public Message() {}

    public void setMID(String MID) {
        this.MID = MID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMID() {

        return MID;
    }

    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }

    public String getRec_id() {

        return rec_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSender_id() {
        return sender_id;
    }

    public CharSequence getContent() {
        return content;
    }

    public String getURL() {
        return URL;
    }

    public boolean hasImage() {

        return !TextUtils.isEmpty(getURL());
    }
}
