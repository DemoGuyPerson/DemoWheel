package com.nes.customgooglelauncher.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.text.TextUtils;
import android.util.LongSparseArray;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;
import androidx.tvprovider.media.tv.TvContractCompat;
import androidx.tvprovider.media.tv.WatchNextProgram;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * TV 端Channel、Program 工具类
 * @author liuqz
 */
public class ChannelProgramUtils {

    private String mTag = ChannelProgramUtils.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile ChannelProgramUtils sChannelProgramUtils;

    private Context mContext;

    private ChannelProgramUtils(){
        mContext = MyApplication.getInstance();
    }

    public static ChannelProgramUtils getInstance(){
        if (sChannelProgramUtils == null){
            synchronized (ChannelProgramUtils.class){
                if (sChannelProgramUtils == null){
                    sChannelProgramUtils = new ChannelProgramUtils();
                }
            }
        }
        return sChannelProgramUtils;
    }

    /**
     * 根据包名获取到Channel信息
     * @param pkgName 包名
     * @return Channel信息,可能是多个
     */
    public List<Channel> getChannelByPkgName(String pkgName){
        List<Channel> channels = null;
        try (Cursor cursor = mContext.getContentResolver().
                query(TvContractCompat.Channels.CONTENT_URI, null, "package_name=?", new String[]{pkgName},null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Channel channel = Channel.fromCursor(cursor);
                    if (channels == null){
                        channels = new ArrayList<>();
                    }
                    channels.add(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    /**
     * 根据Channel id获取Channel信息
     * @param channelId Channel id
     * @return Channel信息,只可能有一个
     */
    Channel getChannelByChannelId(long channelId){
        Channel channel = null;
        try (Cursor cursor = mContext.getContentResolver().
                query(TvContractCompat.Channels.CONTENT_URI, null, "_id=?", new String[]{String.valueOf(channelId)},null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    channel = Channel.fromCursor(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * 获取全部打开的Channel
     * @return List<>{@link Channel}
     */
    public List<Channel> getAllShowChannel(){
        return getAllChannel(true);
    }

    /**
     * 获取全部的Channel
     * @return List<>{@link Channel}
     */
    public List<Channel> getAllChannel(){
        return getAllChannel(false);
    }

    /**
     * 查找所有Channel，是否要考虑过滤isBrowsable
     * @param isFilterShow true表示查找所有显示的Channel
     * @return List<>{@link Channel}
     */
    private List<Channel> getAllChannel(boolean isFilterShow){
        List<Channel> channels = null;
        try (Cursor cursor = mContext.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Channel channel = Channel.fromCursor(cursor);
                    LogX.d(mTag +" getAllChannel channel id : "+channel.getId());
                    boolean isSkip = false;
                    if (channel.getInternalProviderDataByteArray() != null) {
                        String pro = new String(channel.getInternalProviderDataByteArray());
                        LogX.d(mTag +" getAllChannel channel id : "+channel.getId()+" pro : "+pro);
                        isSkip = pro.contains("sponsored.legacy");
                    }
                    if (!isSkip) {
                        isSkip = isFilterShow && (!channel.isBrowsable());
                    }
                    if (!isSkip && ("TYPE_PREVIEW".equals(channel.getType()))) {
                        LogX.i("Channel", "channel:" + channel.toString());
                        if (channel.getPackageName().equals(Constants.GOOGLE_TV_RECOMMEND_PACKAGENAME) && "Apps Spotlight".equals(channel.getDisplayName())) {
                            //屏蔽
                            LogX.i("Ignore Apps Spotlight:" + channel.toString());
                            continue;
                        }
                        if (channels == null){
                            channels = new ArrayList<>();
                        }
                        channels.add(channel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    /**
     * 根据Channel列表,查找出Channel对应的Program列表
     * @param list List<>{@link Channel}
     * @return List<>{@link ChannelBean}
     */
    @SuppressLint("RestrictedApi")
    public List<ChannelBean> getProgramList(List<Channel> list){
        List<ChannelBean> channelBeans = null;
        String sql = getSqlByChannelList(list);
        if (TextUtils.isEmpty(sql)){
            return channelBeans;
        }
        try (Cursor cursor = mContext.getContentResolver().query(TvContractCompat.PreviewPrograms.CONTENT_URI, null, sql, null, null)) {
            if (cursor != null) {
                LongSparseArray<Channel> sparseArray = getSparseArrayByChannelList(list);
                LogX.d(mTag +" getProgramList cursor count : "+cursor.getCount()+" sparseArray.size : "+sparseArray.size());
                LongSparseArray<List<PreviewProgram>> previewPrograms = new LongSparseArray<>();
                while (cursor.moveToNext()) {
                    PreviewProgram program = PreviewProgram.fromCursor(cursor);
                    if (!program.isBrowsable()){
                        continue;
                    }
                    long channelId = program.getChannelId();
                    if (previewPrograms.indexOfKey(channelId) <= -1){
                        previewPrograms.append(channelId,new ArrayList<PreviewProgram>());
                    }
                    previewPrograms.get(channelId).add(program);
                }
                channelBeans = new ArrayList<>();
                for (int i = 0, size = sparseArray.size(); i < size; i++){
                    long channelId = sparseArray.keyAt(i);
                    ChannelBean channelBean = new ChannelBean();
                    channelBean.setChannel(sparseArray.get(channelId));
                    channelBean.setPrograms(previewPrograms.get(channelId));
                    channelBeans.add(channelBean);
                }
            }else{
                LogX.d(mTag +" getProgramList cursor == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogX.e(mTag +" getProgramList e : "+e.getMessage());
        }
        return channelBeans;
    }

    /**
     * 根据channel id 查询到对应的 Program
     * @param channelId channel id
     * @return List<>{@link PreviewProgram}
     */
    public List<PreviewProgram> getProgramList(long channelId){
        List<PreviewProgram> list = null;
        try (Cursor cursor = mContext.getContentResolver().query(TvContractCompat.PreviewPrograms.CONTENT_URI,
                null, "channel_id=?", new String[]{String.valueOf(channelId)}, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PreviewProgram program = PreviewProgram.fromCursor(cursor);
                    if (list == null){
                        list = new ArrayList<>();
                    }
                    list.add(program);
                }
            }else{
                LogX.d(mTag +" getProgramList cursor == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogX.e(mTag +" getProgramList e : "+e.getMessage());
        }
        return list;
    }

    private String getSqlByChannelList(List<Channel> list){
        StringBuilder sb = new StringBuilder();
        if (Util.isNoEmptyList(list)){
            boolean isFirst = true;
            for (int i = 0, size = list.size(); i < size; i++){
                Channel channel = list.get(i);
                if (channel != null){
                    if (isFirst){
                        isFirst = false;
                    }else{
                        sb.append(" OR ");
                    }
                    sb.append("channel_id=").append(channel.getId());
                }
            }

        }
        LogX.d(mTag +" getSqlByChannelList sql : " + sb.toString());
        return sb.toString();
    }

    private LongSparseArray<Channel> getSparseArrayByChannelList(List<Channel> list){
        LongSparseArray<Channel> sparseArray = new LongSparseArray<>();
        if (Util.isNoEmptyList(list)){
            for (Channel channel : list){
                sparseArray.append(channel.getId(),channel);
            }
        }
        return sparseArray;
    }

    /**
     * 显示或者隐藏Channel
     *
     * @param id program id
     * @param isBrowsable 是否关闭
     */
    public void updateBrowsable(long id, boolean isBrowsable) {
        ContentValues values = new ContentValues();
        values.put("browsable", isBrowsable ? 1 : 0);
        Uri uri = TvContractCompat.Channels.CONTENT_URI;
        mContext.getContentResolver().update(Uri.parse(uri.toString() + "/" + id), values, null, null);
    }

    ChannelBean getChannelBean(Uri uri) {
        ChannelBean channelBean = new ChannelBean();
        try (Cursor cursor = mContext.getContentResolver().query(uri, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Channel channel = Channel.fromCursor(cursor);
                    channelBean.setChannel(channel);
                    channelBean.setPrograms(getProgramList(channel.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            channelBean = null;
        }
        return channelBean;
    }

    /**
     * 根据uri获取到{@link PreviewProgram}
     * @param uri {@link Uri}
     * @return {@link PreviewProgram}
     */
    PreviewProgram getProgram(Uri uri) {
        PreviewProgram program = null;
        try (Cursor cursor = mContext.getContentResolver().query(uri, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    program = PreviewProgram.fromCursor(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return program;
    }

    /**
     * 根据 program id删除对应的program
     * @param programid program id
     */
    public void deleteProgram(long programid) {
        Uri uri = TvContractCompat.PreviewPrograms.CONTENT_URI;
        String uriStr = uri.toString() + "/" + programid;
        int delete = -1;
        try {
            delete = mContext.getContentResolver().delete(Uri.parse(uriStr), null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        LogX.d(mTag +" deleteProgram uriStr : "+uriStr+" delete : "+delete);
    }

    public void addPlayNext(PreviewProgram previewPrograms) {
        try {
            mContext.getContentResolver().insert(TvContractCompat.WatchNextPrograms.CONTENT_URI, transitionPreviewPrograms(previewPrograms).toContentValues());
        } catch (Exception e) {
            e.printStackTrace();
            LogX.e("AddPlayNext:" + e.getMessage());
        }
    }

    @SuppressLint({"WrongConstant", "RestrictedApi"})
    private WatchNextProgram transitionPreviewPrograms(PreviewProgram previewProgram) {
        WatchNextProgram.Builder builder = null;
        try {
            builder = new WatchNextProgram.Builder();

            builder.setWatchNextType(TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_NEW)
                    .setBrowsable(true)
                    .setDescription(previewProgram.getDescription())
                    .setIntentUri(previewProgram.getIntentUri())
                    .setIntent(previewProgram.getIntent())
                    .setAuthor(previewProgram.getAuthor())
                    .setAudioLanguages(previewProgram.getAudioLanguages())
                    .setCanonicalGenres(previewProgram.getCanonicalGenres())
                    .setAvailability(previewProgram.getAvailability())
                    .setContentId(previewProgram.getContentId())
                    .setInteractionCount(previewProgram.getInteractionCount())
                    .setType(previewProgram.getType())
                    .setPackageName(previewProgram.getPackageName())
                    .setTitle(previewProgram.getTitle())
                    .setLogoUri(previewProgram.getLogoUri())
                    .setLongDescription(previewProgram.getLongDescription())
                    .setContentRatings(previewProgram.getContentRatings())
                    .setVideoWidth(previewProgram.getVideoWidth())
                    .setVideoHeight(previewProgram.getVideoHeight())
                    .setThumbnailUri(previewProgram.getThumbnailUri())
                    .setThumbnailAspectRatio(previewProgram.getThumbnailAspectRatio())
                    .setPosterArtAspectRatio(previewProgram.getPosterArtAspectRatio())
                    .setSearchable(previewProgram.isSearchable())
                    .setDurationMillis(previewProgram.getDurationMillis())
                    .setPosterArtUri(previewProgram.getPosterArtUri())
                    .setInternalProviderId(previewProgram.getInternalProviderId())
                    .setTransient(previewProgram.isTransient())
                    .setOfferPrice(previewProgram.getOfferPrice())
                    .setReleaseDate(previewProgram.getReleaseDate())
                    .setLastEngagementTimeUtcMillis(System.currentTimeMillis());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return builder.build();
    }
}
