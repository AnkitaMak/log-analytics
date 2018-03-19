package service.streaming
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.streaming.dstream.DStream
import utility.KafkaProperties
import com.google.gson.Gson
import utility.FileSchema
import utility.DirectoryLocation

object FileStream {
  case class LogRecord(record:String,schema:String)
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("FileStream").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    val FILE_SCHEMA = FileSchema.schema()

    val lines: DStream[String] = ssc.textFileStream(DirectoryLocation.location())

    val jsonRecords = lines.map(line=> new Gson().toJson(LogRecord(line,FILE_SCHEMA)))

    jsonRecords.print()
   // val producer = new KafkaProducer[String, DStream[String]](KafkaProperties.properties())

    try{
      jsonRecords.foreachRDD(rdd=>{
        rdd.foreachPartition(partition=>{
          val producer = new KafkaProducer[String, String](KafkaProperties.properties())
          partition.foreach(record=>{
            val data= record
            println(data)
            val message = new ProducerRecord[String,String]("logTopic",data)
            producer.send(message)
            println("message sent")
          })
          producer.close()
        })
      })
   }catch {
      case ex: Exception =>
        ex.printStackTrace()
    }

    ssc.start()
    ssc.awaitTermination()

  }

}
