package io.glutenproject.integration.tpc

import java.sql.Date
import org.apache.spark.SparkConf
import org.apache.spark.sql.GlutenTypeUtils
import org.apache.spark.sql.types.{DateType, DecimalType, DoubleType, IntegerType, LongType, StringType}

object Constants {

  val VANILLA_CONF: SparkConf = new SparkConf()

  val VELOX_BACKEND_CONF: SparkConf = new SparkConf()
    .set("spark.gluten.sql.columnar.backend.lib", "velox")
    .set("spark.gluten.sql.columnar.forceshuffledhashjoin", "true")
    .set("spark.sql.parquet.enableVectorizedReader", "true")
    .set("spark.plugins", "io.glutenproject.GlutenPlugin")
    .set("spark.shuffle.manager", "org.apache.spark.shuffle.sort.ColumnarShuffleManager")
    .set("spark.sql.optimizer.runtime.bloomFilter.enabled", "true")
    .set("spark.sql.optimizer.runtime.bloomFilter.applicationSideScanSizeThreshold", "0")

  val GAZELLE_CPP_BACKEND_CONF: SparkConf = new SparkConf()
    .set("spark.gluten.sql.columnar.backend.lib", "gazelle_cpp")
    .set("spark.gluten.sql.columnar.forceshuffledhashjoin", "true")
    .set("spark.sql.parquet.enableVectorizedReader", "true")
    .set("spark.plugins", "io.glutenproject.GlutenPlugin")
    .set("spark.shuffle.manager", "org.apache.spark.shuffle.sort.ColumnarShuffleManager")

  val TYPE_MODIFIER_DATE_AS_DOUBLE: TypeModifier = new TypeModifier(
    GlutenTypeUtils.typeAccepts(_, DateType), DoubleType) {
    override def modValue(from: Any): Any = {
      from match {
        case v: Date => v.getTime.asInstanceOf[Double] / 86400.0D / 1000.0D
      }
    }
  }

  val TYPE_MODIFIER_INTEGER_AS_DOUBLE: TypeModifier = new TypeModifier(
    GlutenTypeUtils.typeAccepts(_, IntegerType), DoubleType) {
    override def modValue(from: Any): Any = {
      from match {
        case v: Int => v.asInstanceOf[Double]
      }
    }
  }

  val TYPE_MODIFIER_LONG_AS_DOUBLE: TypeModifier = new TypeModifier(
    GlutenTypeUtils.typeAccepts(_, LongType), DoubleType) {
    override def modValue(from: Any): Any = {
      from match {
        case v: Long => v.asInstanceOf[Double]
      }
    }
  }

  val TYPE_MODIFIER_DATE_AS_STRING: TypeModifier = new TypeModifier(
    GlutenTypeUtils.typeAccepts(_, DateType), StringType) {
    override def modValue(from: Any): Any = {
      from match {
        case v: Date => v.toString
      }
    }
  }

  val TYPE_MODIFIER_DECIMAL_AS_DOUBLE: TypeModifier = new TypeModifier(
    GlutenTypeUtils.decimalAccepts, DoubleType) {
    override def modValue(from: Any): Any = {
      from match {
        case v: java.math.BigDecimal => v.doubleValue()
      }
    }
  }
}
