package com.richardluo.musicplayer.entity;

import java.io.Serializable;

public class Artist implements Serializable {
    public int id;
    public String name;

    public Artist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
