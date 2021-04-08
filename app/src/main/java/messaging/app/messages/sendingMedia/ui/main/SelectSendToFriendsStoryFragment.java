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

public class SelectSendToFriendsStoryFragment extends Fragment implements
        SendMediaToFriendsStoryListAdapter.onFriendsStorySelectRecyclerViewClickedUUIDListener {

    View mView;

    private RecyclerView mRecyclerView;
    private SendMediaToFriendsStoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Context mContext;
    onSelectedRowListener mListener;

    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    public interface onSelectedRowListener {
        void onSelectedStoryFragmentRowListener(String UUID, String messageType);
    }


    public SelectSendToFriendsStoryFragment(Context context, onSelectedRowListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_select_friends_story, container, false);


        mRecyclerView = mView.findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        displayFriendsDetailsList(this);

        return mView;
    }


    private void displayFriendsDetailsList(final SelectSendToFriendsStoryFragment selectSendToFriendsStoryFragment) {
        mQueryingDatabase.getFriendsDetails(new QueryingDatabase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetailsList) {
                //display to user
                mAdapter = new SendMediaToFriendsStoryListAdapter(friendsDetailsList,
                        mContext, selectSendToFriendsStoryFragment);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onSelected(String UUID, String messageType) {
        mListener.onSelectedStoryFragmentRowListener(UUID, messageType);
    }
}
