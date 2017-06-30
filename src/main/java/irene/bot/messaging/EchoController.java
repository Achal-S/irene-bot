package irene.bot.messaging;


import io.swagger.client.ApiException;
import io.swagger.client.model.Activity;

public class EchoController {


    private MessageProcessorComponent messageProcessorComponent;


//    @RequestMapping(value = "/messages", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
//    @ResponseBody
//    public ResponseEntity processMessage( String requestString) throws ApiException {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
//        Activity activity = gsonBuilder.create().fromJson(requestString, Activity.class);
//        if (activity.getType().equals("message")) {
//            logger.info("Received message:\n" + requestString);
//            messageProcessorComponent.enqueueMessage(activity);
//        } else {
//            logger.warn("I don't understand messages:\n" + requestString);
//        }
//        return ResponseEntity.ok().build();
//    }
}
