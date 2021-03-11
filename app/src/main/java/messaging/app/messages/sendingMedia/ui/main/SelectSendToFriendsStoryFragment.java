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

import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.sendingMedia.SendMediaToFriendsStoryListAdapter;

public class SelectSendToFriendsStoryFragment extends Fragment implements SendMediaToFriendsStoryListAdapter.onFriendsStorySelectRecyclerViewClickedUUIDListener{

    View view;

    private RecyclerView recyclerView;
    private SendMediaToFriendsStoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Context mContext;
    onSelectedRowListener listener;
    QueryingDatabase queryingDatabase = new QueryingDatabase();

    public interface onSelectedRowListener{
        void onSelectedStoryFragmentRowListener(String UUID, String messageType);
    }


    public SelectSendToFriendsStoryFragment(Context context, onSelectedRowListener listener) {
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
        view = inflater.inflate(R.layout.fragment_select_friends_story, container, false);


        recyclerView = view.findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        displayFriendsDetailsList(this);

        return view;
    }


    private void displayFriendsDetailsList(final SelectSendToFriendsStoryFragment selectSendToFriendsStoryFragment) {
        queryingDatabase.getFriendsDetails(new QueryingDatabase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetailsList) {
                //display to user
                mAdapter = new SendMediaToFriendsStoryListAdapter(friendsDetailsList, mContext, selectSendToFriendsStoryFragment);

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onSelected(String UUID, String messageType) {
        listener.onSelectedStoryFragmentRowListener(UUID, messageType);
    }
}
