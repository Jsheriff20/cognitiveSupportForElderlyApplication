package messaging.app;

import android.app.Application;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends android.app.Application {
    //do not include the list of friends here, dynamically retrieve it every time its needed to ensure that up to date info is received


    private List<friendsDetails> friendsDetailsList = new ArrayList<friendsDetails>();
    private List<String> selectedRecipientsList = new ArrayList<String>();


    public MyApplication() {
        this.friendsDetailsList = friendsDetailsList;
    }

    public List<friendsDetails> getFriendsDetailsList() {
        return friendsDetailsList;
    }

    public void setFriendsDetailsList(List<friendsDetails> friendsDetailsList) {
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
