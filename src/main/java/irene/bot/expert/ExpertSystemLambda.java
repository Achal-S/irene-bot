package irene.bot.expert;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.expert.model.KnowledgeBase;
import irene.bot.lex.LexFullfillmentService;
import irene.bot.lex.model.*;
import irene.bot.util.ApplicationPropertiesUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExpertSystemLambda implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractEmbeddedClient.class);
    private final LexFullfillmentService lexFullfillmentService = new LexFullfillmentService();

    @Override
    public LexResponse handleRequest(LexEvent lexEvent, Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        LexResponse lexResponse = null;
        try {
            ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
            String knowledgeBaseString = IOUtils.toString(ApplicationPropertiesUtil.getFile("expertsystem.json", this.getClass()), StandardCharsets.UTF_8.name());
            KnowledgeBase knowledgeBase = this.parseKnowledgeBase(knowledgeBaseString);

            switch (confirmationStatus) {
                case NONE:
                    msg = "Mmmmmm...it seems you have a problem with the vehicle, may be I can help. I am gonna ask you some questions to diagnose the problem. Is this ok?";
                    lexResponse = lexFullfillmentService.lexConfirmIntent(msg, "", new Slots());
                    break;
                case CONFIRMED:
//                    lexResponse =
                    break;
                case DENIED:
//                    lexResponse = this.processConfirmationStatusDenied();
                    break;
                default:
                    msg = String.format("Sorry there has been a problem.");
//                    lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
            }


        } catch (IOException e) {
            log.error(e);
        }
        return lexResponse;
    }


    private LexResponse handleNoneConfirmationStatus(LexEvent lexEvent, KnowledgeBase knowledgeBase) {
        String msg = "Mmmmmm...it seems you have a problem with the vehicle, may be I can help. I am gonna ask you some questions to diagnose the problem.\n";
        msg += knowledgeBase.getPrimary().get(0).getQuestion();
        Slots slots = lexEvent.getCurrentIntent().getSlots();
        slots.setPrimaryQuestion(1);
        LexResponse lexResponse = lexFullfillmentService.lexConfirmIntent(msg, "ExpertSystem", slots);
        return lexResponse;
    }

    private LexResponse handleConfirmationStatusConfirmed(LexEvent lexEvent, KnowledgeBase knowledgeBase){
        Integer primary = lexEvent.getCurrentIntent().getSlots().getPrimaryQuestion();
        Integer secondary = lexEvent.getCurrentIntent().getSlots().getSecondaryQuestion();
        Slots slots = lexEvent.getCurrentIntent().getSlots();

        if(secondary==null){
            //go to secondary
            slots.setSecondaryQuestion(1);
            return lexFullfillmentService.lexConfirmIntent(knowledgeBase.getPrimary().get(primary).getSecondary().get(0).getQuestion(), "ExpertSystem", slots);
        }

        if(knowledgeBase.getPrimary().get(primary).getSecondary().size()>secondary){
            //increase secondary
            slots.setSecondaryQuestion(secondary+1);
            return lexFullfillmentService.lexConfirmIntent(knowledgeBase.getPrimary().get(primary).getSecondary().get(secondary).getQuestion(), "ExpertSystem", slots);
        }

//        if(knowledgeBase.getPrimary().size()>primary){
//            //increase primary
//            slots.setPrimaryQuestion(primary+1);
//            slots.setSecondaryQuestion(1);
//            return lexFullfillmentService.lexConfirmIntent(knowledgeBase.getPrimary().get(primary).getSecondary().get(0).getQuestion(), "ExpertSystem", slots);
//        }

        return lexFullfillmentService.lexCloseIntent("Boh..", FullfillmentState.FULFILLED);
    }






    private KnowledgeBase parseKnowledgeBase(String knowledgeBaseString) {
        Gson gson = new Gson();
        return gson.fromJson(knowledgeBaseString, KnowledgeBase.class);
    }
}
