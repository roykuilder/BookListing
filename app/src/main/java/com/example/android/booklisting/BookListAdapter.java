package com.example.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends ArrayAdapter<Book> {

    /**
     * ViewHolder class is used to link the Object to the Views.
     */

    class ViewHolder {
        private TextView titleView;
        private TextView authorView;
        private TextView descriptionView;

        public ViewHolder(View view) {
            this.titleView = view.findViewById(R.id.title_view);
            this.authorView = view.findViewById(R.id.author_view);
            this.descriptionView = view.findViewById(R.id.desciption_view);
        }
    }

    /**
     * Constructor for a new BookListAdapter.
     *
     * @param context
     * @param list
     */
    public BookListAdapter(Context context, List<Book> list) {
        super(context, 0, list);
    }

    ViewHolder views;

    /**
     * Make a new ListItem View and set the data from the corresponding Book Object.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View bookItemView = convertView;
        if (bookItemView == null) {
            bookItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
            // Use a ViewHolder to reverence the View for the listItem.
            views = new ViewHolder(bookItemView);
            bookItemView.setTag(views);
        } else {
            views = (ViewHolder) convertView.getTag();
        }

        // Find the current Book Item
        final Book currentBookItem = getItem(position);

        // Set the text from the current Book item tot the title View.
        views.titleView.setText(currentBookItem.getTitle());

        // Set the description from the current Bok item to the description View.
        views.descriptionView.setText(currentBookItem.getDiscription());

        // Make a String from the authors ArrayList and set it to the authors View.
        ArrayList<String> authorList = currentBookItem.getCategorie();
        String authors = "";
        for (int i = 0; i < authorList.size(); i++) {
            authors = authors + authorList.get(i) + "\n";
        }
        views.authorView.setText(authors);

        // Set an OnClickListener on the ListItem and get the link from the current Book Item.
        bookItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String infoLink = currentBookItem.getInfoLink();
                Intent goToWebPage = new Intent(Intent.ACTION_VIEW);
                goToWebPage.setData(Uri.parse(infoLink));
                getContext().startActivity(goToWebPage);
            }
        });

        // This OnClickListener is needed to make the scrollView clickable.
        views.descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String infoLink = currentBookItem.getInfoLink();
                Intent goToWebPage = new Intent(Intent.ACTION_VIEW);
                goToWebPage.setData(Uri.parse(infoLink));
                getContext().startActivity(goToWebPage);
            }
        });

        // Return the list item view that is now showing the appropriate data
        return bookItemView;
    }
}
