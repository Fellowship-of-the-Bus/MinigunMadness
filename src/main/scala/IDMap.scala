package com.github.fellowship_of_the_bus
package mgm

import lib.game.{IDMap, IDFactory}
import lib.ui.{Image}
import rapture.json._
import rapture.json.jsonBackends.jackson._

// need to decide what an image looks like
case class ImageAttributes(name: String) {
  def img = new Image(name)
}

// image only
sealed trait ImageID
case object FotBLogo extends ImageID
case object Logo extends ImageID

object ImageID {
  implicit object Factory extends IDFactory[ImageID] {
    val ids = Vector(FotBLogo, Logo)// GameOver, Heart, TopBorder, Background)
  }
  // implicit lazy val extractor =
  //   Json.extractor[String].map(ImageID.fromString(_))
}

case object images extends IDMap[ImageID, ImageAttributes]("images.json")
