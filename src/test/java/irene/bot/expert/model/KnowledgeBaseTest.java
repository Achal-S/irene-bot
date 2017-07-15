package irene.bot.expert.model;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnowledgeBaseTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

//    @Test
//    public void getNextQuestion() throws Exception {
//        String input = IOUtils.toString(
//                this.getClass().getClassLoader().getResourceAsStream("expertsystem.json"),
//                "UTF-8");
//        Gson gson = new Gson();
//        KnowledgeBase knowledgeBase = gson.fromJson(input,KnowledgeBase.class);
//
//        System.out.println(knowledgeBase.getNextQuestion(0,-1).getQuestion());
//        System.out.println(knowledgeBase.getNextQuestion(0,-1).isPrimary());
//        String[] ids = knowledgeBase.getNextQuestion(0,-1).getId().split("\\.");
//        System.out.println(ids[1]);
//    }

}