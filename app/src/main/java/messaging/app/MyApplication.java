package messaging.app;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends android.app.Application {
    //do not include the list of friends here, dynamically retrieve it every time its needed to ensure that up to date info is received


    private List<FriendsDetails> friendsDetailsList = new ArrayList<FriendsDetails>();
    private List<String> selectedRecipientsList = new ArrayList<String>();


    public MyApplication() {
        this.friendsDetailsList = friendsDetailsList;
    }

    public List<FriendsDetails> getFriendsDetailsList() {
        return friendsDetailsList;
    }

    public void setFriendsDetailsList(List<FriendsDetails> friendsDetailsList) {
        this.friendsDetailsList = friendsDetailsList;
    }


    public void addToSelectedRecipientsList(String recipient){
        selectedRecipientsList.add(recipient);
    }

    public List<String> getSelectedRecipientsList() {
        return selectedRecipientsList;
    }

    public void setSelectedRecipientsList(List<String> selectedRecipientsList) {
        this.selectedRecipientsList = selectedRecipientsList;
    }


}
