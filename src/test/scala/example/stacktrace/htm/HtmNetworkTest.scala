package example.stacktrace.htm

import java.util
import java.util.UUID

import io.timeli.application.htm.EncoderMappings
import io.timeli.application.htm.EncoderMappings.FieldMapping
import org.joda.time.DateTime
import org.numenta.nupic.Parameters
import org.numenta.nupic.Parameters.KEY
import org.scalatest.{FunSuite, Matchers}
import utils.TupleConstructor

import scala.collection.JavaConverters._
import scala.util.Try

class HtmNetworkTest extends FunSuite with Matchers {

  test("test network throws a stack trace in the layer when Numenta Tuple data is sent to it") {

    //instantiate the network
    val nw = HtmNetwork(HtmNetworkConfig.p).network

    //create a map - start with one dimension in the coordinates vector, but the error is almost certainly independent of vector size
    val mapToSend = Map(
      "timestamp" -> DateTime.now,
      "value" -> TupleConstructor.fromSequence(values(10), Double.box(10)),
      "identity" -> UUID.randomUUID().toString).asJava

    //compute immediately with the map
    val inference = nw.computeImmediate(mapToSend)
    Option(inference) should not be None            //this test needs to fail, the exception occurs asynchronously
  }

  def values(dimensionality: Int): util.List[Integer] = (1 to dimensionality).map(_ => math.random() * 100).map(_.toInt).map(Integer.valueOf).asJava

}

object HtmNetworkConfig {
  import io.timeli.application.htm.EncoderMappings.ParameterAdditions
  val p: Parameters = Parameters.getAllDefaultParameters ++ Seq(
    EncoderMappings.FieldMapping("timestamp", 100, 15, 0, 0, 0, 0, None, None, None, Some("datetime"), Some("DateEncoder"),
      Map(
        KEY.DATEFIELD_PATTERN.getFieldName -> "YYYY-MM-dd'T'HH:mm:ss.SSSZ",
        KEY.DATEFIELD_DOFW.getFieldName -> new org.numenta.nupic.util.Tuple(Integer.valueOf(7), Double.box(1)),
        KEY.DATEFIELD_HOLIDAY.getFieldName -> new org.numenta.nupic.util.Tuple(Integer.valueOf(1)),
        KEY.DATEFIELD_SEASON.getFieldName -> new org.numenta.nupic.util.Tuple(Integer.valueOf(21), Double.box(91.5)),
        KEY.DATEFIELD_TOFD.getFieldName -> new org.numenta.nupic.util.Tuple( Integer.valueOf(23), Double.box(1))
      )
    ),
    FieldMapping("value", 2500, 21, Double.MinValue, Double.MaxValue, 0, 0, fieldType = Some("float"), encoderType = Some("CoordinateEncoder"),
      additional = Map()),
    FieldMapping("identity", 1024, 21, 0, 1E8, 0, 0, fieldType = Some("string"), forced = Some(false), encoderType = Some("SDRCategoryEncoder"),
      additional = Map(KEY.CATEGORY_LIST.getFieldName -> new java.util.ArrayList()))
  ) union {
    val also = Parameters.empty
    also.set(KEY.DATEFIELD_PATTERN, "YYYY-mm-dd`T`HH:mm:ssZ")
    also
  }
}
