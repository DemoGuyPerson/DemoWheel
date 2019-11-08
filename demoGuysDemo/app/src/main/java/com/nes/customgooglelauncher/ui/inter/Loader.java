package com.nes.customgooglelauncher.ui.inter;


import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.mvp.presenter.AdapterHomePagePresenter;
import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 约束load
 * @param <T>
 *
 * @author liuqz
 */
public interface Loader<T> {

    /**
     * 小项RecyclerView异步加载数据的地方,加载完成之后会执行{@link #processLoadResult(int, List, AbsAdapter.AbsViewHolder, int, Map)},
     *
     * @param pagePresenter load操作的执行者,一般在{@link AbsAdapter}里面创建对象
     * @param holder 小项RecyclerView在外层RecyclerView --> Adapter{@link AbsAdapter}对应的{@link AbsAdapter.AbsViewHolder}
     * @param position 小项RecyclerView在外层RecyclerView --> Adapter{@link AbsAdapter}对应的position
     * @param map {@link AbsAdapter}中对每一小项的load情况的一个记录，防止同一时刻有两个相同position的任务在loading;一般会置为true
     */
    void load(AdapterHomePagePresenter pagePresenter, AbsAdapter.AbsViewHolder holder, int position, Map<AbsItem, Boolean> map);

    /**
     * 对{@link #load(AdapterHomePagePresenter, AbsAdapter.AbsViewHolder, int, Map)}的结果进行处理,
     *
     * @param code {@link #load(AdapterHomePagePresenter, AbsAdapter.AbsViewHolder, int, Map)}的结果code,表示是否成功
     * @param list {@link #load(AdapterHomePagePresenter, AbsAdapter.AbsViewHolder, int, Map)}的结果数据,用来刷新界面和保存数据
     * @param holder 小项RecyclerView在外层RecyclerView --> Adapter{@link AbsAdapter}对应的{@link AbsAdapter.AbsViewHolder}
     * @param position 小项RecyclerView在外层RecyclerView --> Adapter{@link AbsAdapter}对应的position
     * @param map {@link AbsAdapter}中对每一小项的load情况的一个记录，防止同一时刻有两个相同position的任务在loading;一般会置为false
     */
    void processLoadResult(int code,List<T> list, AbsAdapter.AbsViewHolder holder, int position, Map<AbsItem, Boolean> map);
}
