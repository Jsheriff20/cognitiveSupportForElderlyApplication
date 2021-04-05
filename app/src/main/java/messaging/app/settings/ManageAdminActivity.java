package messaging.app.settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingFriends;
import messaging.app.contactingFirebase.QueryingDatabase;

public class ManageAdminActivity extends AppCompatActivity {

    Spinner spnPossibleAdminFriends;
    private RecyclerView lstCurrentAdminFriends;
    Button btnAddAdminFriend;

    QueryingDatabase queryingDatabase = new QueryingDatabase(null);
    ManagingFriends managingFriends = new ManagingFriends(this, null);

    HashMap<String, String> mFriends;
    HashMap<String, String> mAdminFriends;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String friendsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin);

        lstCurrentAdminFriends = findViewById(R.id.lstCurrentAdminFriends);
        spnPossibleAdminFriends = findViewById(R.id.spnPossibleAdminFriends);
        btnAddAdminFriend = findViewById(R.id.btnAddAdminFriend);


        setupSpinner();
        setupRecyclerView();
        setBtnAddAdminFriendOnClick();
    }


    private void setupSpinner(){

        queryingDatabase.getFriendsAndAdminFriendsNames(new QueryingDatabase.OnGetFriendsNamesListener() {
            @Override
            public void onSuccess(HashMap<String, String> friends, HashMap<String, String> adminFriends) {
                mFriends = friends;
                mAdminFriends = adminFriends;


                List<String> friendsNames = new ArrayList<>();
                for(String friend : mFriends.values()){
                    friendsNames.add(friend);
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManageAdminActivity.this, R.layout.spinner_item, friendsNames);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spnPossibleAdminFriends.setAdapter(arrayAdapter);
                spnPossibleAdminFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        friendsName = parent.getItemAtPosition(position).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView <?> parent) {
                    }
                });
            }
        });
    }


    private void setupRecyclerView(){

        //display to user
        queryingDatabase.getFriendsAndAdminFriendsNames(new QueryingDatabase.OnGetFriendsNamesListener() {
            @Override
            public void onSuccess(HashMap<String, String> friends, HashMap<String, String> adminFriends) {

                mLayoutManager = new LinearLayoutManager(ManageAdminActivity.this);
                mAdapter = new AdminFriendsAdapter(adminFriends, ManageAdminActivity.this);

                lstCurrentAdminFriends.setLayoutManager(mLayoutManager);
                lstCurrentAdminFriends.setHasFixedSize(true);
                lstCurrentAdminFriends.setAdapter(mAdapter);
            }
        });
    }


    private void setBtnAddAdminFriendOnClick(){
        btnAddAdminFriend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                queryingDatabase.getFriendsAndAdminFriendsNames(new QueryingDatabase.OnGetFriendsNamesListener() {
                    @Override
                    public void onSuccess(HashMap<String, String> friends, HashMap<String, String> adminFriends) {

                        //get the UUID of the users name
                        Set<String> UUIDs = getKeyByValue(friends, friendsName);
                        String UUID = UUIDs.iterator().next();

                        //add friend and refresh
                        managingFriends.addFriendAsAdmin(UUID, new ManagingFriends.OnAdminFriendAddedListener() {
                            @Override
                            public void onSuccess(boolean adminFriendAdded) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T, E> Set<T> getKeyByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}