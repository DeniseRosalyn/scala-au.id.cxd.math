import au.id.cxd.math.count.Factorial
import au.id.cxd.math.function._
import au.id.cxd.math.function.beta.BetaFn
import au.id.cxd.math.function.gamma.GammaFn
import au.id.cxd.math.probability.analysis.{CriticalValue, LowerTail, UpperTail}
import au.id.cxd.math.probability.continuous.FDistribution
import au.id.cxd.math.probability.analysis.CriticalValue._

import scala.collection.immutable.Stream

val numeratorDf = 1.0
val denominatorDf = 2.0
val y = 1.2

val beta = BetaFn(numeratorDf / 2.0)(denominatorDf / 2.0)


val a = Math.pow(numeratorDf * y, numeratorDf) * Math.pow(denominatorDf, denominatorDf)
val b = Math.pow(numeratorDf * y + denominatorDf, numeratorDf + denominatorDf)
val c = Math.sqrt(a / b)
val d = y * beta
(c / d)

val a2 = Math.sqrt(Math.pow(denominatorDf, denominatorDf)) * Math.sqrt(Math.pow(numeratorDf, numeratorDf)) * Math.sqrt(Math.pow(y, numeratorDf - 2.0))
val b2 = Math.sqrt(Math.pow(denominatorDf + numeratorDf * y, numeratorDf + denominatorDf)) * beta
a2/b2

/**
  * > df(c(1, 1.1, 1.2), 2, 2)
  * [1] 0.2500000 0.2267574 0.2066116
  * > df(c(1, 1.1, 1.2), 2, 1)
  * [1] 0.1924501 0.1746928 0.1595077
  **/

val a3 = GammaFn((numeratorDf + denominatorDf) / 2.0)


// perform inferential test of F statistic.


val fdist = FDistribution(2.0, 11.0)
/*
> df(1,2,11)
[1] 0.3376142

*/
val p = fdist.pdf(1.0)

println("")

/**
df(1:10,2,11)
 [1] 0.337614168 0.133186213 0.059038384 0.028651941 0.014949513 0.008276020
 [7] 0.004813294 0.002918690 0.001834279 0.001189054

  */
val temp = sequence(1, by = 1).take(10).toList

 temp.map {
   i => {
     val f = fdist.pdf(3.0)
     println(s"df($i) = $f")
   }
 }

fdist.pdf(3.0)

/**
> pf(0.001,2,11)
[1] 0.0009994094

  */
val cP = fdist.cdf(0.001)

/**
> pf(0.05,2,11)
[1] 0.04855566

  *
  */
val cP2 = fdist.cdf(0.05)



/**
> pf(0.05,2,11,lower.tail=FALSE)
[1] 0.9514443
>

  The above computes CDF for 0.05 at the upper tail of the distribution

  however the F distribution does not terminate at 1.0 given the df1 and df2
  hence we need to determine the 0.05 critical value at the upper
  tail in order to provide the appropriate parameter for this method.

  Hence we cannot use 1.0 - 0.05 to compute this value

  Instead we need the quantile which as shown below is 3.982298
  *
  */
val cP3 = fdist.cdf(3.982298)


/*
> qf(0.05,2,11,lower.tail=FALSE)
[1] 3.982298

Note: estimating the integral at a finer step size causes the process to take much longer
than desired.
Need to work with a more optimised numeric integration method than
the trapezoid approximation.

Note the current approximation gets close but it is not quite correct.
 */

val crit = CriticalValue(FDistribution(2, 11), UpperTail())(_)

// note both sequence and array take a
// comparable time to compute the quantile, hence it
// will be more efficient to compute the inverse CDF via direct approximation
// instead of via the integration method.

//val r = (0.0 to 5.0 by 0.01).toArray

val r = sequence(0.0, by = 0.01).take(1500)

val i = crit(r)
  .value(0.05)

