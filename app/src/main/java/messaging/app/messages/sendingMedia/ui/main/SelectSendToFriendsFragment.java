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
import messaging.app.messages.sendingMedia.SendMediaToFriendsListAdapter;

public class SelectSendToFriendsFragment extends Fragment implements
        SendMediaToFriendsListAdapter.onFriendsSelectRecyclerViewClickedUUIDListener {

    View mView;
    private RecyclerView mRecyclerView;
    private SendMediaToFriendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Context mContext;
    onSelectedRowListener mListener;

    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    public SelectSendToFriendsFragment(Context context, onSelectedRowListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public interface onSelectedRowListener {
        void onSelectedFriendsFragmentRowListener(String UUID, String messageType);
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
        mView = inflater.inflate(R.layout.fragment_select_friends, container, false);


        mRecyclerView = mView.findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        displayFriendsDetailsList(this);

        return mView;
    }


    private void displayFriendsDetailsList(final SelectSendToFriendsFragment selectSendToFriendsFragment) {
        mQueryingDatabase.getFriendsDetails(new QueryingDatabase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetailsList) {
                //display to user
                mAdapter = new SendMediaToFriendsListAdapter(friendsDetailsList,
                        mContext, selectSendToFriendsFragment);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onSelected(String UUID, String messageType) {

        mListener.onSelectedFriendsFragmentRowListener(UUID, messageType);
    }
}
