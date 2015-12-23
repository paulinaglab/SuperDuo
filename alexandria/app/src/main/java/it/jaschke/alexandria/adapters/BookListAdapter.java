package it.jaschke.alexandria.adapters;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.widgets.BookInfoView;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    protected Context context;
    protected Cursor cursor;
    protected OnItemClickListener onItemClickListener;

    public BookListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
        final BookViewHolder holder = new BookViewHolder(view);
        // Setting listener for item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    /**
     * Method called when any item has been clicked.
     *
     * @param clickedPos clicked item position
     */
    public void itemClicked(int clickedPos) {
        if (onItemClickListener != null) {
            if (cursor.moveToPosition(clickedPos)) {
                int idColumnIndex = cursor.getColumnIndex(AlexandriaContract.BookEntry._ID);
                if (idColumnIndex != -1) {
                    Uri clickedUri = AlexandriaContract.BookEntry.buildFullBookUri(cursor.getLong(idColumnIndex));
                    onItemClickListener.onItemClicked(clickedUri);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.bookInfoView.fillWithData(cursor);

        int colorColumnIndex = cursor.getColumnIndex(AlexandriaContract.BookEntry.COLUMN_COLOR);
        if (!cursor.isNull(colorColumnIndex))
            ((CardView) holder.itemView).setCardBackgroundColor(cursor.getInt(colorColumnIndex));
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        else
            return 0;
    }

    /**
     * Method to swap old cursor for a new one.
     *
     * @param cursor new cursor.
     */
    public void swapCursor(Cursor cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        public BookInfoView bookInfoView;

        public BookViewHolder(View itemView) {
            super(itemView);
            bookInfoView = (BookInfoView) itemView.findViewById(R.id.item_book_info_view);
        }
    }

    /**
     * Method setting listener for item click.
     *
     * @param onItemClickListener listener.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Listener for item (movie) selection.
     */
    public interface OnItemClickListener {
        /**
         * Triggered when user click an item from grid.
         *
         * @param uri uri of movie clicked by user.
         */
        void onItemClicked(Uri uri);
    }
}
