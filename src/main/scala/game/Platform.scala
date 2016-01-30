package com.github.fellowship_of_the_bus
package mgm
package game
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException, Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}
import org.newdawn.slick.geom.{Polygon, Transform}

import lib.util.Native
import lib.game.GameConfig
import lib.game.TopLeftCoordinates

import lib.ui._

trait platformType
case object tetrisL extends platformType
case object tetrisT extends platformType
case object tetrisS extends platformType
case object tetrisZ extends platformType
case object tetrisO extends platformType
case object tetrisI extends platformType
case object tetrisJ extends platformType

object Platform {
  def apply(xc: Int, yc: Int, shape: platformType, rotation: Int) = {
    var platform = shape match {
      case tetrisL => new PlatformL(xc, yc, rotation)
      case _ => () => ()
    }
    platform
  } 
}

abstract class Platform(xc: Int, yc: Int, var rotation: Int) extends GameObject(xc, yc) {
  // for now
  def width = 300
  def height = 300
  def velocity = (0,0)

  def cellWidth = width/5
  def cellHeight = height/5

  var initialMesh = new Polygon()
  def image: Drawable

  def addMeshPoint(topLeftX: Int, topLeftY: Int) = {
    initialMesh.addPoint(cellWidth*topLeftX, cellHeight*topLeftY)
  }

  def draw(g: Graphics) = {
    image.setCenterOfRotation(width/2, height/2)
    image.setRotation(rotation)
    image.draw(x, y)
  }

  //given a game object + velocity and returns an allowable velocity vector
  def collision(gameObject: GameObject, velocity: (Int, Int)): (Int, Int) = {
    val (dx, dy) = velocity
    // normalize gameObject to this region
    var (gx, gy) = (gameObject.x - x, gameObject.y - y)
    // check if moving will will send you in the platform
    var transform = Transform.createTranslateTransform(gx + dx, gy + dy)
    var translatedgMesh = gameObject.mesh.transform(transform)
    //check if full movement is allowable
    if (!mesh.intersects(translatedgMesh)) {
      return (dx, dy)
    }
    // binary search x to see get max x movment
    var lowerBound = 0
    var upperBound = 1
    while (upperBound - lowerBound > 0.1) {
      translatedgMesh = gameObject.mesh.transform(Transform.createTranslateTransform(gx + dx*(lowerBound+upperBound)/2, gy))
      if (mesh.intersects(translatedgMesh)) {
        upperBound = (lowerBound+upperBound)/2
      } else {
        lowerBound = (lowerBound+upperBound)/2
      }
    }
    val allowabledx = (lowerBound + upperBound)/2
    //binary search to get max y movement
    lowerBound = 0
    upperBound = 1
    while (upperBound - lowerBound > 0.1) {
      translatedgMesh = gameObject.mesh.transform(Transform.createTranslateTransform(gx + allowabledx,  gy + dy*(lowerBound+upperBound)/2))
      if (mesh.intersects(translatedgMesh)) {
        upperBound = (lowerBound+upperBound)/2
      } else {
        lowerBound = (lowerBound+upperBound)/2
      }
    }
    val allowabledy = (lowerBound + upperBound)/2
    return (allowabledx.toInt, allowabledy.toInt)
  }
}

class PlatformL(xc: Int, yc: Int, rotation: Int) extends Platform(xc, yc, rotation) {
  
  addMeshPoint(1, 3)
  addMeshPoint(4, 3)
  addMeshPoint(4, 4)
  addMeshPoint(1, 4)
  initialMesh.setLocation(cellWidth, cellHeight)

  val finalMesh = initialMesh.transform(Transform.createRotateTransform(rotation, width/2, height/2))
  override def mesh = finalMesh

  override def image = images(FotBLogo).copy()

}