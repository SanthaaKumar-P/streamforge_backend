package com.streamforge.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        System.out.println("MANAGER = " +
                encoder.encode("manager123"));

        System.out.println("PRODUCER = " +
                encoder.encode("producer123"));

        System.out.println("DIRECTOR = " +
                encoder.encode("director123"));

        System.out.println("CREATOR = " +
                encoder.encode("creator123"));
    }
}