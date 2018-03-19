package service.consumer

import kafka.serializer.StringDecoder
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.ConnectionFactory
import util.Bytes
import org.apache.hadoop.hbase.client._
import org.apache.spark.streaming.kafka.KafkaUtils
import utility.{Conversion, HbaseConfig}
import java.util.regex.Matcher

import scala.util.parsing.json._
import scala.collection.JavaConversions._
import com.datastax.spark.connector.streaming._
import java.util.regex.Pattern

import com.datastax.driver.core.utils.UUIDs
import com.datastax.spark.connector.SomeColumns
import org.apache.spark.streaming.dstream.DStream

object SparkConsumer {
  case class logclass(uuid: String,ip:String,logparameter1:String,logparameter2:String,date:String,time:String,
                      logparameter3:String,requesttype:String,
                      message:String,httpversion:String,code:Int,id:String, six_hour_slot:Int)

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("SparkConsumer").setMaster("local[*]")
      .set("spark.cassandra.connection.host", "localhost")
      .set("spark.cassandra.auth.username", "cassandra")
      .set("spark.cassandra.auth.password", "cassandra")
      .set("spark.cassandra.connection.keep_alive_ms","20000")
    val ssc = new StreamingContext(sparkConf, Seconds(20))

    val topics =Set("logTopic")
    val kafkaParams=Map("metadata.broker.list"-> "quickstart.cloudera:9092")
    val inputStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaParams,topics)

    var keyList=List[String]()

   //  lordgun.org - - [07/Mar/2004:17:01:53 -0800] "GET /razor.html HTTP/1.1" 200 2869
    val regexPattern: Pattern = Pattern.compile("(\\S+) (\\S+) (\\S+) (\\[.+?\\]) \"(.*?)\" (\\d{3}) (\\S+)")

    val message = inputStream.map(_._2).map(record=> {
      val parsedJSON: Option[Any] = JSON.parseFull(record)
      parsedJSON match {
        case parsedData: Some[Map[String, String]] =>
          val schemaTypes: String = parsedData.get("schema")
          keyList = schemaTypes.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\"", "").split(",").map(_.split(":")).map(arr => arr(0)).toList
          val recordTypes: String = parsedData.get("record")
          val parsedRecord = regexPattern.matcher(recordTypes)
          if (parsedRecord.matches())
            Array(parsedRecord.group(1),parsedRecord.group(2),parsedRecord.group(3), parsedRecord.group(4), parsedRecord.group(5), parsedRecord.group(6),parsedRecord.group(7))
          else
            Array("Regex and Record type mismatch")
      }
    })
    val processedRecords: DStream[logclass] = message.map(array =>{
      val time: Array[String] = array(3).split(":")
      val timestamp: String = time(1) + ":" + time(2) + ":" +time(3).split(" ")(0)
      val hourSlot: Int = Conversion.getHourSlot(time(1).toInt)
      val date: Array[String] = array(3).split(":")
      val messageType: Array[String] = Conversion.extractMessage(array(4))

      //  lordgun.org - - [07/Mar/2004:17:01:53 -0800] "GET /razor.html HTTP/1.1" 200 2869
      logclass(UUIDs.timeBased().toString(),array(0),array(1).toString,array(2).toString,
        Conversion.convertDateStringFormat(date(0).replace("[", "")),
        timestamp,
        date(0).replace("[",""),
        messageType(0),
        messageType(1),
        messageType(2),array(5).toInt,array(6),hourSlot)
    })
    processedRecords.saveToCassandra("apachelogs", "logtable1", SomeColumns("uuid", "ip","logparameter1","logparameter2", "date", "time", "logparameter3", "requesttype", "message", "httpversion", "code", "id","six_hour_slot"))
    ssc.start()
    ssc.awaitTermination()
  }
}