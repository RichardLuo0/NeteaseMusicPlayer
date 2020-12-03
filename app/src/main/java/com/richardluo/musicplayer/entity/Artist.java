package com.richardluo.musicplayer.entity;

import java.io.Serializable;

public class Artist implements Serializable {
    int id;
    String name;

    public String getName() {
        return name;
    }
}
