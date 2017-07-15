package irene.bot.embedded;

import irene.bot.util.ApplicationPropertiesUtil;

public abstract class AbstractNotificationLambda extends AbstractEmbeddedClient {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractNotificationLambda.class);

    private static final String SKYPE_ID = "skype.id";
    private static final String SKYPE_NAME = "skype.name";
    private static final String SLACK_ID = "slack.id";
    private static final String SLACK_NAME = "slack.name";
    private static final String FACEBOOK_ID = "facebook.id";
    private static final String FACEBOOK_NAME = "facebook.name";

    private final String BOT_SKYPE_ID = ApplicationPropertiesUtil.getProperty(SKYPE_ID, this.getClass());
    private final String BOT_SKYPE_NAME = ApplicationPropertiesUtil.getProperty(SKYPE_NAME, this.getClass());
    private final String BOT_SLACK_ID = ApplicationPropertiesUtil.getProperty(SLACK_ID, this.getClass());
    private final String BOT_SLACK_NAME = ApplicationPropertiesUtil.getProperty(SLACK_NAME, this.getClass());
    private final String BOT_FACEBOOK_ID = ApplicationPropertiesUtil.getProperty(FACEBOOK_ID, this.getClass());
    private final String BOT_FACEBOOK_NAME = ApplicationPropertiesUtil.getProperty(FACEBOOK_NAME, this.getClass());


    protected String getUserNameFromChannel(String channelId) {
        String userName;
        log.info("Channel is: " + channelId);
        switch (channelId) {
            case "skype":
                userName = this.BOT_SKYPE_NAME;
                break;
            case "slack":
                userName = this.BOT_SLACK_NAME;
                break;
            case "facebook":
                userName = this.BOT_FACEBOOK_NAME;
                break;
            default:
                userName = this.BOT_SKYPE_NAME;
                break;
        }
        return userName;
    }

    protected String getIdFromChannel(String channelId) {
        String userName;
        switch (channelId) {
            case "skype":
                userName = this.BOT_SKYPE_ID;
                break;
            case "slack":
                userName = this.BOT_SLACK_ID;
                break;
            case "facebook":
                userName = this.BOT_FACEBOOK_ID;
                break;
            default:
                userName = this.BOT_SKYPE_ID;
                break;
        }
        return userName;
    }
}
