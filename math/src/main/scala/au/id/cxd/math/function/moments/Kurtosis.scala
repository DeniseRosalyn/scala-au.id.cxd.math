package au.id.cxd.math.function.moments

/**
  * empirical kurtosis
  * https://www.itl.nist.gov/div898/handbook/eda/section3/eda35b.htm
  *
  * k = \frac{\sum_{i=1}^N (X-\bar{X})^4 / N}{s^4)
  *
  * For standard normal distribution kurtosis should be E{X^4] = 3.
  *
  * Hence k - 3 should be 0.^
  */
class Kurtosis {
  def op(series:Seq[Double]):Double = {
    val n = series.length
    val mu = Mean(series)
    val s = Math.sqrt(Variance(series))
    val all = Seq(Math.pow(series.head - mu, 4.0)/n) ++ series.tail
    val fourth = all.reduce((b,a) => b + Math.pow(a - mu, 4.0) / n)
    fourth / Math.pow(s, 4.0)
  }
}
object Kurtosis {
  def apply(series:Seq[Double]):Double = new Kurtosis().op(series)
}
