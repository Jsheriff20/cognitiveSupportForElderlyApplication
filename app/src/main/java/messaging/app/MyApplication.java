package messaging.app;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class MyApplication extends android.app.Application {
    //do not include the list of friends here, dynamically retrieve it every time its needed to ensure that up to date info is received


    private String UUID;
    private String username;
    private String Email;
    private String userImage;
    private String firstName;
    private String surname;


    public MyApplication() {
    }
}
