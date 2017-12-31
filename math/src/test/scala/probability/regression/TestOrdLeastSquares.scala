package probability.regression

import au.id.cxd.math.probability.regression.OrdLeastSquares
import breeze.linalg.{DenseMatrix, DenseVector}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import org.scalatest._

import scalax.chart.module.ChartFactories.XYLineChart

/**
 * Created by cd on 29/06/2014.
 */
class TestOrdLeastSquares extends FlatSpec with ShouldMatchers {

  lazy val input = {
    for (i <- 0.0 to Math.PI*2.0 by 0.1) yield i
  }

  "OrdLeastSquares" should "approximate sin" in {
    val X2 = DenseMatrix.tabulate[Double](input.length, 1) {
      case (i, j) => input(j)
    }
    val Y = DenseVector.tabulate[Double](input.length) {
      i => Math.sin(input(i))
    }
    val ols = OrdLeastSquares(X2, Y, 3)
    val T1 = ols.train()
    val error = T1._2
    println(s"Error: $error")
    println(Y)
    println(T1)

    val Y2 = input.map { x: Double => ols.predict(x) }
    println(Y2)

    val Y3 = ols.predictSeq(DenseVector.tabulate[Double](input.length) { j => input(j) })
    println(Y3)

  }

}