package utility

import java.text.SimpleDateFormat

object Conversion {

  def getHourSlot(hour: Int): Int = {

      if (hour >= 0 && hour < 6) 1
      else if (hour >= 6 && hour < 12) 2
      else if (hour >= 12 && hour < 18) 3
      else if (hour >= 18 && hour < 24) 4

      else 0

  }
  def convertDateStringFormat(date: String): String = {

    val originalFormat = new SimpleDateFormat("dd/MMM/yyyy");
    val finalFormat = new SimpleDateFormat("yyyy-MM-dd");

    finalFormat.format(originalFormat.parse(date))
  }
  def extractMessage(message: String): Array[String] = {
    /*c-24-20-163-223.client.comcast.net - - [09/Mar/2004:13:16:00 -0800] "GET /twiki/bin/view/Main/DCCAndPostFix HTTP/1.1" 200 5253
    h194n2fls308o1033.telia.com - - [09/Mar/2004:13:49:05 -0800] "-" 408 -*/
    var version = "Unknown"
    var text = "Unknown"
    var requestType = "Unknown"

    var msgArray = message.split(" ")
    if(msgArray.size>=3) {
      var httpArray = msgArray(2).split("/")
      if(httpArray.size >=2){
        version = httpArray(1)
      }else{
        version = "Unknown"
      }
      text = msgArray(1)
      requestType = msgArray(0)
    }else if(msgArray.size==2){
      version = "Unknown"
      text = msgArray(1)
      requestType = msgArray(0)
    }else if(msgArray.size==1){
      version = "Unknown"
      text = "Unknown"
      requestType = msgArray(0)
    }else{
      version = "Unknown"
      text = "Unknown"
      requestType = "Unknown"
    }
   return Array(requestType,text,version)


  }


}
