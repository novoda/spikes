import React from 'react'
import {
  Image,
  StyleSheet
} from 'react-native'

class AvatarView extends Image {
  getHighResolutionImage (originalURL) {
    return originalURL.replace('_normal', '_bigger')
  }

  render () {
    let source = {
      uri: this.getHighResolutionImage(this.props.uri)
    }

    let style = StyleSheet.create({
      image: {
        width: this.props.size,
        height: this.props.size,
        borderRadius: this.props.size / 10,
        borderWidth: 1,
        borderColor: '#E7E7E7'
      }
    })

    return <Image
      source= {source}
      style = {style.image}
      resizeMode='contain'/>
  }
}

module.exports = AvatarView
