package com.appsinventiv.buyandsell.Activities.Chat;


public class ChatListModel {
    String username;
    ChatModel message;

    public ChatListModel(String username, ChatModel message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ChatModel getMessage() {
        return message;
    }

    public void setMessage(ChatModel message) {
        this.message = message;
    }
}
