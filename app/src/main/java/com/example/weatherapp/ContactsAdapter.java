package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contact> implements Filterable {
    List<Contact> contactList;
    List<Contact> originalList;
    private CountryFilter filter;
    public ContactsAdapter(Context context, ArrayList<Contact> contactList) {
        super(context, 0, contactList);
        this.contactList = new ArrayList<Contact>();
        this.contactList.addAll(contactList);
        this.originalList = new ArrayList<Contact>();
        this.originalList.addAll(contactList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_listitem_layout, parent, false);
        }
        // Lookup view for data population
        TextView contactName = (TextView) convertView.findViewById(R.id.list_name);
        // Populate the data into the template view using the data object
        contactName.setText(contact.contact_name);
        // Return the completed view to render on screen
        return convertView;
    }
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }

        class CountryFilter extends Filter
        {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<Contact> filteredItems = new ArrayList<Contact>();

                    for(int i = 0, l = originalList.size(); i < l; i++)
                    {
                        Contact country = originalList.get(i);
                        if(country.getSearchString().toLowerCase().contains(constraint))
                            filteredItems.add(country);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                contactList = (ArrayList<Contact>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = contactList.size(); i < l; i++)
                    add(contactList.get(i));
                notifyDataSetInvalidated();
            }
        }



}