package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      .process(new MaxFinder())
      .toD("twitter-search:${body}")
      .log("Body now contains the response from twitter:\n${body}");
  }


  public class MaxFinder implements Processor{
    public void process(Exchange exchange) throws Exception{
      //Get message
      Message rawMsg = exchange.getIn();
      String msg = rawMsg.getBody(String.class);
      //Look for the max: expression
      if(msg.matches("^[a-zA-Z0-9]+ max:[0-9]+$")){
        String split[] = msg.split(":");
        msg = msg.replace("max:"+Integer.parseInt(split[1]),"");
        msg = msg + " ?count=" + Integer.parseInt(split[1]);
      }
      //Default of 5 retrieved tweets in case of not defining the max parameter
      else{
        msg = msg + " ?count=5";
      }
      exchange.getOut().setBody(msg);
    }
  }


}
