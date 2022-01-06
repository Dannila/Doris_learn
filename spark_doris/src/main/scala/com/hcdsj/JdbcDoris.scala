package com.hcdsj

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 使用jdbc的连接方式读取MySQL数据并且写入doris
 */
object JdbcDoris {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val spark = SparkSession.builder().config(conf).getOrCreate()
    val df: DataFrame = spark.read.format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://172.16.2.120:3306")
      .option("dbtable", "db_issue_clear.tb_nclear_dispute_setting")
      .option("user", "root")
      .option("password", "123456")
      .load()

    df.write.format("jdbc").option("url","jdbc:mysql://172.31.41.20:9030/ds").option("user","root").option("dbtable","ods_etc_project_etcobubusiness").save()

    spark.stop()

  }
}
