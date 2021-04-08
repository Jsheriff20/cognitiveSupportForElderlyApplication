package messaging.app.contactingFirebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Timestamp;

public class ManagingGames {

    Context mContext;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    public ManagingGames(Context context) {
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }


    public void storeGameResult(String gameType, long score) {

        //store game data for analysis review
        DatabaseReference databaseRef = mDatabase.getReference("gamesDetails");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "//" + gameType + "//" +
                timestamp.getTime()).setValue(score);


        //Add game data to users past 5 scores
        databaseRef = mDatabase.getReference("userGamesDetails");
        databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "//" + gameType + "//" +
                timestamp.getTime()).setValue(score);

        //if there are more than 5 scores stored, remove any old ones
        Query query = databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "//" +
                gameType).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("test", "list of most recent 5 scores" + snapshot);

                if (snapshot.getChildrenCount() > 5) {
                    long numberOfExtraScores = snapshot.getChildrenCount() - 5;

                    long count = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (count >= numberOfExtraScores) {
                            break;
                        }
                        //remove the extra score
                        DatabaseReference extraScore = ds.getRef();
                        extraScore.removeValue();
                        count++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //check if score is a high score, if so update the users high score
        DatabaseReference highScoreDatabaseRef = mDatabase.getReference("userDetails");
        Query highScoreQuery = highScoreDatabaseRef.child(mQueryingDatabase.getCurrentUsersUUID() +
                "/highScores/" + gameType);
        highScoreQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if it is a reaction game or not.
                //reaction games a lower score is better (quicker reactions)
                if (gameType.equals("pairs") || gameType.equals("pattern")) {
                    if (snapshot.getValue() == null || (long) snapshot.getValue() < score) {
                        highScoreDatabaseRef.child(mQueryingDatabase.getCurrentUsersUUID() +
                                "/highScores/" + gameType).setValue(score);
                    }
                } else {
                    if (snapshot.getValue() == null || (long) snapshot.getValue() > score) {
                        highScoreDatabaseRef.child(mQueryingDatabase.getCurrentUsersUUID() +
                                "/highScores/" + gameType).setValue(score);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
