package com.hcdsj

import org.apache.spark.{SparkConf}
import org.apache.spark.sql.SparkSession

/**
 * 使用Spark Doris Connector编译的doris-spark-1.0.0-spark-3.1.2_2.12.jar 读取 hudi数据写入doris
 *
 */
object DorisTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val frame = spark.read.format("hudi").load("hdfs://localhost:9000/hudi/rdbms/default/db_issue_clear/ods_db_issue_clear_tb_nclear_inside_record_dispute/clean_tb")

    frame.write.format("doris").option("doris.fenodes", "localhost:8030").option("doris.table.identifier", "ds.test1").option("user", "root").option("password","").option("mode","Append").save()

    spark.stop()
  }
}
