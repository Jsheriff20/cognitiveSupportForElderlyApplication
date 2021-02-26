package messaging.app.messages.sendingMedia.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import messaging.app.ContactingFirebase;
import messaging.app.R;
import messaging.app.messages.sendingMedia.SendMediaToFriendsListAdapter;

public class SelectSendToFriendsFragment extends Fragment implements SendMediaToFriendsListAdapter.onFriendsSelectRecyclerViewClickedUUIDListener {

    View view;

    private RecyclerView recyclerView;
    private SendMediaToFriendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Context mContext;
    ContactingFirebase contactingFirebase = new ContactingFirebase(mContext);
    onSelectedRowListener listener;


    public interface onSelectedRowListener{
        void onSelectedFriendsFragmentRowListener(String UUID, String messageType);
    }


    public SelectSendToFriendsFragment(Context context, onSelectedRowListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_friends, container, false);


        recyclerView = view.findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        displayFriendsDetailsList(this);

        return view;
    }


    private void displayFriendsDetailsList(final SelectSendToFriendsFragment selectSendToFriendsFragment) {
        contactingFirebase.getFriendsDetails(new ContactingFirebase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetailsList) {
                //display to user
                mAdapter = new SendMediaToFriendsListAdapter(friendsDetailsList, mContext, selectSendToFriendsFragment);

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onSelected(String UUID, String messageType) {

        listener.onSelectedFriendsFragmentRowListener(UUID, messageType);
    }
}
