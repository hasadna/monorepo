package hasadna.noloan2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.example.galgo.noloan.protobuf.UserProto.loguser;
import com.google.protobuf.InvalidProtocolBufferException;

import noloan.R;

public class UserAdapter extends FirestoreAdapter<UserAdapter.UserViewHolder> {

  public UserAdapter(Query query) {
    super(query);
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new UserViewHolder(inflater.inflate(R.layout.user_listitem, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    ((UserViewHolder) viewHolder).bind(getSnapshots().get(i));
  }

  class UserViewHolder extends RecyclerView.ViewHolder {

    TextView name;

    public UserViewHolder(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.TV_username);
    }

    public void bind(DocumentSnapshot snapshot) {
      FirestoreElement userElement = snapshot.toObject(FirestoreElement.class);
      byte[] userbyte = Base64.decode(userElement.getBase64(), Base64.DEFAULT);
      loguser user = null;
      try {
        user = loguser.newBuilder().build().getParserForType().parseFrom(userbyte);
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }

      android.content.res.Resources resources = itemView.getResources();

      name.setText(user.getName());
    }
  }
}

