package com.hcdsj

import java.sql.DriverManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
/**
 * 动态获取数据的dataframe中的Schame信息，通过jdbc的方式在doris中建表，则无需手动在doris中提前建表
 */
object SparkDorisTestTwo {
  def main(args: Array[String]): Unit = {
    val USER_DATABASES = "ds"
    val USER_TABLE = "test1"

    val sc = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(sc).getOrCreate()

    Class.forName("com.mysql.jdbc.Driver")
    val connection = DriverManager.getConnection("jdbc:mysql://172.31.41.20:9030/ds", "root", "")

    val frame = spark.readStream.format("hudi").load("/tmp/hudi/my_hudi_table")

    /**
     * 1.提取数据
     */
    val (fieldName,fieldType,pl) = frame.schema.fields.foldLeft("", "", "")(
      (z, f) => {
        if (z._1.nonEmpty && z._2.nonEmpty && z._3.nonEmpty) {
          //非空即表示不是第一次的时候进行拼接
          (z._1 + "," + f.name, z._2 + "," + f.name + " " + f.dataType.simpleString, z._3 + ",?")
        } else {
          (f.name, f.name + " " + f.dataType.simpleString, "?")
        }
      }
    )

    /**
     * 2.将spark的表达式转换为clickhouse的表达式
     */
    val chCol: String = dfTypeName2CH(fieldType)

    /**
     * 3.建库
     */
    val createDatabaseSql =
      s"create database if not exists ${USER_DATABASES}";

    /**
     * 4.在doris中建表
     */
    val chTableSql =
      s"""
         |create table if not exists ${USER_DATABASES}.${USER_TABLE}(${chCol})
         |ENGINE=olap
         |DISTRIBUTED BY HASH(id) BUCKETS 10
         |PROPERTIES("replication_num" = "1")
         |""".stripMargin

    val i = connection.createStatement().executeUpdate(createDatabaseSql)
    val y = connection.createStatement().executeUpdate(chTableSql)
    if(y != -1){
      frame.writeStream.format("doris").option("doris.table.identifier", "${USER_DATABASES}.${USER_TABLE}").option("doris.fenodes", "172.31.41.20:8030").option("user", "root").option("password","").option("checkpointLocation", "/tmp/zhongtai/sq/t1_check").option("mode","Append").start().awaitTermination()

    }
    spark.stop()

  }

  /**
   * df
   * uid string gender string,...
   * ch
   * uid String gender String,...
   */
  def dfTypeName2CH(dfCol:String) = {
    dfCol.split(",").map(line => {
      val fields: Array[String] = line.split(" ")
      val fType: String = dfType2CHType(fields(1))
      val fName: String = fields(0)
      fName + " " + fType
    }).mkString(",")
  }

  /**
   * 将df的type转为ch的type
   */
  def dfType2CHType(fieldType: String):String = {
    fieldType.toLowerCase() match {
      case "string" => "VARCHAR(255)"
      case "integer" => "INT"
      case "long" => "BIGINT"
      case "float" => "FLOAT"
      case "double" => "DOUBLE"
      case "date" => "VARCHAR(255)"
      case "timestamp" => "VARCHAR(255)"
      case _ => "VARCHAR(255)"
    }
  }

}
