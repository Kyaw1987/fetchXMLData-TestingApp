package com.freelance.android.fetchXmlData.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.freelance.android.fetchXmlData.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public final static String STOCK_SYMBOL = "com.freelance.android.fetchXmlData";

    private SharedPreferences stockSymbolsEntered;

    private TableLayout tLStockScrollView;

    private EditText eTStockSymbol;

    Button btnEnter;
    Button btnDelete;

    /*public static String yahooStockInfo = "https://query.yahooapis.com/v1/public/yql?q=Select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22MSFT%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";

    public static String stockSymbol = "";
    public static String stockDaysLow = "";
    public static String stockDaysHigh = "";
    public static String stockChange = "";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);

        tLStockScrollView = (TableLayout) findViewById(R.id.tLStockScrollView);

        eTStockSymbol = (EditText) findViewById(R.id.eTStockSymbol);

        btnEnter = (Button) findViewById(R.id.btnEnter);
        btnDelete = (Button) findViewById(R.id.btnDelete);

       /* btnEnter.setOnClickListener((View.OnClickListener) btnEnter);
        btnDelete.setOnClickListener((View.OnClickListener) btnDelete);*/

        updateSavedStockList(null);
    }

    private void saveStockSymbol(String newStock) {

        String isTheStockNew = stockSymbolsEntered.getString(newStock, null);

        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();

        preferencesEditor.putString(newStock, newStock);
        preferencesEditor.apply();

        if (null == isTheStockNew) {
            updateSavedStockList(newStock);
        }
    }

    private void updateSavedStockList(String newStockSymbol) {
        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);

        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);

        if (null != newStockSymbol) {
            insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
        } else {
            for (int i = 0; i < stocks.length; i++) {
                insertStockInScrollView(stocks[i], i);
            }
        }
    }

    public void enterStockSymbol(View view) {
        if (eTStockSymbol.getText().length() > 0) {
            saveStockSymbol(eTStockSymbol.getText().toString());
            eTStockSymbol.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eTStockSymbol.getWindowToken(), 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.invalid_stock_symbol);
            builder.setPositiveButton(R.string.ok, null);
            builder.setMessage(R.string.missing_stock_symbol);
            AlertDialog aD = builder.create();
            aD.show();
        }
    }

    public void deleteAllStockSymbols(View view) {
        tLStockScrollView.removeAllViews();

        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

    private void insertStockInScrollView(String stock, int arrayIndex) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);

        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.tVStockSymbol);

        newStockTextView.setText(stock);

        Button btnQuote = (Button) newStockRow.findViewById(R.id.btnQuote);
        btnQuote.setOnClickListener(getStockActivityListener);

        Button btnDelete = (Button) newStockRow.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(getStockFromWebsiteListener);

        tLStockScrollView.addView(newStockRow, arrayIndex);
    }

    public View.OnClickListener getStockActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tr = (TableRow) v.getParent();
            TextView stockTextView = (TextView) tr.findViewById(R.id.tVStockSymbol);
            String stockSymbol = stockTextView.getText().toString();

            Intent i = new Intent(MainActivity.this, StockInfoActivity.class);
            i.putExtra(STOCK_SYMBOL, stockSymbol);
            startActivity(i);
        }
    };

    public View.OnClickListener getStockFromWebsiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tr = (TableRow) v.getParent();
            TextView stockTextView = (TextView) tr.findViewById(R.id.tVStockSymbol);
            String stockSymbol = stockTextView.getText().toString();
            String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
            startActivity(i);
        }
    };
}

