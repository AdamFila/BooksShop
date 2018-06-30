package com.example.android.booksshop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by af8ko on 18.06.2018.
 */

public final class BookContract {

    //The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.booksshop";

    //Use CONTENT_AUTHORITY to create the base of all URI's
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path (appended to base content URI for possible URI's)
    public static final String PATH_BOOKS = "books";

    public static abstract class BookEntry implements BaseColumns{

        // The content URI to access the pet data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        //The MIME type of the {@link #CONTENT_URI} for a list of books.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //The MIME type of the {@link #CONTENT_URI} for a single book
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_NAME = "name";
        public static final String COLUMN_BOOK_AUTHOR = "author";
        public static final String COLUMN_BOOK_TYPE = "type";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        //Possible values for the type of the book
        public static final int TYPE_UNKNOWN = 0;
        public static final int TYPE_BUSINESS = 1;
        public static final int TYPE_FANTASY = 2;
        public static final int TYPE_ROMANCES = 3;
        public static final int TYPE_HISTORY = 4;
        public static final int TYPE_DRAMA = 5;
        public static final int TYPE_HORROR = 6;

        public static boolean isValidType(int type) {
            if (type == TYPE_UNKNOWN || type == TYPE_BUSINESS || type == TYPE_FANTASY
                    || type == TYPE_ROMANCES || type == TYPE_HISTORY || type == TYPE_DRAMA || type == TYPE_HORROR) {
                return true;
            }
            return false;
        }
    }
}
