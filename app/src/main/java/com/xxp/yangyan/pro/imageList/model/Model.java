package com.xxp.yangyan.pro.imageList.model;

import com.xxp.yangyan.mvp.model.MvpModel;
import com.xxp.yangyan.pro.App;
import com.xxp.yangyan.pro.api.ApiEngine;
import com.xxp.yangyan.pro.entity.ImageInfo;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/5/2
 * Description :
 */

public class Model implements MvpModel {
    private final String TAG = "ImageListModelImpl";
    //加载图片详细的集合
    //每日最新的
    public static final String TYPE_NEW = "type_new";
    //自己收藏的(来自数据库)
    public static final String TYPE_COLLECT = "type_collect";
    //详细的写真集合
    public static final String TYPE_PARTICULARS = "type_particulars";


    //收藏,,数据库加载只加载一次,避免重复
    private Boolean isFirstLoad = true;

    public Observable getData(String type, int page) {
        switch (type) {
            case TYPE_NEW:
                return ApiEngine.getInstance().getHContentService().getHomePage(page);
            case TYPE_PARTICULARS:
                return ApiEngine.getInstance().getHContentService().getParticulars(page);
            case TYPE_COLLECT:
                return Observable.create(new Observable.OnSubscribe<List<ImageInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<ImageInfo>> subscriber) {
                        List<ImageInfo> imageinfos = App.getDaoSession().getImageInfoDao().loadAll();
                        if (imageinfos != null) {
                            subscriber.onNext(imageinfos);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new Throwable("数据库加载出错"));
                        }
                    }
                });
            default:
                return ApiEngine.getInstance().getHContentService().getTypePage(type, page);
        }

    }

}
