package com.freelance.android.fetchXmlData.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.freelance.android.fetchXmlData.R;
import com.freelance.android.fetchXmlData.data.StockInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class StockInfoActivity extends AppCompatActivity {

    //    private static final String LOG_TAG = StockInfoActivity.class.getSimpleName();
    private static final String LOG_TAG = "StockQuote";

    TextView tVCompanyName;
    TextView tVYearLow;
    TextView tVYearHigh;
    TextView tVDayLow;
    TextView tVDayHigh;
    TextView tVLastPrice;
    TextView tVChange;
    TextView tVDaysRange;

    static final String KEY_ITEM = "quote";
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_LOW = "YearLow";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
    static final String KEY_CHANGE = "Change";
    static final String KEY_DAYS_RANGE = "DaysRange";

    String name = "";
    String YearLow = "";
    String YearHigh = "";
    String DaysLow = "";
    String DaysHigh = "";
    String lastTradePriceOnly = "";
    String change = "";
    String daysRange = "";

    String yahooURLFirst = "https://query.yahooapis.com/v1/public/yql?q=Select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22MSFT";
    String yahooURLSecond = "%22)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    String[][] xmlPullParserArray = {{"AverageDailyVolume", "0"}, {"Change", "0"},
            {"DaysLow", "0"}, {"DaysHigh", "0"}, {"YearLow", "0"}, {"YearHigh", "0"},
            {"MarketCapitalization", "0"}, {"LastTradePriceOnly", "0"}, {"DaysRange", "0"}, {"Name", "0"},
            {"Symbol", "0"}, {"Volume", "0"}, {"StockExchange", "0"}};

    int parserArrayIncrement = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        Intent i = getIntent();
        String stockSymbol = i.getStringExtra(MainActivity.STOCK_SYMBOL);

        tVCompanyName = (TextView) findViewById(R.id.tVCN);
        tVYearLow = (TextView) findViewById(R.id.tVYL);
        tVYearHigh = (TextView) findViewById(R.id.tVYH);
        tVDayLow = (TextView) findViewById(R.id.tVDL);
        tVDayHigh = (TextView) findViewById(R.id.tVDH);
        tVLastPrice = (TextView) findViewById(R.id.tVLTPO);
        tVChange = (TextView) findViewById(R.id.tVC);
        tVDaysRange = (TextView) findViewById(R.id.tVDPR);

        Log.d(LOG_TAG, "Before URL Creation " + stockSymbol);

        final String yqlURL = yahooURLFirst + stockSymbol + yahooURLSecond;

        new MyAsyncTask().execute(yqlURL);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL(args[0]);

                URLConnection uRLC;
                uRLC = url.openConnection();

                HttpURLConnection httpURLC = (HttpURLConnection) uRLC;
                int responceCode = httpURLC.getResponseCode();

                if (responceCode == HttpURLConnection.HTTP_OK) {

                    InputStream iS = httpURLC.getInputStream();

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document dom = db.parse(iS);

                    Element docEle = dom.getDocumentElement();

                    NodeList nl = docEle.getElementsByTagName("quote");

                    if (nl != null && nl.getLength() > 0) {
                        for (int i = 0; i < nl.getLength(); i++) {

                            StockInfo si = getStockInformation(docEle);
                            name = si.getName();
                            YearLow = si.getYearLow();
                            YearHigh = si.getYearHigh();
                            DaysLow = si.getDaysLow();
                            DaysHigh = si.getDaysHigh();
                            lastTradePriceOnly = si.getLastTradePriceOnly();
                            change = si.getChange();
                            daysRange = si.getDaysRange();

                        }
                    }
                }
            } catch (MalformedURLException e) {
                Log.d(LOG_TAG, "MalformedURLException", e);
            } catch (IOException e) {
                Log.d(LOG_TAG, "IOException", e);
            } catch (ParserConfigurationException e) {
                Log.d(LOG_TAG, "ParserConfigurationException", e);
            } catch (SAXException e) {
                Log.d(LOG_TAG, "SAXException", e);
            } finally {

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {

            tVCompanyName.setText(name);
            tVYearLow.setText("Year Low :" + YearLow);
            tVYearHigh.setText("Year High :" + YearHigh);
            tVDayLow.setText("Days Low :" + DaysLow);
            tVDayHigh.setText("Days Low :" + DaysLow);
            tVLastPrice.setText("Last Price :" + lastTradePriceOnly);
            tVChange.setText("Change :" + change);
            tVDaysRange.setText("Days Range :" + daysRange);
        }
    }

    private StockInfo getStockInformation(Element docEle) {
        String stockName = getTextValue(docEle, "Name");
        String stockYearLow = getTextValue(docEle, "YearLow");
        String stockYearHigh = getTextValue(docEle, "YearHigh");
        String stockDaysLow = getTextValue(docEle, "DaysLow");
        String stockDaysHigh = getTextValue(docEle, "DaysHigh");
        String stockLastTradePriceOnly = getTextValue(docEle, "LastTradePriceOnly");
        String stockChange = getTextValue(docEle, "Change");
        String stockDaysRange = getTextValue(docEle, "DaysRange");

        StockInfo si = new StockInfo(stockDaysLow, stockDaysHigh,
                stockYearLow, stockYearHigh,
                stockName, stockLastTradePriceOnly,
                stockChange, stockDaysRange);

        return si;
    }

    private String getTextValue(Element docEle, String tagName) {
        String tagValueReturn = null;

        NodeList nl = docEle.getElementsByTagName(tagName);

        if (nl != null && nl.getLength() > 0) {

            Element e = (Element) nl.item(0);

            tagValueReturn = e.getFirstChild().getNodeValue();
        }
        return tagValueReturn;
    }
}

/*try {
                XmlPullParserFactory xmlPPF = XmlPullParserFactory.newInstance();

                xmlPPF.setNamespaceAware(true);

                XmlPullParser xmlPP = xmlPPF.newPullParser();

                xmlPP.setInput(new InputStreamReader(getUrlData(args[0])));

                beginDocument(parser, "query");

                int eventType = parser.getEventType();

                do {
                    nextElement(parser);

                    parser.next();

                    eventType = parser.getEventType();

                    if (eventType == XmlPullParser.TEXT) {
                        String valueFromXML = parser.getText();

                        XmlPullParserArray[parserArrayIncrement++][1] = valueFromXML;
                    }

                } while (eventType != XmlPullParser.END_DOCUMENT);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }*/


/*

  public final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
            int type;

            while ((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT) {
                ;
            }
        }


        public final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
            int type;

            while ((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT) {
                ;
            }
            if (type != parser.START_TAG) {
                throw new XmlPullParserException("No Start Tag Found.");
            }

            if(!parser.getName().equals(firstElementName)) {
                throw new XmlPullParserException("Unexpected Start Tag Found." + parser.getName() + ", expected " + firstElementName);
            }
        }

        public InputStream getUrlData(String arg) throws URISyntaxException, ClientProtocolException, IOException {
            DefaultHttpClient client = new DefaultHttpClient();

            HttpGet method = new HttpGet(new URI(url));

            HttpReponse res = client.execute(method);

            return res.getEntity().getContent();

        }

    @Override
    protected void onPostExecute(String result) {

        tVCompanyName.setText(xmlPullParserArray[9][1]);
        tVYearLow.setText("Year Low :" + XmlPullParserArray[4][1]);
        tVYearHigh.setText("Year High :" + XmlPullParserArray[5][1];
        tVDayLow.setText("Days Low :" + XmlPullParserArray[2][1]);
        tVDayHigh.setText("Days Low :" + XmlPullParserArray[3][1]);
        tVLastPrice.setText("Last Price :" + XmlPullParserArray[7][1]);
        tVChange.setText("Change :" + XmlPullParserArray[1][1]);
        tVDaysRange.setText("Days Range :" + XmlPullParserArray[8][1]);
    }*/
