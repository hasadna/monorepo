package hasadna.noloan2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hasadna.noloan2.protobuf.SmsProto.SMSmessage;
import noloan.R;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsHolder> {
  
  ArrayList<SMSmessage> smsList;
  
  public SmsAdapter(ArrayList<SMSmessage> smsList) {
    this.smsList = smsList;
  }
  
  @NonNull
  @Override
  public SmsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new SmsHolder(inflater.inflate(R.layout.sms_listitem, viewGroup, false));
  }
  
  @Override
  public void onBindViewHolder(@NonNull SmsHolder smsHolder, int i) {
    smsHolder.address.setText(smsList.get(i).getPhonenumber());
    smsHolder.date.setText(smsList.get(i).getDate());
  }
  
  @Override
  public int getItemCount() {
    return smsList.size();
  }
  
  class SmsHolder extends RecyclerView.ViewHolder
  {
    TextView address;
    TextView date;
    
    
    public SmsHolder(@NonNull View itemView) {
      super(itemView);
      address = itemView.findViewById(R.id.TV_smsaddress);
      date = itemView.findViewById(R.id.TV_smsdate);
    }
  }
}
