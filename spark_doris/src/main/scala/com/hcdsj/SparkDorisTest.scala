package com.hcdsj

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * 使用doris-spark-1.0.0-spark-3.1.2_2.12.jar通过spark实时读取hudi数据写入doris
 */
object SparkDorisTest {

  def main(args: Array[String]): Unit = {

    val sc = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(sc).getOrCreate()

    val frame = spark.readStream.format("hudi").load("/tmp/hudi/my_hudi_table")

    frame.writeStream.format("doris").option("doris.table.identifier", "ds.test1").option("doris.fenodes", "172.31.41.20:8030").option("user", "root").option("password","").option("checkpointLocation", "/tmp/zhongtai/sq/t1_check").option("mode","Append").start().awaitTermination()

    spark.stop()

  }
}
