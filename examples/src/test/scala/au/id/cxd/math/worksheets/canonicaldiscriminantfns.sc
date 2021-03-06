import au.id.cxd.math.data.MatrixReader
import au.id.cxd.math.example.charting.VegasHelper
import au.id.cxd.math.function.transform.StandardisedNormalisation
import au.id.cxd.math.model.components.CanonicalDiscriminantAnalysis
import vegas.DSL.Layer
import vegas.spec.Spec.MarkEnums.{Square, Text}
import vegas.{Point, Quantitative, Vegas}
import vegas.spec.Spec.TypeEnums.Nominal

import scala.io.Source
// investigate the use of canonical discriminant analysis.



val file:String = "/Users/cd/Projects/scala/au.id.cxd.math/data/employ_pop.csv"
val mat = MatrixReader.readFileAt(file)

val file2:String = "/Users/cd/Projects/scala/au.id.cxd.math/data/employ_countries.csv"
val countries = Source.fromFile(file2).getLines().toArray.tail



val m2 = mat(::, 1 to 9).toDenseMatrix

//println(m2)
// the data set is standardised prior to the procedure
val X = StandardisedNormalisation().transform(m2)
// we also know ahead of time that there are 5 groups in the data.
val groups = mat(::,0).toArray.map(_.toString).toList

// perform the analysis.

val (components, coeffs, intercept, percentVar, zMat, cor, groupMeans) = CanonicalDiscriminantAnalysis(groups, X)



percentVar

val attributes = List(
  "AGR",
  "MIN",
  "MAN",
  "PS",
  "CON",
  "SER",
  "FIN",
  "SPS",
  "TC"
)
val componentNames = (for (i <- 1 to cor.cols) yield i.toString)

val corData = (for (i <- 0 until attributes.length) yield i)
  .foldLeft(List[Map[String, Any]]()) {
  (accum1, i) => {
    (for (j <- 0 until componentNames.length) yield j)
      .foldLeft(accum1) {
        (accum, j) => {
          val d = cor(i,j)
          val record = Map(
            "component" -> componentNames(j),
            "attribute" -> attributes(i),
            "correlation" -> d
          )
          accum :+ record
        }
      }
  }
}


// correlations
cor.toString()

// plot the ordination.
val comp1 = zMat(::,0).toArray
val comp2 = zMat(::,1).toArray
val comp3 = zMat(::,2).toArray

val n = zMat.rows
val m = zMat.cols

// the length of the matrix corresponds to the number of distinct groups.
val dataset = comp1.zip(comp2).zip(countries).
  map { group => (group._1._1, group._1._2, group._2) }.
  map { group => Map("c1" -> group._1,
    "c2" -> group._2,
  "country" -> group._3)}


val plot1 = Vegas.layered("Ordination of CDF",
  width=800.0,
  height=600.0).
  withData(
    dataset
  ).withLayers(
    /*Layer().
      mark(Point).
        encodeX("c1", Quantitative).
        encodeY("c2", Quantitative).
        encodeColor(field="country", dataType=Nominal).
        encodeText(field="country", dataType=Nominal),
    */
  Layer().
      mark(Text).
        encodeX("c1", Quantitative).
        encodeY("c2", Quantitative).
        encodeColor(field="country", dataType=Nominal).
        encodeText(field="country", dataType=Nominal)
  )


VegasHelper.showPlot(plot1, fileName="docs/plots/plotcdf1.html")

// color ranges can be updated using:
// https://vega.github.io/vega/docs/schemes/#scheme-properties

// display a heatmap of attributes and correlation
// to the associated canonical component
val plot2 = Vegas("Heatmap of attribute and component correlation",
  width = 600d,
  height = 600d).
  withData(
    corData
  ).
  mark(Square).
  encodeX("component", Nominal).
  encodeY("attribute", Nominal).
  encodeColor(field="correlation", dataType=Quantitative)
  /*.
  configScale(
    sequentialColorRange=SequentialColorRangeListString(
      List("inferno")))
*/


VegasHelper.transformAndShowPlot(plot2,
  VegasHelper.replaceMark("rect", _),
  fileName="docs/plots/plotcor1.html")
