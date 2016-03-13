package function

import au.id.cxd.math.function.NumericIntegral
import au.id.cxd.math.probability.continuous.Normal
import org.scalatest.{ShouldMatchers, FlatSpec}

/**
  * Created by cd on 13/03/2016.
  */
class TestTrapIntegrate extends FlatSpec with ShouldMatchers {

  "Integrate std normal" should "equal expected value" in {
    // standard normal distribution p-value between -1.0 and 1.0
    // P(x < 1.0) - P(x < -1.0) = pnorm(1.0) - pnorm(-1.0)
    /*
    > pnorm(1.0, 0.0, 1) - pnorm(-1.0, 0, 1)
    [1] 0.6826895
     */

    val norm = Normal(0.0)(1.0)
    def func = norm.pdf(_)
    val integral = NumericIntegral(-1.0, 1.0, func)
    val result1 =  integral.integrate()

    result1 > 0.6 && result1 < 0.7 should be (true)

  }

}
