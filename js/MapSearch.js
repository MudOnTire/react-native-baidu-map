import React, {Component} from 'react';
import {
  requireNativeComponent,
  NativeModules,
  Platform,
  DeviceEventEmitter
} from 'react-native';

const _module = NativeModules.BaiduSearchModule;

export default {
    //关键字检索
    requestSuggestion(city ,keyWords){
        return new Promise((resolve, reject) => {
              try {
                _module.requestSuggestion(city ,keyWords);
              }
              catch (e) {
                reject(e);
                return;
              }
              DeviceEventEmitter.once('onGetSuggestionResult', resp => {
                resolve(resp);
              });
            });
    },

    poiSearchNearby(lat,lng,radius,keyword){
            return new Promise((resolve, reject) => {
                  try {
                    _module.poiSearchNearby(lat,lng,radius,keyword);
                  }
                  catch (e) {
                    reject(e);
                    return;
                  }
                  DeviceEventEmitter.once('onGetPoiResult', resp => {
                    resolve(resp);
                  });
                });
        }

}

//关键词检索的使用例子
//MapSearch.requestSuggestion('深圳' ,'高新奇')
//            .then( data =>{
//                var list = data.sugList;
//                for(let i in list){
//                    console.log('Home',"city：" + list[i].city + ' ,district:' + list[i].district
//                    + ' ,key:' + list[i].key + ' ,latitude:' + list[i].latitude + ' ,longitude:' + list[i].longitude);
//                }
//            })
//            .catch(e =>{
//
//            });