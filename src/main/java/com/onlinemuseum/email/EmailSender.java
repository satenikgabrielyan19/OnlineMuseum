package com.onlinemuseum.email;

public interface EmailSender {
    void send(String to, String email,String subject);
}
