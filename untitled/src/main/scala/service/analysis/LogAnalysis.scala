import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}
object LogAnalysis {

  def main(args: Array[String]): Unit = {


   val sparkConf = new SparkConf().setAppName("SparkConsumer").setMaster("local[*]")
     .set("spark.cassandra.connection.host", "localhost")
     .set("spark.cassandra.auth.username", "cassandra")
     .set("spark.cassandra.auth.password", "cassandra")
     .set("spark.cassandra.connection.keep_alive_ms","20000")

    val sc = SparkContext.getOrCreate(sparkConf)
    val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)

    val df: DataFrame = hiveContext
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map( "table" -> "logtable1", "keyspace" -> "apachelogs"))
      .load()

    df.registerTempTable("temp")
    /* c.	Version of Http request(1.0 or 1.1) , codes (200, 304, 404 etc) along with the count (descending)*/
    val result1 = hiveContext.sql("SELECT httpversion, code, count(*) AS total FROM temp WHERE httpversion IN ('1.0','1.1') GROUP BY httpversion, code ORDER BY total DESC").coalesce(1).cache()
    result1.show()

    /*b.	Popular hits – Top 10 hits every 24 and 6 hours, group by IP-Address*/

    //for 6 hours
    val result2 = hiveContext.sql(
      """
        |   SELECT ip, date, six_hour_slot, message, count_hits, rank_hits
        |   FROM (
        |   SELECT ip, date, six_hour_slot, message, count_hits, DENSE_RANK() OVER (PARTITION BY date, six_hour_slot ORDER BY count_hits DESC) as rank_hits
        |     FROM (
        |       select ip, date, message, six_hour_slot, count(1) as count_hits
        |         from temp
        |        group by ip, date, six_hour_slot, message
        |        order by ip, date, six_hour_slot, message desc
        |     ) tmp
        |    )tmp1
        |    WHERE rank_hits <= 10
        |    ORDER BY date, six_hour_slot, count_hits DESC
      """.stripMargin)
    result2.show()

    //for 24 hours

    val result3 = hiveContext.sql(
      """
        |   SELECT ip, date, message, count_hits, rank_hits
        |   FROM (
        |   SELECT ip, date, message, count_hits, DENSE_RANK() OVER(PARTITION BY date ORDER BY count_hits DESC) as rank_hits
        |     FROM (
        |       select ip, date, message, count(1) as count_hits
        |         from temp
        |        group by ip, date, message
        |        order by ip, date, message desc
        |     ) tmp
        |    )tmp1
        |    WHERE rank_hits <= 10
        |    ORDER BY date, count_hits DESC
      """.stripMargin)
    result3.show()

    /*a.	Popular hits – Top 10 hits every 24 and 6 hours (excluding images)*/

  val filteredDF = df.filter(!df.col("message").endsWith(".jpg") || !df.col("message").endsWith(".jpeg"))
      filteredDF.registerTempTable("logtable")

    //for 6 hours
    val result4 = hiveContext.sql(
          """
            |   SELECT ip, date, six_hour_slot, message, count_hits, rank_hits
            |   FROM (
            |   SELECT ip, date, six_hour_slot, message, count_hits, DENSE_RANK() OVER (PARTITION BY date, six_hour_slot ORDER BY count_hits DESC) as rank_hits
            |     FROM (
            |       select ip, date, message, six_hour_slot, count(1) as count_hits
            |         from logtable
            |        group by ip, date, six_hour_slot, message
            |        order by ip, date, six_hour_slot, message desc
            |     ) tmp
            |    )tmp1
            |    WHERE rank_hits <= 10
            |    ORDER BY date, six_hour_slot, count_hits DESC
          """.stripMargin)
        result4.show()

    //for 24 hours

    val result5 = hiveContext.sql(
      """
        |   SELECT ip, date, message, count_hits, rank_hits
        |   FROM (
        |   SELECT ip, date, message, count_hits, DENSE_RANK() OVER(PARTITION BY date ORDER BY count_hits DESC) as rank_hits
        |     FROM (
        |       select ip, date, message, count(1) as count_hits
        |         from logtable
        |        group by ip, date, message
        |        order by ip, date, message desc
        |     ) tmp
        |    )tmp1
        |    WHERE rank_hits <= 10
        |    ORDER BY date, count_hits DESC
      """.stripMargin)
    result5.show()


  }
}