package hasadna.noloan2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter implements EventListener<QuerySnapshot> {
  private Query query;
  private ListenerRegistration registration;

  private ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();

  public FirestoreAdapter(Query query) {
    this.query = query;
  }

  public void startListening() {
    if (query != null && registration == null) {
      registration = query.addSnapshotListener(this);
    }
  }

  @Override
  public int getItemCount() {
    return snapshots.size();
  }

  @Override
  public void onEvent(
      @Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException e) {
    if (e != null) {
      Log.w("firestore adapter", e);
      return;
    }

    for (DocumentChange change : querySnapshots.getDocumentChanges()) {
      DocumentSnapshot snapshot = change.getDocument();

      switch (change.getType()) {
        case ADDED:
          added(change);
          break;
        case MODIFIED:
          modified(change);
          break;
        case REMOVED:
          removed(change);
          break;
      }
    }
  }

  private void added(DocumentChange change) {
    snapshots.add(change.getNewIndex(), change.getDocument());
    notifyItemInserted(change.getNewIndex());
  }

  private void modified(DocumentChange change) {
    if (change.getOldIndex() == change.getNewIndex()) {
      snapshots.set(change.getOldIndex(), change.getDocument());
      notifyItemChanged(change.getOldIndex());
    } else {
      snapshots.remove(change.getOldIndex());
      snapshots.add(change.getNewIndex(), change.getDocument());
      notifyItemMoved(change.getOldIndex(), change.getNewIndex());
    }
  }

  private void removed(DocumentChange change) {
    snapshots.remove(change.getOldIndex());
    notifyItemRemoved(change.getOldIndex());
  }

  public ArrayList<DocumentSnapshot> getSnapshots() {
    return snapshots;
  }
}

