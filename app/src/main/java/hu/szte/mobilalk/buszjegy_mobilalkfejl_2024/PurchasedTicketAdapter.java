package hu.szte.mobilalk.buszjegy_mobilalkfejl_2024;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PurchasedTicketAdapter extends RecyclerView.Adapter<PurchasedTicketAdapter.ViewHolder>{

    private ArrayList<PurchasedTicket> ticketData;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public PurchasedTicketAdapter(Context mContext, ArrayList<PurchasedTicket> ticketData){
        this.mContext = mContext;
        this.ticketData = ticketData;
        this.mAuth = FirebaseAuth.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PurchasedTicketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_purchased_ticket, parent, false);

        return new PurchasedTicketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedTicketAdapter.ViewHolder holder, int position) {
        PurchasedTicket actualData = ticketData.get(position);

        holder.setData(actualData);
    }

    @Override
    public int getItemCount() {
        return ticketData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ticketPlaceName;
        private TextView ticketValidationLength;
        private TextView ticketDurationTimeText;
        private TextView ticketValid;
        private ImageButton deletePurchasedTicket;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ticketPlaceName = itemView.findViewById(R.id.ticketPlaceName);
            ticketValidationLength = itemView.findViewById(R.id.ticketValidationLengthText);
            ticketDurationTimeText = itemView.findViewById(R.id.ticketDurationTimeText);
            ticketValid = itemView.findViewById(R.id.ticketValid);
            deletePurchasedTicket = itemView.findViewById(R.id.deletePurchasedTicket);
        }

        public void setData(PurchasedTicket data){
            ticketPlaceName.setText(data.getCity());
            ticketDurationTimeText.setText(data.getType() + " jegy " + data.getCity());
            ticketValidationLength.setText("Érvényesség: " + data.getValidDate());
            ticketValid.setText("Érvényes");

            deletePurchasedTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(data._getDocumentID(), getAdapterPosition());
                }
            });
        }

        public void showDialog(String documentId, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.purchase_ticket_delete_message_title);
            builder.setMessage(R.string.purchase_ticket_delete_message);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mFirestore.collection("purchasedTickets").document(documentId).delete();
                    ticketData.remove(position);
                    notifyItemRemoved(position);
                }
            });
            builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
