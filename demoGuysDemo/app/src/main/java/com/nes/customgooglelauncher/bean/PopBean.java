package com.nes.customgooglelauncher.bean;

/**
 * PopupWindow --> RecyclerView --> item实体类
 * @author liuqz
 */
public class PopBean {
    private String name;
    private boolean isEnable = true;
    private int res = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }


    @Override
    public String toString() {
        return "PopBean{" +
                "name='" + name + '\'' +
                ", isEnable=" + isEnable +
                ", res=" + res +
                '}';
    }
}
