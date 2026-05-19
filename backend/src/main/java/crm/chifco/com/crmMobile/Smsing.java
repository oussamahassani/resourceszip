package crm.chifco.com.crmMobile;

import java.util.List;

public class Smsing {
  private String sender;
  private String APIKEY;
  private List<Message> Messages;

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getAPIKEY() {
    return APIKEY;
  }

  public void setAPIKEY(String aPIKEY) {
    APIKEY = aPIKEY;
  }

  public List<Message> getMessages() {
    return Messages;
  }

  public void setMessages(List<Message> messages) {
    Messages = messages;
  }

  public Smsing() {
    super();
  }

}
