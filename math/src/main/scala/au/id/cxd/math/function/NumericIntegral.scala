package au.id.cxd.math.function

import breeze.linalg.{max, min}
/**
  * ##import MathJax
  *
  * Numeric integration for a given range
  *
  * determine the area beneath the curve
  * for the sequence generated by f(x) -> y
  * between the given upper and lower limits using
  * numeric quadrature
  *
  * This is based on the algorithms provided by the GNU Scientific Library
  *
  * http://savannah.gnu.org/projects/gsl
  *
  * git://git.savannah.gnu.org/gsl.git
  *
  * http://www.gnu.org/software/gsl/manual/html_node/Numerical-Integration.html#Numerical-Integration
  *
  * https://en.wikipedia.org/wiki/Numerical_integration
  *
  * https://en.wikipedia.org/wiki/Trapezoidal_Rule
  *
  * Created by cd on 17/11/2015.
  */
class NumericIntegral(val lower: Double, val upper: Double, val genFn: Double => Double) {

  /**
    * limit of lower and upper bounds
    */
  type Limit = (Double, Double)

  /**
    * a step is the output of an iteration
    * it includes the limit
    */
  type Step = (Double, Limit)

  /**
    * a container use to generate the next iteration of the numeric integration
    * it uses the mean between the function at a and b at each level (or step) in the operation
    * it returns the result and the next instance of the trapezoidal function that can be used to generate the next in sequence.
    */
  trait Trapezoid {

    val prevStep: Step

    def f: Double => Double

    def level: Int = 1

    /**
      * generate the next in sequence.
      * Based on method trapezoid_rule in the GSL.
      *
      * Implements the trapezoid rule
      *
      * @return
      */
    def next() = {
      val (xPrior, limit) = prevStep
      val (a, b) = limit

      val nextX = if (level == 1) {
        0.5 * (b - a) * (f(a) + f(b))
      }
      else {
        var len = 1
        for (i <- 1 to level-1) {
          len = len << 1
        }
        val delta = (b - a) / len
        val x = a + 0.5 * delta
        val steps = for (j <- 1 to len) yield (x + j * delta)
        val steps2 = steps map { xn => f(xn) }
        val sum = steps2 reduce (_ + _)
        delta * sum
      }
      val temp = level
      val tempF = f (_)
      val result = (0.5 * (nextX + xPrior), limit)
      (result, new Trapezoid {
        val prevStep = result
        override def level = temp + 1
        def f = tempF
      })

    }

  }

  /**
    * refine the trapezoidal approximation until either
    * a maximum is reached or the function converges
 *
    * @param epsilon
    * @return
    */
  def trapezoid(epsilon:Double = 1.0/Math.E, max:Int = 20): Double = {

    def step (max:Int, j:Int, trap:Trapezoid, oldS:Double):Double = (j == max) match {
      case true => oldS
      case _ => {
        val (nextS, nextT) = trap next()
        //if (j > (max+1)/2 && (Math.abs(nextS._1 - oldS) < epsilon * Math.abs(oldS) ) )  {
        //  nextS._1
        //} else
        step (max, j+1, nextT, nextS._1)
      }
    }

    // iterative method
    val trap = new Trapezoid {
      val prevStep = (0.0, (lower,upper))
      def f = genFn
    }
    step (max, 1, trap, 0.0)
  }

  /**
    * apply simpsons rule to determine the error in each step of the integration procedure.
    * The difference in the error at each iteration will be calculated as
    *
    * S = 4/3 S_{2N} - 1/3 S_N
    *
    * Based on the method
    *
    * gsl_integ_simpson from GSL
    *
    * @param epsilon
    * @return
    */
  def simpson(epsilon:Double = Math.E, max:Int = 20) : Double = {
    def step (max:Int, j:Int, trap:Trapezoid, prevStep:Double, oldS:Double):Double = (j == max) match {
      case true => oldS
      case _ => {
        val (nextS, nextT) = trap next()
        val curS = (4.0*nextS._1 - prevStep) / 3.0
        //if (j > (max+1)/2 && (Math.abs (curS - oldS) < epsilon * Math.abs(oldS)) ) {
        //  curS
        //} else
        step  (max, j+1, nextT, nextS._1, curS)
      }
    }

    val trap = new Trapezoid {
      val prevStep = (0.0, (lower, upper))
      def f = genFn
    }

    step (max, 1, trap, 0.0, 0.0)
  }

  /**
    * approximate the integral using the summation using the midpoint rule.
    *
    * This is a simplistic implementation of the approximate integral
    * a = x+deltax
    * b = x+2deltax
    * w = (a+b)/2
    * p = [pdf(a)+pdf(b)]*w
    * @return
    */
  def approxIntegral():Double = {
    (min(lower, upper) to max(lower, upper-0.01) by 0.01)
      .zip(lower+0.01 to upper by 0.01)
      .map {
        (pair:(Double, Double)) => {
          val a = genFn(pair._1)
          val b = genFn(pair._2)
          val w = (pair._2-pair._1)/2.0
          w*(a+b)
        }
      }.sum
  }

  /**
    * the default method of integration
 *
    * @param epsilon
    * @return
    */
  def integrate(epsilon:Double = Math.E) = trapezoid (epsilon)

  def integrateS (epsilon:Double = Math.E) = simpson (epsilon)

}

object NumericIntegral {

  def apply(lower: Double, upper: Double, genFn: Double => Double) = new NumericIntegral(lower, upper, genFn)

  def integrate  (lower: Double, upper: Double, genFn: Double => Double, epsilon:Double = Math.E) =
    NumericIntegral(lower, upper, genFn).integrate(epsilon)

  def integrateS  (lower: Double, upper: Double, genFn: Double => Double, epsilon:Double = Math.E) =
    NumericIntegral(lower, upper, genFn).integrateS(epsilon)

}
