package com.github.fellowship_of_the_bus
package mgm

import lib.game.{IDMap, IDFactory}
import lib.slick2d.ui.{Image,Animation,Drawable}
import rapture.json._
import rapture.json.jsonBackends.jackson._

object ImageExtractor {
  lazy val imageExtractor =
    Json.extractor[String].map(name => Image(s"img/${name}") : Drawable)
  lazy val animationExtractor = Json.extractor[Array[String]].map(array => Animation(array.map(name => s"img/${name}")) : Drawable)
  implicit lazy val extractor = imageExtractor orElse animationExtractor

}
import ImageExtractor._

sealed trait ImageID
case object FotBLogo extends ImageID
case object Logo extends ImageID
case object IBlock extends ImageID
case object JBlock extends ImageID
case object LBlock extends ImageID
case object TBlock extends ImageID
case object Player1Walk extends ImageID
case object Player1Jetpack extends ImageID
case object Player2Walk extends ImageID
case object Player2Jetpack extends ImageID
case object Player3Walk extends ImageID
case object Player3Jetpack extends ImageID
case object Player4Walk extends ImageID
case object Player4Jetpack extends ImageID
case object Background extends ImageID
case object Bullet extends ImageID
case object Minigun extends ImageID

object ImageID {
  implicit object Factory extends IDFactory[ImageID] {

    val ids = Vector(FotBLogo, Logo, IBlock, JBlock, LBlock, TBlock,
     Player1Walk, Player1Jetpack, Player2Walk, Player2Jetpack, Player3Walk, Player3Jetpack, Player4Walk, Player4Jetpack,
     Background, Bullet, Minigun)// GameOver, Heart, TopBorder, Background)
  }
  implicit lazy val extractor =
    Json.extractor[String].map(Factory.fromString(_))
}

case object images extends IDMap[ImageID, Drawable]("data/images.json")
