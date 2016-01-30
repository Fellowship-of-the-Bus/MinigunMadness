package com.github.fellowship_of_the_bus
package mgm

import lib.game.{IDMap, IDFactory}
import lib.ui.{Image}
import rapture.json._
import rapture.json.jsonBackends.jackson._

object ImageExtractor {
  implicit lazy val extractor =
    Json.extractor[String].map(name => Image(s"img/${name}"))
}
import ImageExtractor._

sealed trait ImageID
case object FotBLogo extends ImageID
case object Logo extends ImageID
case object IBlock extends ImageID
case object JBlock extends ImageID
case object LBlock extends ImageID
case object TBlock extends ImageID

object ImageID {
  implicit object Factory extends IDFactory[ImageID] {
    val ids = Vector(FotBLogo, Logo, IBlock, JBlock, LBlock, TBlock)// GameOver, Heart, TopBorder, Background)
  }
  implicit lazy val extractor =
    Json.extractor[String].map(Factory.fromString(_))
}

case object images extends IDMap[ImageID, Image]("data/images.json")
