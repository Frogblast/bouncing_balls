package bouncing_balls;


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
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		float g = -9.8f; // acceleration of gravity
		double collisionThreshold = 0.0001;

		for (Ball b : balls) {
			// detect collision with the border
			if (b.x < b.radius || b.x > areaWidth - b.radius) {
				b.vx *= -1; // change direction of ball
			}
			if (b.y < b.radius || b.y > areaHeight - b.radius) {
				b.vy *= -1;
			}

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;

			// apply gravitation. v(t + deltaT) = v(t) + v'(deltaT) where v' = g = -9.8
			b.vy = b.vy + g*deltaT;

			// handle collisions with the other ball(s?)
			handleTwoBallsColliding(b);
		}
	}

	private void handleTwoBallsColliding(Ball b) {
		for(Ball b2 : balls){
			if (b == b2) break;

			// The two balls collide when the sum of their radii are equal to the length of the hypotenuse
				// of the right triangle where distance between x coordinates is the first leg
				// and the distance between y coordinates is the second leg.
				// the hypotenuse and the sum of radii are both squared in order to not having to take the costly square root...
			double distanceSquared = Math.pow((b2.x - b.x),2) + Math.pow((b2.y - b.y), 2);
			if (distanceSquared <= Math.pow(b.radius + b2.radius, 2)){
				applyCollisionForce(b, b2);
			}
		}
	}

	private static void applyCollisionForce(Ball b1, Ball b2) {
		// mass m assumed to be relative to the radius
		// u is velocity before collision and v is after
		double m1 = b1.radius;
		double m2 = b2.radius;
		double uX1 = b1.vx;
		double uY1 = b1.vy;
		double uX2 = b2.vx;
		double uY2 = b2.vy;

		double x1 = b1.x; // current positions for calculating the line between their centers
		double x2 = b2.x;
		double y1 = b1.y;
		double y2 = b2.y;

		// Center vector or line between the balls' centers on collision
		double dx = x2 - x1;
		double dy = y2 - y1;
		double magnitude = Math.sqrt(dx * dx + dy * dy);
		double[] centerVector = { dx / magnitude, dy / magnitude }; // Unit vector


		double hypotenuse = Math.sqrt(centerVector[0]*centerVector[0] + centerVector[1]*centerVector[1]);
		double cosAngle = centerVector[0]/hypotenuse;
		double sinAngle = centerVector[1]/hypotenuse;

		// new direction (apply the rotation matrix to the initial directions)
		b1.vx = uX1*cosAngle - uY1*sinAngle;
		b1.vy = uX1*sinAngle + uY1*cosAngle;
		b2.vx = uX2*cosAngle - uY2*sinAngle;
		b2.vy = uX2*sinAngle + uY2*cosAngle;
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius;


	}
}
