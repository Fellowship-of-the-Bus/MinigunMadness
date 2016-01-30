package com.github.fellowship_of_the_bus
package mgm
package game

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.geom.{Rectangle}

import lib.ui.{Drawable}
import lib.game.GameConfig.{Width,Height}
import lib.game.GameConfig
import lib.util.{TickTimer,TimerListener,FireN}
import lib.math.clamp
import scala.math._

class Bullet(xc: Float, yc: Float, angle: Float, var playerNum: Int) extends GameObject(xc, yc) {
	val rad = -1 * angle * (math.Pi / 180f).toFloat
	val speed = 0.5f
	val xVel = -1 * (speed * math.cos(rad).toFloat)
	val yVel = speed * math.sin(rad).toFloat
	
	val width = 20f // To do: base width on game size
	val height = width * 0.25f

	val velocity: (Float, Float) = (xVel, yVel)

	val image = images(Bullet);
	image.setCenterOfRotation(width/2, height/2)
    image.setRotation(angle)

    val shape = new Rectangle(0,0,width,height)
    def mesh = shape


    override def move() {
    	// Check Collsion
    	// If collides with player (with number != playerNum): deal damage and deactivate
    	// If collides with platform/Edge of map: deactivate
    	// Else: 
    	x = x + xVel
    	y = y + yVel
    }

    def draw() = {
		image.draw(x, y, width, height)
	}
}