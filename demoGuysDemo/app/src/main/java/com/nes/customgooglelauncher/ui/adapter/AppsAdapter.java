package com.nes.customgooglelauncher.ui.adapter;


import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.presenter.AllAppAndGameAdapterPresenter;

/**
 * all app and all game对应的Adapter
 * @author liuqz
 */
public class AppsAdapter extends AbsAdapter{

    @Override
    public int getItemResId() {
        return R.layout.item_app;
    }

    /**
     * 移除特定包名的item项,委托给{@link com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter}
     * @param pkgName 包名
     * @return 在显示的view中是否找到了该包名
     */
    public boolean removeApp(String pkgName){
        boolean result = false;
        if (getRealCount() > 0){
            for (int i = 0 ; i < getRealCount(); i++){
                AbsItem absItem = getItem(i);
                if (absItem != null && absItem.getAdapterPresenter() instanceof AllAppAndGameAdapterPresenter){
                    boolean tem = ((AllAppAndGameAdapterPresenter)absItem.getAdapterPresenter()).removeApp(this,pkgName,i);
                    if (tem){
                        result = true;
                        break;
                    }
                }
            }

        }
        return result;
    }
}
