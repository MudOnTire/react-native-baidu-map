/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import {
  requireNativeComponent,
  View,
  NativeModules,
  Platform,
  DeviceEventEmitter
} from 'react-native';

import React, { Component } from 'react';
import PropTypes from 'prop-types';

export default class Polyline extends Component {
  static propTypes = {
    ...View.propTypes,
    points: PropTypes.array,
    color: PropTypes.string,
    width: PropTypes.number,
    visible: PropTypes.bool
  };

  static defaultProps = {
    points: [{
      lat: 0 + '',
      lng: 0 + ''
    },{
       lat: 0 + '',
       lng: 0 + ''
    }],
    color: 'FF00FF44',
    width: 8,
    visible: true
  };

  constructor() {
    super();
  }

  render() {
    return <BaiduMapOverlayPolyline {...this.props} />;
  }
}

const BaiduMapOverlayPolyline = requireNativeComponent('BaiduMapOverlayPolyline', Polyline);
