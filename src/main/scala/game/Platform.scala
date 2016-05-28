package com.github.fellowship_of_the_bus
package mgm
package game
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}
import org.newdawn.slick.geom.{Polygon, Transform}
import scala.math.Pi

import lib.util.Native
import lib.game.GameConfig
import lib.game.TopLeftCoordinates

import lib.slick2d.ui._

trait platformType
case object TetrisL extends platformType
case object TetrisT extends platformType
case object TetrisS extends platformType
case object TetrisZ extends platformType
case object TetrisO extends platformType
case object TetrisI extends platformType
case object TetrisJ extends platformType
case object TetrisEmpty extends platformType

object Platform {
  def apply(xc: Float, yc: Float, shape: platformType, rotation: Int) = {
    val platform = shape match {
      case TetrisI =>
        new PlatformI(xc, yc, rotation)
      case TetrisJ =>
        new PlatformJ(xc, yc, rotation)
      case TetrisL =>
        new PlatformL(xc, yc, rotation)
      case TetrisT =>
        new PlatformT(xc, yc, rotation)
      case TetrisEmpty =>
        new PlatformEmpty(xc, yc, rotation)
      case _ =>
        new PlatformI(xc, yc, rotation)
    }
    platform
  }
}

abstract class Platform(xc: Float, yc: Float, var rotation: Int) extends GameObject(xc, yc) {
  // for now
  def width = GameConfig.Width/5
  def height = width
  def velocity = (0,1)

  def cellWidth = width/5
  def cellHeight = height/5

  var initialMesh = new Polygon()
  def image: Drawable

  def addMeshPoint(topLeftX: Int, topLeftY: Int) = {
    initialMesh.addPoint(cellWidth*topLeftX, cellHeight*topLeftY)
  }

  def init() {
    image.scaleFactor = width/500
    image.setCenterOfRotation(width/2, height/2)
    image.setRotation(rotation)
  }

  def draw(g: Graphics) = {
    image.draw(x, y)

    // var (prevx, prevy): (Float, Float) = (-1,-1)
    // for (i <- 0 until mesh.getPointCount()) {
    //   val px = mesh.getPoint(i)(0)
    //   val py = mesh.getPoint(i)(1)

    //   if (prevx == -1 && prevy == -1) {
    //     prevx = px
    //     prevy = py
    //   } else {
    //     g.drawGradientLine(x+prevx, y+prevy, Color.green, x+px, y+py, Color.green)
    //     prevx = px
    //     prevy = py
    //   }
    // }
  }

  //given a game object + velocity and returns an allowable velocity vector
  def collision(gameObject: GameObject, velocity: (Float, Float)): (Float, Float) = {
    val (dx, dy) = velocity
    // normalize gameObject to this region
    val (gx, gy) = (gameObject.x - x, gameObject.y - y)
    // check if moving will will send you in the platform
    val transform = Transform.createTranslateTransform(gx + dx, gy + dy)
    var translatedgMesh = gameObject.mesh.transform(transform)
    //check if full movement is allowable
    if (!mesh.intersects(translatedgMesh)) {
      return (dx, dy)
    }
    // binary search x to see get max x movment
    val tolerance = 0.01f
    var lowerBound = 0f
    var upperBound = 1f
    while (upperBound - lowerBound > tolerance) {
      translatedgMesh = gameObject.mesh.transform(Transform.createTranslateTransform(gx + dx*(lowerBound+upperBound)/2, gy))
      if (mesh.intersects(translatedgMesh)) {
        upperBound = (lowerBound+upperBound).toFloat/2f
      } else {
        lowerBound = (lowerBound+upperBound).toFloat/2f
      }
    }
    val allowabledx =
      if ((lowerBound + upperBound)/2 < tolerance) 0f
      else dx*lowerBound//dx*(lowerBound + upperBound)/2
    //binary search to get max y movement
    lowerBound = 0
    upperBound = 1
    while (upperBound - lowerBound > tolerance) {
      translatedgMesh = gameObject.mesh.transform(Transform.createTranslateTransform(gx + allowabledx,  gy + dy*(lowerBound+upperBound)/2))
      if (mesh.intersects(translatedgMesh)) {
        upperBound = (lowerBound+upperBound).toFloat/2f
      } else {
        lowerBound = (lowerBound+upperBound).toFloat/2f
      }
    }
    val allowabledy =
      if ((lowerBound + upperBound)/2 < tolerance) 0f
      else dy*lowerBound//(lowerBound + upperBound)/2
    return (allowabledx, allowabledy)
  }
}

class PlatformI(xc: Float, yc: Float, rot: Int) extends Platform(xc, yc, rot) {

  addMeshPoint(1, 2)
  addMeshPoint(4, 2)
  addMeshPoint(4, 3)
  addMeshPoint(1, 3)

  val finalMesh = initialMesh.transform(Transform.createRotateTransform((rotation*Pi).toFloat/180.0f, width/2, height/2))
  override def mesh = finalMesh

  val regionImage = images(IBlock).copy()
  override def image = regionImage
  init()
}

class PlatformJ(xc: Float, yc: Float, rot: Int) extends Platform(xc, yc, rot) {

  addMeshPoint(1, 2)
  addMeshPoint(2, 2)
  addMeshPoint(2, 3)
  addMeshPoint(4, 3)
  addMeshPoint(4, 4)
  addMeshPoint(1, 4)

  val finalMesh = initialMesh.transform(Transform.createRotateTransform((rotation*Pi).toFloat/180.0f, width/2, height/2))
  override def mesh = finalMesh

  val regionImage = images(JBlock).copy()
  override def image = regionImage
  init()
}

class PlatformL(xc: Float, yc: Float, rot: Int) extends Platform(xc, yc, rot) {

  addMeshPoint(1, 3)
  addMeshPoint(3, 3)
  addMeshPoint(3, 2)
  addMeshPoint(4, 2)
  addMeshPoint(4, 4)
  addMeshPoint(1, 4)

  val finalMesh = initialMesh.transform(Transform.createRotateTransform((rotation*Pi).toFloat/180.0f, width/2, height/2))
  override def mesh = finalMesh

  val regionImage = images(LBlock).copy()
  override def image = regionImage
  init()
}

class PlatformT(xc: Float, yc: Float, rot: Int) extends Platform(xc, yc, rot) {

  addMeshPoint(2, 1)
  addMeshPoint(3, 1)
  addMeshPoint(3, 3)
  addMeshPoint(4, 3)
  addMeshPoint(4, 4)
  addMeshPoint(1, 4)
  addMeshPoint(1, 3)
  addMeshPoint(2, 3)

  val finalMesh = initialMesh.transform(Transform.createRotateTransform((rotation*Pi).toFloat/180.0f, width/2, height/2))
  override def mesh = finalMesh

  val regionImage = images(TBlock).copy()
  override def image = regionImage
  init()
}

class PlatformEmpty(xc: Float, yc: Float, rot: Int) extends Platform(xc, yc, rot) {
  //single point mesh because apparently empty mesh does not work
  addMeshPoint(0, 0)
  override def mesh = initialMesh
  init()
  override def draw(g:Graphics) = {}
  override def image = images(TBlock).copy()

}
