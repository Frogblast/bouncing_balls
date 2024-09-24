package bouncing_balls;


import java.awt.*;
import java.util.Random;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 *
 */
class Model {

	double areaWidth, areaHeight;

	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls
		balls = new Ball[3];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, Color.BLUE);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, Color.RED);
		balls[2] = new Ball(4 * width / 6, height * 0.5, -1.5, 0.3, 0.1, Color.GREEN);

	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		float g = -9.8f; // acceleration of gravity
		double collisionMargin = 0.01; // to avoid overlapping

		for (Ball b : balls) {

			b.collisionTimer += deltaT;

			// detect collision with the border
			if (b.x - collisionMargin < b.radius || b.x + collisionMargin > areaWidth - b.radius) {
				b.vx *= -1; // change direction of ball
			}
			if (b.y - collisionMargin < b.radius || b.y + collisionMargin > areaHeight - b.radius) {
				b.vy *= -1;
			}

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;

			// apply gravitation. v(t + deltaT) = v(t) + v'(deltaT) where v' = g = -9.8
			b.vy = b.vy + g*deltaT;

			// handle collisions with the other ball(s?)
			handleTwoBallsColliding(b, collisionMargin, deltaT);
		}
	}

	private void handleTwoBallsColliding(Ball b, double collisionMargin, double collisionTimer) {
		for(Ball b2 : balls){
			if (b == b2) break;

			// The two balls collide when the sum of their radii are equal to the length of the hypotenuse
			// of the right triangle where distance between x coordinates is the first leg
			// and the distance between y coordinates is the second leg.
			// the hypotenuse and the sum of radii are both squared in order to not having to take the costly square root...
			if (b.collisionTimer > collisionTimer*2){
				double distanceSquared = Math.pow((b2.x - b.x),2) + Math.pow((b2.y - b.y), 2);
				if (distanceSquared <= Math.pow(b.radius + b2.radius, 2) + collisionMargin){
					transferMomentum(b, b2);
					b.collisionTimer = 0;
				}
			}
		}
	}

	private static void transferMomentum(Ball b1, Ball b2) {
		// mass m assumed to be relative to the radius
		// u is velocity before collision and v is after
		double m1 = b1.radius;
		double m2 = b2.radius;

		// initial velocities (x and y respectively)
		double uX1 = b1.vx;
		double uY1 = b1.vy;
		double uX2 = b2.vx;
		double uY2 = b2.vy;

		// current positions for calculating the line between their centers
		double pX1 = b1.x;
		double pX2 = b2.x;
		double pY1 = b1.y;
		double pY2 = b2.y;

		// Line between the balls' centers on collision which is the direction of the transferred momentum. (This becomes the new x-axis).
		double distX = pX2 - pX1;
		double distY = pY2 - pY1;
		double magnitude = Math.sqrt(distX * distX + distY * distY);
		double[] momentumVector = { distX / magnitude, distY / magnitude }; // Make it a unit vector

		// Project the initial velocities unto the momentum vector (or new x-axis). OBS! Scalar and not vector! Needs to be multiplied back onto
		// the momentumVector in order to find the correct vectors (or values for their x and y components)
		double u1projected = uX1*momentumVector[0] + uY1*momentumVector[1];
		double u2projected = uX2*momentumVector[0] + uY2*momentumVector[1];

		// swap velocities (since the velocities are now projected to the direction of momentum they can now be exchanged).
		// TODO: take masses and the law of preservation of kinetic energy (elastic collision) into account
		double temp = u1projected;
		u1projected = u2projected;
		u2projected = temp;

		// the exchanged velocities projected to the correct axes. This is the final velocities
		b1.vx = u1projected*momentumVector[0];
		b1.vy = u1projected*momentumVector[1];
		b2.vx = u2projected*momentumVector[0];
		b2.vy = u2projected*momentumVector[1];
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		/*Collision timer increases with deltaT on each step. If this number is above a certain threshold
		 * the collision will register and this timer will be set to 0. An ugly bug fix...
		 * */
		double collisionTimer = 0;
		Color color;

		Ball(double x, double y, double vx, double vy, double r, Color c) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.color = c;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius;


	}
}
