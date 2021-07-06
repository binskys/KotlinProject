package com.ruiec.testkotlin.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author pengfaming
 * @date 2021/2/4 10:56
 */
@Entity
public class DataBean {
    
    private String name = "";

    @Generated(hash = 1022528227)
    public DataBean(String name) {
        this.name = name;
    }

    @Generated(hash = 908697775)
    public DataBean() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
