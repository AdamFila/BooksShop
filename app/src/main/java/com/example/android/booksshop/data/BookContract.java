package com.example.android.booksshop.data;

import android.provider.BaseColumns;

/**
 * Created by af8ko on 18.06.2018.
 */

public final class BookContract {

    public static abstract class BookEntry implements BaseColumns{

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
    }
}
