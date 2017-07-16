package irene.bot.expert;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.maps.model.PlacesSearchResult;
import irene.bot.embedded.AbstractEmbeddedClient;
import irene.bot.embedded.sensing.model.Position;
import irene.bot.expert.model.KnowledgeBase;
import irene.bot.expert.model.Question;
import irene.bot.expert.model.Secondary;
import irene.bot.lex.model.*;
import irene.bot.map.GeocodingService;
import irene.bot.util.ApplicationPropertiesUtil;
import irene.bot.util.MessageUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExpertSystemLambda extends AbstractEmbeddedClient implements RequestHandler<LexEvent, LexResponse> {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ExpertSystemLambda.class);
    private static final String POSITION_PATH = "position";
    private static final String EXPERTSYSTEM_JSON_FILE = "expertsystem.json";
    private static final String EXPERT_SYSTEM_INTENT = "ExpertSystem";
    private static final String MECHANIC_QUERY = "meccanico+moto";
    private GeocodingService geocodingService = new GeocodingService();

    @Override
    public LexResponse handleRequest(LexEvent lexEvent, Context context) {
        log.info(String.format("Intent %s triggered with confirmation %s", lexEvent.getCurrentIntent().getName(), lexEvent.getCurrentIntent().getConfirmationStatus()));
        String msg;
        LexResponse lexResponse = null;
        try {
            ConfirmationStatus confirmationStatus = ConfirmationStatus.fromString(lexEvent.getCurrentIntent().getConfirmationStatus());
            String knowledgeBaseString = IOUtils.toString(ApplicationPropertiesUtil.getFile(EXPERTSYSTEM_JSON_FILE, this.getClass()), StandardCharsets.UTF_8.name());
            KnowledgeBase knowledgeBase = this.parseKnowledgeBase(knowledgeBaseString);

            switch (confirmationStatus) {
                case NONE:
                    lexResponse = handleNoneConfirmationStatus(lexEvent, knowledgeBase);
                    break;
                case CONFIRMED:
                    lexResponse = handleConfirmationStatusConfirmed(lexEvent, knowledgeBase);
                    break;
                case DENIED:
                    lexResponse = handleConfirmationStatusDenied(lexEvent, knowledgeBase);
                    break;
                default:
                    msg = String.format("Sorry there has been a problem.");
                    lexResponse = lexFullfillmentService.lexCloseIntent(msg, FullfillmentState.FAILED);
                    break;
            }


        } catch (IOException e) {
            log.error(e);
        }
        return lexResponse;
    }

    private void pupulateSlotsFromQuestion(final Slots slots, final Question question) {
        if (question.isPrimary()) {
            slots.setPrimaryQuestion(Integer.parseInt(question.getId()));
            slots.setSecondaryQuestion(-1);
        } else {
            String[] ids = question.getId().split("\\.");
            slots.setPrimaryQuestion(Integer.parseInt(ids[0]));
            slots.setSecondaryQuestion(Integer.parseInt(ids[1]));
        }
    }

    private LexResponse handleNoneConfirmationStatus(LexEvent lexEvent, KnowledgeBase knowledgeBase) {
        log.info("Processing None confirmation status of Expert system");
        String msg = "It seems you have a problem with the vehicle, may be I can help. Let me ask you some questions to diagnose the problem.\n";
        Slots slots = lexEvent.getCurrentIntent().getSlots();
        LexResponse lexResponse;
        try {
            Question question = knowledgeBase.getNextQuestion(-1, -1, true);
            pupulateSlotsFromQuestion(slots, question);
            lexResponse = lexFullfillmentService.lexConfirmIntent(msg + question.getQuestion(), EXPERT_SYSTEM_INTENT, slots);
        } catch (Exception e) {
            log.error(e);
            lexResponse = lexFullfillmentService.lexCloseIntent("Ouch, an error occurred. " + MessageUtil.getErrorEmoji(), FullfillmentState.FAILED);
        }
        return lexResponse;
    }


    private String buildOutcomesReply(final Secondary secondary) {
        final StringBuilder stringBuilder = new StringBuilder("Ok, I think I have some hints now....\nBased on my knowledge the possible problem ranked by likelyhood are:\n");
        secondary.getOutcomes().stream().forEach(o -> stringBuilder.append(o + "\n"));
        return stringBuilder.toString();
    }

    private Question getCurrentQuestion(final Slots slots, final KnowledgeBase knowledgeBase) {
        final Question currentQuestion;
        Integer primaryIndex = slots.getPrimaryQuestion();
        Integer secondaryIndex = slots.getSecondaryQuestion();
        if (secondaryIndex < 0) {
            currentQuestion = knowledgeBase.getPrimary().get(primaryIndex);
        } else {
            currentQuestion = knowledgeBase.getPrimary().get(primaryIndex).getSecondary().get(secondaryIndex);
        }
        return currentQuestion;
    }

    private LexResponse handleConfirmationStatusConfirmed(LexEvent lexEvent, KnowledgeBase knowledgeBase) {
        log.info("Processing Confirmed status of Expert system");
        Slots slots = lexEvent.getCurrentIntent().getSlots();
        Integer primaryIndex = slots.getPrimaryQuestion();
        Integer secondaryIndex = slots.getSecondaryQuestion();
        LexResponse lexResponse;
        Question currentQuestion = this.getCurrentQuestion(slots, knowledgeBase);


        if (!currentQuestion.isPrimary() && currentQuestion.isConfirmation()) {
            lexResponse = lexFullfillmentService.lexCloseIntent(buildOutcomesReply(((Secondary) currentQuestion)), FullfillmentState.FULFILLED);
        } else {
            try {
                Question question;
                if (currentQuestion.isPrimary()) {
                    question = knowledgeBase.getNextQuestion(primaryIndex, secondaryIndex, !currentQuestion.isConfirmation());
                } else {
                    question = knowledgeBase.getNextQuestion(primaryIndex, secondaryIndex, false);
                }

                pupulateSlotsFromQuestion(slots, question);
                lexResponse = lexFullfillmentService.lexConfirmIntent(question.getQuestion(), EXPERT_SYSTEM_INTENT, slots);
            } catch (Exception e) {
                log.error(e);
                try {
                    String positionJSON = this.embeddedEndpointGET(POSITION_PATH);
                    final Position position = parseResponse(positionJSON, Position.class);
                    PlacesSearchResult placesSearchResult = this.geocodingService.getPlace(MECHANIC_QUERY, position.getLatitude(), position.getLongitude()).results[0];
                    String mapUrl = geocodingService.getMapURL(placesSearchResult.geometry.location.lat, placesSearchResult.geometry.location.lng);
                    lexResponse = lexFullfillmentService.lexCloseIntent("Sorry, This time it seems I cannot help you. The closest mechanic is:  " + placesSearchResult.name + " Check it out on map: " + mapUrl, FullfillmentState.FAILED);

                } catch (Exception e1) {
                    log.error(e1);
                    lexResponse = lexFullfillmentService.lexCloseIntent("Sorry, This time it seems I cannot help you. " + MessageUtil.getErrorEmoji(), FullfillmentState.FAILED);
                }
            }
        }
        return lexResponse;
    }

    private LexResponse handleConfirmationStatusDenied(LexEvent lexEvent, KnowledgeBase knowledgeBase) {
        log.info("Processing Confirmed status of Expert system");
        Slots slots = lexEvent.getCurrentIntent().getSlots();
        Integer primaryIndex = slots.getPrimaryQuestion();
        Integer secondaryIndex = slots.getSecondaryQuestion();
        LexResponse lexResponse;
        Question currentQuestion = this.getCurrentQuestion(slots, knowledgeBase);

        if (!currentQuestion.isPrimary() && !currentQuestion.isConfirmation()) {
            lexResponse = lexFullfillmentService.lexCloseIntent(buildOutcomesReply(((Secondary) currentQuestion)), FullfillmentState.FULFILLED);
        } else {

            try {
                Question question;
                if (currentQuestion.isPrimary()) {
                    question = knowledgeBase.getNextQuestion(primaryIndex, secondaryIndex, currentQuestion.isConfirmation());
                } else {
                    question = knowledgeBase.getNextQuestion(primaryIndex, secondaryIndex, false);
                }
                pupulateSlotsFromQuestion(slots, question);
                lexResponse = lexFullfillmentService.lexConfirmIntent(question.getQuestion(), EXPERT_SYSTEM_INTENT, slots);
            } catch (Exception e) {
                log.error(e);
                try {
                    String positionJSON = this.embeddedEndpointGET(POSITION_PATH);
                    final Position position = parseResponse(positionJSON, Position.class);
                    PlacesSearchResult placesSearchResult = this.geocodingService.getPlace(MECHANIC_QUERY, position.getLatitude(), position.getLongitude()).results[0];
                    String mapUrl = geocodingService.getMapURL(placesSearchResult.geometry.location.lat, placesSearchResult.geometry.location.lng);
                    lexResponse = lexFullfillmentService.lexCloseIntent("Sorry, I cannot help you. The closest mechanic to you is:  \"" + placesSearchResult.name + "\" Check it out on map: " + mapUrl, FullfillmentState.FAILED);

                } catch (Exception e1) {
                    log.error(e);
                    lexResponse = lexFullfillmentService.lexCloseIntent("Error", FullfillmentState.FAILED);
                }
            }
        }
        return lexResponse;
    }


    private KnowledgeBase parseKnowledgeBase(String knowledgeBaseString) {
        Gson gson = new Gson();
        return gson.fromJson(knowledgeBaseString, KnowledgeBase.class);
    }
}
