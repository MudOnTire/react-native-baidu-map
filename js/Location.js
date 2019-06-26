/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import {
  requireNativeComponent,
  NativeModules,
  Platform,
  DeviceEventEmitter
} from 'react-native';

import React, {
  Component,
  PropTypes
} from 'react';


const _module = NativeModules.BaiduLocationModule;

export default {
    config(key) {
        return new Promise((resolve, reject) => {
                           try {
                           _module.config(key);
                           } catch (e) {
                           reject(e);
                           return;
                           }
                           DeviceEventEmitter.once('onLocationModuleCheckPermission', resp => {
                                                   resp.errcode = parseInt(resp.errcode);
                                                   resolve(resp);
                                                   });
                           })
    },
    locationTimeout(time) {
        _module.locationTimeout(time);
    },
    allowsBackground() {
        _module.allowsBackground
    },
    startUpdatingLocation() {
        return new Promise((resolve, reject) => {
                           try {
                           _module.startUpdatingLocation();
                           } catch (e) {
                           reject(e);
                           return;
                           }
                           DeviceEventEmitter.once('onLocationModuleFail', resp => {
                                                   resp.errcode = parseInt(resp.errcode);
                                                   resolve(resp);
                                                   });
                           DeviceEventEmitter.once('onLocationModuleUpdateLocation', resp => {
                                                   resp.latitude = parseFloat(resp.latitude);
                                                   resp.longitude = parseFloat(resp.longitude);
                                                   resolve(resp);
                                                   });
                           DeviceEventEmitter.once('onLocationModuleChangeAuthorization', resp => {
                                                   resp.status = parseInt(resp.status);
                                                   resolve(resp);
                                                   });
                           DeviceEventEmitter.once('onLocationModuleUpdateNetworkState', resp => {
                                                   resp.state = parseInt(resp.state);
                                                   resolve(resp);
                                                   });
                           });
    },
    stopUpdatingLocation() {
        _module.stopUpdatingLocation();
    },
    startUpdatingHeading() {
        return new Promise((resolve, reject) => {
                           try {
                           _module.startUpdatingHeading();
                           } catch (e) {
                           reject(e);
                           return;
                           }
                           DeviceEventEmitter.once('onLocationModuleUpdateHeading', resp => {
                                                   resp.magneticHeading = parseFloat(resp.magneticHeading);
                                                   resp.trueHeading = parseFloat(resp.trueHeading);
                                                   resp.headingAccuracy = parseFloat(resp.headingAccuracy);
                                                   resp.timestamp = parseFloat(resp.timestamp);
                                                   resolve(resp);
                                                   });
                           });
    },
    stopUpdatingHeading() {
        _module.stopUpdatingHeading();
    }
};
