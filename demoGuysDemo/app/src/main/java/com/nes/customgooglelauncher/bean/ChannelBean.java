package com.nes.customgooglelauncher.bean;


import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.utils.Util;

import java.util.List;

/**
 * Channel和List<>{@link PreviewProgram}的对应关系
 *
 * @author liuqz
 */

public class ChannelBean {
    private Channel channel;
    private List<PreviewProgram> programs;
    private long channelId = -1L;

    /**
     * 由于Channel 不能修改数据，所以有一个值表示是否已经修改；
     * 修改状态，默认为-1,
     * 1表示修改为打开,
     * 0表示修改为关闭
     */
    private int changeState = -1;

    public int getChangeState() {
        return changeState;
    }

    public void setChangeState(int changeState) {
        this.changeState = changeState;
    }

    public boolean isChange(){
        return changeState != -1;
    }


    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<PreviewProgram> getPrograms() {
        return programs;
    }

    public void setPrograms(List<PreviewProgram> programs) {
        this.programs = programs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        ChannelBean that = (ChannelBean) o;
        if (channel != null){
            if (that.channel != null){
                return channel.getId() == that.channel.getId();
            }else{
                return channel.getId() == that.getChannelId();
            }
        }else{
            if (that.channel != null){
                return getChannelId() == that.channel.getId();
            }else{
                if (getChannelId() == -1 && that.getChannelId() == -1){
                    return false;
                }else {
                    return getChannelId() == that.getChannelId();
                }
            }
        }
    }

    public long getRealChannelId(){
        if (channel != null){
            return channel.getId();
        }
        return channelId;
    }

    public boolean isDataOK(){
        return channel!=null && Util.isNoEmptyList(programs);
    }

    @Override
    public int hashCode() {
        int result = channel.hashCode();
        result = 31 * result + programs.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChannelBean{" +
                "channel=" + (channel == null ? " null " : channel.toString()) +
                ", programs=" + (programs == null ? " null " : String.valueOf(programs.size())) +
                ", channelId=" + channelId +
                '}';
    }
}
