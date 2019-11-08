package com.nes.customgooglelauncher.bean;

import java.util.List;

/**
 * 默认{@link ChannelBean} --> List<>{@link ChannelBean}的对应关系
 *
 * @author liuqz
 */
public class CustomChannelBean {

    public CustomChannelBean(List<ChannelBean> channelBeans, ChannelBean defBean) {
        this.channelBeans = channelBeans;
        this.defBean = defBean;
    }

    private List<ChannelBean> channelBeans;
    private ChannelBean  defBean;

    public CustomChannelBean() {
    }

    public List<ChannelBean> getChannelBeans() {
        return channelBeans;
    }

    public void setChannelBeans(List<ChannelBean> channelBeans) {
        this.channelBeans = channelBeans;
    }

    public ChannelBean getDefBean() {
        return defBean;
    }

    public void setDefBean(ChannelBean defBean) {
        this.defBean = defBean;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomChannelBean)) return false;

        CustomChannelBean bean = (CustomChannelBean) o;

        return getDefBean().getChannel().getPackageName().equals(bean.getDefBean().getChannel().getPackageName());
    }

    @Override
    public int hashCode() {
        return getDefBean().hashCode();
    }
}
