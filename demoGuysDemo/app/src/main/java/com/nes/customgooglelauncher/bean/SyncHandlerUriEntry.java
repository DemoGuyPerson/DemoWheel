package com.nes.customgooglelauncher.bean;

import android.net.Uri;

/**
 * 数据库变化uri的实体类，一次uri变化看做是一个 SyncHandlerUriEntry
 *
 * @author liuqz
 */
public class SyncHandlerUriEntry {

    private final int TYPE_SYNC_ENTRY_CHANNEL = 1;
    private final int TYPE_SYNC_ENTRY_PROGRAM = 2;

    public SyncHandlerUriEntry(){

    }

    public SyncHandlerUriEntry(Uri uri, int type){
        this.uri = uri;
        this.type = type;
    }

    public Uri uri;
    public int type;
    public long currentTime;

    /**
     * 设置 Channel 类型的数据库变化
     * @param uri
     */
    public void setChannelUri(Uri uri){
        this.uri = uri;
        this.type = TYPE_SYNC_ENTRY_CHANNEL;
    }

    /**
     * 设置 Program 类型的数据库变化
     * @param uri
     */
    public void setProgramUri(Uri uri){
        this.uri = uri;
        this.type = TYPE_SYNC_ENTRY_PROGRAM;
    }

    public boolean isChannel(){
        return type==TYPE_SYNC_ENTRY_CHANNEL;
    }

    public boolean isProgram(){
        return type==TYPE_SYNC_ENTRY_PROGRAM;
    }

    @Override
    public String toString() {
        return "SyncHandlerUriEntry{" +
                "uri=" + uri +
                ", type=" + type +
                ", currentTime=" + currentTime +
                '}';
    }
}
