package com.example.android.booksshop;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.booksshop.data.BookContract;
import com.example.android.booksshop.data.BookContract.BookEntry;
import com.example.android.booksshop.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri mCurrentBookUri;

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

    /** Boolean flag that keeps track of whether the book has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are creating a new book
        if(mCurrentBookUri == null){
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_book_supplier_phone_number);

        Button quantityMinus = (Button) findViewById(R.id.button_minus);
        Button quantityPlus = (Button) findViewById(R.id.button_plus);
        Button callButton = (Button) findViewById(R.id.button_call);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        quantityMinus.setOnTouchListener(mTouchListener);
        quantityPlus.setOnTouchListener(mTouchListener);
        callButton.setOnTouchListener(mTouchListener);

        quantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString)) {
                    Toast.makeText(EditorActivity.this, "Quantity can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    quantity = Integer.parseInt(quantityString);
                    //To validate if quantity is greater than 0
                    if ((quantity - 1) >= 0) {
                        mQuantityEditText.setText(String.valueOf(quantity - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, "Quantity can not get below 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        quantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantityString)) {
                    Toast.makeText(EditorActivity.this, "Quantity can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    quantity = Integer.parseInt(quantityString);
                    mQuantityEditText.setText(String.valueOf(quantity + 1));
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();
                makeACall(supplierPhoneNumber);
            }
        });
        
        setupSpinner();
    }

    private void makeACall(String supplierPhoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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

        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                mType == BookEntry.TYPE_UNKNOWN &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneNumberString)){
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_TYPE, mType);
        // If the price and quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0, quantity = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            //TOAST
            if (newUri == null) {
                Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book saved with row id: ", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
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
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////
    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_TYPE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int typeColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int suppierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(suppierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);

            //Type is a dropdown spinner, so map the constant value from the database
            switch (type) {
                case BookEntry.TYPE_BUSINESS:
                    mTypeSpinner.setSelection(1);
                    break;
                case BookEntry.TYPE_FANTASY:
                    mTypeSpinner.setSelection(2);
                    break;
                case BookEntry.TYPE_ROMANCES:
                    mTypeSpinner.setSelection(3);
                    break;
                case BookEntry.TYPE_HISTORY:
                    mTypeSpinner.setSelection(4);
                    break;
                case BookEntry.TYPE_DRAMA:
                    mTypeSpinner.setSelection(5);
                    break;
                case BookEntry.TYPE_HORROR:
                    mTypeSpinner.setSelection(6);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mTypeSpinner.setSelection(0); // Select "Unknown" gender
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
