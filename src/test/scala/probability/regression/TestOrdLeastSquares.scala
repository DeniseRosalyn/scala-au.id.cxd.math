package probability.regression

import au.id.cxd.math.probability.regression.OrdLeastSquares
import breeze.linalg.{DenseMatrix, DenseVector}
import org.scalatest._

/**
 * Created by cd on 29/06/2014.
 */
class TestOrdLeastSquares extends FlatSpec with ShouldMatchers {

  "OrdLeastSquares" should "approximate sin" in {
    def testY(X:DenseMatrix[Double]):DenseVector[Double] = {
      DenseVector.tabulate[Double](X.cols) { i => Math.sin(X(0,i))}
    }
    val X1 = Array(0.0, Math.PI/2.0, Math.PI, Math.PI*2.0)
    val X2 = DenseMatrix.tabulate[Double](1,X1.length) {
      case (i, j) => X1(j)
    }
    val Y = testY(X2)
    val ols = OrdLeastSquares(X2, Y, 3, 0.5)
    val T1 = ols.train()
    val error = T1._2
    (error <= 0.5) should be (true)
    println(Y)
    println(T1)

    val Y2 = X1.map { x: Double => ols.predict(x) }
    println(Y2)
  }

}
