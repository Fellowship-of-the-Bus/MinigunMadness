package com.github.fellowship_of_the_bus
package mgm
package game

import lib.game.TopLeftCoordinates
import org.newdawn.slick.geom.{Polygon, Shape}


abstract class GameObject(var x: Float, var y: Float) extends TopLeftCoordinates {
  // type IDKind <: ID
  // def id: IDKind

  // generic
  private var isActive = true
  def active = isActive
  def inactivate() = isActive = false

  def velocity: (Float, Float)
  def mesh: Shape

  def move() = {
    val (dx, dy) = velocity
    x = x + dx
    y = y + dy
  }
}
