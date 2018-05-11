package com.parse.starter.domain;

import java.io.Serializable;

/**
 * Created by henrique.carvalho on 10/05/2018.
 */

public class Image implements Serializable{

    private static final long serialVersionUID = -4559634123705134293L;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
