package com.example.mikeygresl.template;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contactItemList;
    private View.OnClickListener onContactClickListtener;

    public ContactsAdapter(List<Contact> contactItemList) {

        this.contactItemList = contactItemList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ContactViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setOnClickListener(onContactClickListtener);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ContactViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i) {
        contactViewHolder.fullnameTextView.setText(contactItemList.get(i).getFname() + " " + contactItemList.get(i).getLname());
        contactViewHolder.emailTextView.setText(contactItemList.get(i).getEmail());
    }

    @Override
    public int getItemCount() {
        return contactItemList.size();
    }

    public void setOnContactClickListener(View.OnClickListener listener) {

        onContactClickListtener = listener;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView fullnameTextView;
        private TextView emailTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            fullnameTextView = (TextView)itemView.findViewById(R.id.fullnameTextView);
            emailTextView = (TextView)itemView.findViewById(R.id.emailTextView);
        }
    }
}
