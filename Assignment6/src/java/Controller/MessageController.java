/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import Entities.Message;

/**
 *
 * @author bellahuang
 */
@ApplicationScoped
public class MessageController {
    private List<Message> messages = new ArrayList<>();
    DateFormat df = new SimpleDateFormat("EEE MMM dd H:m:s zzz yyyy");
    
    
    /**
     * initialize the messages list
     */
    public MessageController(){
        try {
            messages.add(new Message(1, "title1", "contents1", "author1", df.parse("Fri Mar 17 15:50:07 EDT 2017")));
            messages.add(new Message(2, "title2", "contents2", "author2", df.parse("Sat Mar 18 15:50:07 EDT 2017")));
            messages.add(new Message(3, "title3", "contents3", "author3", df.parse("Sun Mar 19 15:50:07 EDT 2017")));
        } catch (ParseException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }
    
    /**
     * get a Message object by a specific id
     * @param id
     * @return Message
     */
    public Message getMessageById(int id){
        for (Message m : messages){
            if (m.getId() == id)
                return m;
        }
        return null;
    }
    
    /**
     * delete the Message specified by id
     * @param id
     * @return "200 OK" when it is deleted successfully
     */
    public String deleteMessageById(int id){
        for (Message m : messages){
            if(m.getId() == id){
                messages.remove(m);
                return "200 OK";
            }
        }
        return "Delete Failed";
    }
    
    /**
     * update the Message specified by id
     * @param id
     * @param j
     * @return the updated Message
     */
    public Message updateMessageById(int id, JsonObject j){
        String senttime = "";
        for (Message m : messages){
            if(m.getId() == id){
                try {
                    // assign new values to the specific Message
                    m.setTitle(j.getString("title"));
                    m.setContents(j.getString("contents"));
                    m.setAuthor(j.getString("author"));
                    senttime = j.getString("senttime");
                    m.setSenttime(df.parse(senttime));
                    return m;
                } catch (ParseException ex) {
                    Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
    
    /**
     * add a new Message into the Message List
     * @param j
     * @return the Message List of all Message
     */
    public List<Message> addMessage(JsonObject j){
        String senttime = "";
        try {
            // set the id of the new Message
            int id = messages.size() + 1;
            // get the new values from JSON
            String title = j.getString("title");
            String contents  = j.getString("contents");
            String author = j.getString("author");
            // if senttime is updated by user, then set the new senttime to the Message
            if (j.containsKey("senttime")){
                senttime = j.getString("senttime");
                messages.add(new Message(id, title, contents, author, df.parse(senttime)));
            }
            else {
                // if senttime is not included in JSON, then set the current Datetime as the senttime
                messages.add(new Message(id, title, contents, author, new Date()));
            }
        } catch (ParseException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return messages;
    }
    
    /**
     * get a List of Message in range of a specific period
     * @param startDate
     * @param endDate
     * @return a List of Message
     */
    public List<Message> getMessagesByDate(String startDate, String endDate){
        // declare a new List to store Message in a specific period
        List<Message> messagesByDate = new ArrayList<>();
        // loop through each Message in the Message List to compare with the specific date
        for (Message m : messages){
            try {
                // add the Message to the new List if it matched the specific date period
                if (m.getSenttime().after(df.parse(startDate)) 
                        && m.getSenttime().before(df.parse(endDate))
                        || m.getSenttime().equals(df.parse(startDate))
                        || m.getSenttime().equals(df.parse(endDate))){
                    messagesByDate.add(m);
                }
            } catch (ParseException ex) {
                Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return messagesByDate;
    }

}
