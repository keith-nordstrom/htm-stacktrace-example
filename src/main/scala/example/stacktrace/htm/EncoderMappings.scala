package io.timeli.application.htm

import org.numenta.nupic.Parameters
import org.numenta.nupic.Parameters.KEY

/**
  * Sugar for improving type safety on configuration
  */
object EncoderMappings {

  /**
    *
    * @param n      the total number of bits in the output encoding
    * @param w      the number of bits to use in the representation
    * @param min    the minimum value (if known i.e. for the ScalarEncoder)
    * @param max    the maximum value (if known i.e. for the ScalarEncoder)
    * @param radius see { @link Encoder}
    * @param resolution see { @link Encoder}
    * @param periodic    such as hours of the day or days of the week, which repeat in cycles
    * @param clip        whether the outliers should be clipped to the min and max values
    * @param forced      use the implied or explicitly stated ratio of w to n bits rather than the "suggested" number
    * @param fieldName   the name of the encoded field
    * @param fieldType   the data type of the field
    * @param encoderType the Camel case class name minus the .class suffix
    */
  final case class FieldMapping(fieldName: String, n: Int, w: Int, min: Double, max: Double, radius: Double, resolution: Double, periodic: Option[Boolean] = None, clip: Option[Boolean] = None, forced: Option[Boolean] = None, fieldType: Option[String] = None, encoderType: Option[String] = None, additional: Map[String, Any] = Map()) {
    def asMap: Map[String, Any] = {
      val result = Map[String, Any]("n" -> n , "w" -> w , "minVal" -> min, "maxVal" -> max, "radius" -> radius, "resolution" -> resolution)
      result ++ Map[String, Option[Any]](
        "periodic" -> periodic,
        "clipInput" -> clip,
        "forced" -> forced,
        "fieldName" -> Some(fieldName),
        "fieldType" -> fieldType,
        "encoderType" -> encoderType
      ).flatMap{ case (k, ov: Option[Any]) =>
        ov.map{ v => k -> v}
      } ++ additional
    }

  }

  /**
    * Adds an addition operator and a conversion to anything that looks like a Map[String, Map[String, Any]] (when imported)
    *
    * @param input scala map of mappings
    */
  implicit class MappingsAdditions(input: Map[String, Map[String, Any]]) {

    /**
      * Adds a Field Mapping to a map of them
      *
      * @param mapping mapping case class
      * @return
      */
    def +(mapping: FieldMapping): Map[String, Map[String, Any]] = {
      input + (mapping.fieldName -> (input.getOrElse(mapping.fieldName, Map[String, Any]()) ++ mapping.asMap))
    }

    def convertToJava: java.util.Map[String, java.util.Map[String, Any]] = {
      import scala.collection.JavaConverters._
      input.map{ case(k,v) => k -> v.asJava}.asJava
    }

  }

  /**
    * Adds a conversion from Java maps to Scala (when imported)
    *
    * @param map java map of mappings
    */
  implicit class JavaConversions(map: java.util.Map[String, java.util.Map[String, Any]]) {
    def convertToScala: Map[String, Map[String, Any]] = {
      import scala.collection.JavaConverters._
      map.asScala.map{ case(k,v: java.util.Map[String, Any]) => k -> scala.collection.JavaConverters.mapAsScalaMap(v).toMap}.toMap
    }
  }

  implicit class ParameterAdditions(config: Parameters) {
    def ++(fieldMappings: Seq[FieldMapping]): Parameters = {
      val original = Option(config.get(KEY.FIELD_ENCODING_MAP)).getOrElse(new java.util.HashMap[String, java.util.Map[String, Any]]()).asInstanceOf[java.util.Map[String, java.util.Map[String, Any]]]
      config.set(KEY.FIELD_ENCODING_MAP, fieldMappings.foldLeft(original.convertToScala) { (acc, m) =>
        acc + m
      }.convertToJava)
      config
    }

    def --(keys: Seq[String]): Parameters = {
      val original = Option(config.get(KEY.FIELD_ENCODING_MAP)).getOrElse(new java.util.HashMap[String, java.util.Map[String, Any]]()).asInstanceOf[java.util.Map[String, java.util.Map[String, Any]]]
      config.set(KEY.FIELD_ENCODING_MAP, (original.convertToScala -- keys).convertToJava)
      config
    }

    def clear: Parameters = {
      import scala.collection.JavaConverters._
      val original = Option(config.get(KEY.FIELD_ENCODING_MAP)).getOrElse(new java.util.HashMap[String, java.util.Map[String, Any]]()).asInstanceOf[java.util.Map[String, java.util.Map[String, Any]]]
      config -- original.keySet().asScala.toSeq
    }
  }

}
