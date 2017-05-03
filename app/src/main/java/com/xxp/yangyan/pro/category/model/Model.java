package com.xxp.yangyan.pro.category.model;

import com.xxp.yangyan.mvp.model.MvpModel;
import com.xxp.yangyan.pro.api.ApiEngine;
import com.xxp.yangyan.pro.bean.CategoryInfoData;

import rx.Observable;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/5/2
 * Description :
 */

public class Model implements MvpModel {
    public Observable<CategoryInfoData> getData() {
        return ApiEngine.getInstance().getMMService().getCategoryPage();
    }
}
