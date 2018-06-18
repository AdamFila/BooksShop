package com.example.android.booksshop;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.booksshop.data.BookContract;
import com.example.android.booksshop.data.BookContract.BookEntry;
import com.example.android.booksshop.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter the book's author */
    private EditText mAuthorEditText;

    /** EditText field to enter the book's type */
    private Spinner mTypeSpinner;

    /**
     * Type of the book. The possible values are:
     * 0 for unknown type, 1 for business, 2 for fantasy, 3 for romances, 4 for history, 5 for drama, 6 for horror.
     */
    private int mType = 0;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** EditText field to enter the book's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the book's supplier name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the book's supplier phone number */
    private EditText mSupplierPhoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        setupSpinner();
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_book_supplier_phone_number);

    }

    private void setupSpinner(){
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_business))) {
                        mType = BookEntry.TYPE_BUSINESS;
                    } else if (selection.equals(getString(R.string.type_fantsy))) {
                        mType = BookEntry.TYPE_FANTASY;
                    } else if (selection.equals((getString(R.string.type_romances)))){
                        mType = BookEntry.TYPE_ROMANCES;
                    } else if (selection.equals((getString(R.string.type_history)))){
                        mType = BookEntry.TYPE_HISTORY;
                    } else if (selection.equals((getString(R.string.type_drama)))){
                        mType = BookEntry.TYPE_DRAMA;
                    } else if (selection.equals((getString(R.string.type_horror)))){
                        mType = BookEntry.TYPE_HORROR;
                    } else {
                        mType = BookEntry.TYPE_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = 0; // Unknown
            }
        });
    }

    private void insertBook(){

        String nameString = mNameEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        BookDbHelper mDbHelper = new BookDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_TYPE, mType);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        //TOAST
        if(newRowId == -1){
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
