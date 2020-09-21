package com.plumb5.plugin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.plumb5.plugin.P5LifeCycle.cordovaActivity;
import static com.plumb5.plugin.P5LifeCycle.cordovaWebView;


public class P5DialogBox {

    JSONArray fieldsList;
    private static Activity context;
    List<Object> allFields = new ArrayList<Object>();
    List<Object> allFieldsName = new ArrayList<Object>();
    final JSONObject finalData = new JSONObject();
    private DatePickerDialog caldialog = null;
    public GradientDrawable gd;
    static AlertDialog p5dialog;
    public String getResponses = "Widget", WidgetName = "", CloseDialog = "", ScreenName = "", ServiceUrl = "", getImageUrl = "", dPosition = "", dAnimation = "0", BorderColor = "#ffffff", BgColor = "#ffffff", BgImage = "", fieldType = "", fieldName = "", fieldCategory = "", fieldText = "", fieldImageUrl = "", fieldColor = "", fieldBgColor = "#ffffff", fieldBorderColor = "#ffffff", rbOrientation = "Hor", getAlign = "Left", Action = "", Message = "", Redirect = "", Parameter = "";
    public float fieldSize = 0;
    public int SendReport = 0, Interval = 0, MobileFormId = 0, CalPosition = 0, finalPosition = 0, fCheck = 0, fLoop = 0, fHorWidth = 0, Height = 0, BorderWidth = 0, BorderRadius = 0, fieldLeftD = 0, fieldTopD = 0, fieldRightD = 0, fieldBottomD = 0, fieldWidth = 0, fieldImgWidth = 0, fieldLeft = 0, fieldTop = 0, fieldRight = 0, fieldBottom = 0, fieldLeftM = 0, fieldTopM = 0, fieldRightM = 0, fieldBottomM = 0, fieldBorder = 0, fieldBorderRadius = 0, fieldAlign = 0, fieldOrientation = 0, fiedStyle = 0, Group = 1, Close = 1, mGroupFieldHeight = 0, formSubmit = 0;
    Plumb5 eng = new Plumb5();
    protected static final String TAG = "P5 - Dialog Box";
    private static String OldPage = "";
    HashMap<Integer, String> actionData = new HashMap<Integer, String>();


    public AlertDialog getP5dialog() {
        return p5dialog;
    }

    public void P5dialogdismiss() {
        if (p5dialog != null) {
            p5dialog.dismiss();
            p5dialog = null;
        }
    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    public void dialogBox(final Activity activity, String getContent, final String getServiceUrl, int FormId, String getWidgetName) {

        //Log.d("p5", "Dialog Started" + getContent);

        try {
            WidgetName = getWidgetName.toString();
            ServiceUrl = getServiceUrl;
            context = activity;
            MobileFormId = FormId;
            ScreenName = eng.p5GetScreenName(activity);


            EditText allEt[] = new EditText[1];
            int getPadding = 10;
            final RelativeLayout diagLayout = new RelativeLayout(context);
            // diagLayout.setOrientation(LinearLayout.VERTICAL);


            final ImageView imgn = new ImageView(context);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            JSONObject jsonObject = new JSONObject(getContent);

            // Log.d("p5", "aa");

            dPosition = jsonObject.getJSONObject("Display").getString("Position");
            dAnimation = jsonObject.getJSONObject("Display").getString("Animation");
            BgColor = jsonObject.getJSONObject("Display").getString("BgColor");
            BgImage = jsonObject.getJSONObject("Display").getString("BgImage");
            BorderColor = jsonObject.getJSONObject("Display").getString("Border");
            BorderWidth = Integer.parseInt(jsonObject.getJSONObject("Display").getString("BorderWidth"));
            BorderRadius = Integer.parseInt(jsonObject.getJSONObject("Display").getString("BorderRadius"));
            Height = Integer.parseInt(jsonObject.getJSONObject("Display").getString("Height"));
            Close = Integer.parseInt(jsonObject.getJSONObject("Display").getString("Close"));
            Interval = Integer.parseInt(jsonObject.getJSONObject("Display").getString("Interval"));

            //Log.d("p5", "bb");
            //set padding for main layout....
            String[] dpText = jsonObject.getJSONObject("Display").getString("Padding").split("\\,");
            if (dpText.length == 4) {
                fieldLeftD = Integer.parseInt(dpText[0]);
                fieldLeftD = convertPixelsToDp(fieldLeftD, context);
                fieldTopD = Integer.parseInt(dpText[1]);
                fieldTopD = convertPixelsToDp(fieldTopD, context);
                fieldRightD = Integer.parseInt(dpText[2]);
                fieldRightD = convertPixelsToDp(fieldRightD, context);
                fieldBottomD = Integer.parseInt(dpText[3]);
                fieldBottomD = convertPixelsToDp(fieldBottomD, context);
            }

            diagLayout.setPadding(fieldLeftD, fieldTopD, fieldRightD, fieldBottomD);


            //Log.d("p5", "cc");
            //Bind fields in side layout....
            fieldsList = (JSONArray) jsonObject.get("Fields");
            for (int fieldNo = 0; fieldNo < fieldsList.length(); fieldNo++) {

                //Log.d("p5", "dd");
                JSONObject getfield = fieldsList.getJSONObject(fieldNo);
                fieldType = getfield.getString("Type");
                fieldName = getfield.getString("Name");
                fieldCategory = getfield.getString("Category");
                fieldText = getfield.getString("Text");
                fieldImageUrl = getfield.getString("ImageUrl");
                fieldSize = Float.parseFloat(getfield.getString("Size"));
                fieldColor = getfield.getString("Color");
                fieldBgColor = getfield.getString("BgColor");
                fieldBorderColor = getfield.getString("Border");
                fieldBorder = Integer.parseInt(getfield.getString("BorderWidth"));
                fieldBorderRadius = Integer.parseInt(getfield.getString("BorderRadius"));

                //Log.d("p5", "ee"+fieldImageUrl);
                //set margin with main layout padding for field....
                fieldWidth = (context.getResources().getDisplayMetrics().widthPixels * Integer.parseInt(getfield.getString("Width"))) / 100;
                String[] mText = getfield.getString("Margin").split("\\,");
                if (mText.length == 4) {
                    fieldLeftM = Integer.parseInt(mText[0]);
                    fieldLeftM = convertPixelsToDp(fieldLeftM, context);
                    fieldTopM = Integer.parseInt(mText[1]);
                    fieldTopM = convertPixelsToDp(fieldTopM, context);
                    fieldRightM = Integer.parseInt(mText[2]);
                    fieldRightM = convertPixelsToDp(fieldRightM, context);
                    fieldBottomM = Integer.parseInt(mText[3]);
                    fieldBottomM = convertPixelsToDp(fieldBottomM, context);
                }

                //Log.d("p5", "ff");
                //set padding for field....
                String[] pText = getfield.getString("Padding").split("\\,");
                if (pText.length == 4) {
                    fieldLeft = Integer.parseInt(pText[0]);
                    fieldLeft = convertPixelsToDp(fieldLeft, context);
                    fieldTop = Integer.parseInt(pText[1]);
                    fieldTop = convertPixelsToDp(fieldTop, context);
                    fieldRight = Integer.parseInt(pText[2]);
                    fieldRight = convertPixelsToDp(fieldRight, context);
                    fieldBottom = Integer.parseInt(pText[3]);
                    fieldBottom = convertPixelsToDp(fieldBottom, context);
                }

                //Log.d("p5", "gg");
                //set align of parent layout for field....
                getAlign = getfield.getString("Align");
                if (getAlign.equals("Top")) {
                    fieldAlign = RelativeLayout.ALIGN_PARENT_TOP;
                } else if (getAlign.equals("Bottom")) {
                    fieldAlign = RelativeLayout.ALIGN_PARENT_BOTTOM;
                } else if (getAlign.equals("End")) {
                    fieldAlign = RelativeLayout.ALIGN_PARENT_END;
                } else if (getAlign.equals("Center")) {
                    fieldAlign = RelativeLayout.CENTER_HORIZONTAL;
                } else if (getAlign.equals("Right")) {
                    fieldAlign = RelativeLayout.ALIGN_PARENT_RIGHT;
                } else {
                    fieldAlign = RelativeLayout.ALIGN_PARENT_LEFT;
                }

                //Log.d("p5", "hh");
                //set align of text for field....
                String getOrientation = getfield.getString("Orientation");
                if (getOrientation.indexOf(',') > 0) {
                    String[] pOrien = getOrientation.split("\\,");
                    getOrientation = pOrien[0].toString();
                    rbOrientation = pOrien[1].toString();
                }

                if (getOrientation.equals("Right")) {
                    fieldOrientation = Gravity.RIGHT;
                } else if (getOrientation.equals("Top")) {
                    fieldOrientation = Gravity.TOP;
                } else if (getOrientation.equals("Bottom")) {
                    fieldOrientation = Gravity.BOTTOM;
                } else if (getOrientation.equals("Center")) {
                    fieldOrientation = Gravity.CENTER;
                } else {
                    fieldOrientation = Gravity.LEFT;
                }

                // this orientation is
                //Log.d("p5", "ii");
                //set style like bold and italic of text for field....
                String fstyle = getfield.getString("Style");
                if (fstyle.equals("Bold_Italic")) {
                    fiedStyle = Typeface.BOLD_ITALIC;
                } else if (fstyle.equals("Bold")) {
                    fiedStyle = Typeface.BOLD;
                } else if (fstyle.equals("Italic")) {
                    fiedStyle = Typeface.ITALIC;
                } else {
                    fiedStyle = Typeface.NORMAL;
                }

                //Log.d("p5", "jj"+fstyle);
                Group = Integer.parseInt(getfield.getString("Group"));

                //if field is button then get action on click....
                if (fieldType.equals("Btn") || fieldType.equals("Img") || fieldType.equals("Tv"))
                {
                    Action = getfield.getString("Action");
                    Message = getfield.getString("Message");
                    Redirect = getfield.getString("Redirect");
                    Parameter = getfield.getString("Parameter");
                    CloseDialog = getfield.getString("CloseDialog");
                }

                //Log.d("p5", "kk");
                //set border and radios for field....
                gd = new GradientDrawable();
                if (fieldBgColor.length() > 0)
                {
                    gd.setColor(Color.parseColor(fieldBgColor));
                }
                gd.setCornerRadius(fieldBorderRadius);
                if (fieldBorderColor.length() > 0) {
                    gd.setStroke(fieldBorder, Color.parseColor(fieldBorderColor));
                }
                //Log.d("p5", "ll");

                //calculate position and width for field....
                if (Group > 1) {
                    fHorWidth = fieldWidth * fLoop;
                    fLoop = fLoop + 1;
                    if (fLoop == 1) {
                        if (fCheck > 0) {
                            finalPosition = (finalPosition + fCheck) - 1;
                        } else {
                            finalPosition = fieldNo;
                        }
                    }

                    if (Group == fLoop) {
                        fLoop = 0;
                        Group = 1;
                    }

                    fCheck = Group;
                    mGroupFieldHeight = fHorWidth != 0 ? 100 : 0;

                } else {

                    fieldTopM = fieldTopM + mGroupFieldHeight;
                    mGroupFieldHeight = 0;
                    if (fCheck > 0) {
                        finalPosition = (finalPosition + fCheck) - 1;
                    } else {
                        finalPosition = fieldNo;
                    }

                    fCheck = 0;
                    fHorWidth = 0;
                }


                //Log.d("p5", "mm"+fieldType);
                //Field Binding..............................................

                if (fieldNo == 0) {
                    TextView intTv = new TextView(context);
                    intTv.setId(fieldNo);
                    diagLayout.addView(intTv);
                }


                //Log.d("p5", "4364643");
                if (fieldType.equals("Tv")) {
                    diagLayout.addView(getTextView(fieldNo, fieldText, fieldSize, fieldColor));
                } else if (fieldType.equals("Et")) {
                    if (fieldCategory.equals("Calender")) {
                        diagLayout.addView(getCalender(fieldNo, fieldText, fieldSize, fieldColor));
                    } else {
                        diagLayout.addView(getEditText(fieldNo, fieldText, fieldSize, fieldColor));
                    }
                } else if (fieldType.equals("Cb")) {
                    String[] texts = fieldText.split(",");
                    for (int i = 0; i < texts.length; i++) {
                        Log.e("", "" + fieldText);
                        if (i == 0) {
                            diagLayout.addView(getCheckBox(fieldNo, texts[i], fieldSize, fieldColor));
                        } else {
                            diagLayout.addView(getCheckBox(fieldNo+i, texts[i], fieldSize, fieldColor));
                        }
                    }
                    //  diagLayout.addView(getCheckBox(fieldNo, fieldText, fieldSize, fieldColor,rbOrientation));
                } else if (fieldType.equals("Rg")) {
                    diagLayout.addView(getRadioGroup(fieldNo, fieldText, fieldSize, fieldColor, rbOrientation));
                } else if (fieldType.equals("Sp")) {
                    diagLayout.addView(getSpinner(fieldNo, fieldText, fieldSize, fieldColor));
                } else if (fieldType.equals("Img")) {
                    getImageUrl = fieldImageUrl;
                    fieldImgWidth = fieldWidth;
                    diagLayout.addView(getImage(imgn, fieldNo, fieldImageUrl, alertDialogBuilder));
                } else if (fieldType.equals("Btn")) {
                    diagLayout.addView(getButton(fieldNo, fieldText, fieldSize, fieldColor));
                } else if (fieldType.equals("Rb")) {
                    diagLayout.addView(getRatingBar(fieldNo, fieldText, fieldSize, fieldColor));
                } else if (fieldType.equals("Cd")) {
                    diagLayout.addView(getCountDown(fieldNo, fieldText, fieldSize, fieldColor));
                }

                //check form submit
                if (!fieldType.equals("Tv") && !fieldType.equals("Img") && !fieldType.equals("Cd") && !fieldType.equals("Btn") && formSubmit == 0) {
                    getResponses = "Form";
                    formSubmit = 1;
                }
                //Log.d("p5", "2222");

                String indicate = "";
                if (fieldCategory.equals("Name")) {
                    indicate = "#0$";
                }
                if (fieldCategory.equals("Emailid")) {
                    indicate = "#1$";
                } else if (fieldCategory.equals("Phone")) {
                    indicate = "#2$";
                }

                allFieldsName.add(fieldNo, fieldName + indicate);

                if (fieldNo == fieldsList.length() - 1 && Close == 1) {
                    ImageView cimg = getCancelImage();
                    diagLayout.addView(cimg);
                }

            }

//            }
//            catch (JSONException ignoredExc){}

            //Log.d("p5", "mmfff");

            //Insert loading................

                JSONObject json = new JSONObject();
                try {
                    json.put("AppKey", eng.p5GetAppKey());
                    json.put("MobileFormId", MobileFormId);
                    json.put("DeviceId", eng.getDeviceId(context));
                    json.put("SessionId", P5LifeCycle.getP5Session());
                    json.put("ScreenName", ScreenName);
                    json.put("FormResponses", getResponses);
                    json.put("BannerView", 1);
                    json.put("BannerClick", 0);
                    json.put("BannerClose", 0);
                    json.put("WorkFlowDataId",0);
                    json.put("SendReport", 0);
                    json.put("P5UniqueId", "");
                    json.put("ButtonName", "");
                    json.put("WidgetName", "");
                } catch (JSONException e) {
                    Log.e(TAG, " DialogBox push details failed");
                    e.printStackTrace();
                    Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                }

                String getresult = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                //new P5HttpRequest(context, ServiceUrl + context.getResources().getString(R.string.FORM_RESPONSES), getresult).execute();
                new P5LifeCycle().callPushSend(context, new ObjectMapper().readValue(getresult, HashMap.class));
                OldPage = ScreenName + "." + MobileFormId;

            //end loading................

            //set bgcolor and border and radios for main layout....
            GradientDrawable border = new GradientDrawable();
            if (BgColor.length() > 0) {
                border.setColor(Color.parseColor(BgColor));
            }
            if (BorderColor.length() > 0) {
                border.setStroke(BorderWidth, Color.parseColor(BorderColor));
            }
            border.setCornerRadius(BorderRadius);
            diagLayout.setBackground(border);

            //set height for main layout....
            int sHeight = LayoutParams.WRAP_CONTENT;
            if (Height != 0) {
                sHeight = (context.getResources().getDisplayMetrics().heightPixels * Height) / 100;
            }
            diagLayout.setMinimumHeight(sHeight);
            alertDialogBuilder.setView(diagLayout);

            if (getP5dialog() != null) {
                P5dialogdismiss();
            }


            if (!dAnimation.equals("0")) {
                Animation trans = null;
                if (dAnimation.equals("1")) {
                    trans = new TranslateAnimation(0, 0, 1000, 0);
                } else if (dAnimation.equals("2")) {
                    trans = new TranslateAnimation(0, 0, -1000, 0);
                } else if (dAnimation.equals("3")) {
                    trans = new TranslateAnimation(1000, 0, 0, 0);
                } else if (dAnimation.equals("4")) {
                    trans = new TranslateAnimation(-1000, 0, 0, 0);
                }
                AnimationSet showanim = new AnimationSet(false);
                trans.setDuration(1000);
                showanim.addAnimation(trans);

                diagLayout.setAnimation(showanim);
            }

            //Log.d("p5", "235423523");
            p5dialog = alertDialogBuilder.create();

            //set position for main alayout....
            WindowManager.LayoutParams wmlp = p5dialog.getWindow().getAttributes();
            if (dPosition.equals("Top")) {
                wmlp.gravity = Gravity.TOP;
            } else if (dPosition.equals("Bottom")) {
                wmlp.gravity = Gravity.BOTTOM;
            } else if (dPosition.equals("Center")) {
                wmlp.gravity = Gravity.CENTER;
            }


            //set TRANSPARENT for dialog....
            p5dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            if (BgImage.length() > 0) {
                ImageView bgimg = new ImageView(context);
                int dShow = 0;
                if (getImageUrl.length() > 0) {
                    dShow = -1;
                }//no need "dialog.show();" if "-1"
                new P5DialogPicture(context, BgImage, bgimg, p5dialog, fieldWidth, diagLayout, fieldBottom,dShow);
            }


            if (getImageUrl.length() == 0 && BgImage.length() == 0) {

                if (Interval == 0) {
                    AlertDialog alertDialog = getP5dialog();
                    alertDialog.show();
                } else {
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            new OpenDialogByInterval().execute();
                            t.cancel();
                        }
                    }, 1000 * Interval);
                }
            } else {
                //Log.d("p5", "2222");
                final String imgtempUrl = getImageUrl;
                getImageUrl = "";
                int dShow = 1;
                if (Interval == 0) {
                    new P5DialogPicture(context, imgtempUrl, imgn, p5dialog, fieldImgWidth, diagLayout, fieldBottom,dShow);
                } else {
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            int dShow = 1;
                            try {
                                new P5DialogPicture(context, imgtempUrl, imgn, p5dialog, fieldImgWidth, diagLayout, fieldBottom,dShow);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            t.cancel();
                        }
                    }, 1000 * Interval);
                }
            }

        } catch (Throwable e) {
            Log.d(TAG, "something goes wrong.");
            Log.e(TAG, " DialogBox push details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
        }

    }

    private class OpenDialogByInterval extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(Long result) {
            try {
                if (p5dialog != null)
                    p5dialog.show();
            } catch (Throwable e) {
                Log.e(TAG, " DialogBox show failed");
                e.printStackTrace();

            }
        }
    }

    public TextView getTextView(int Position, String textValue, Float fontSize, String fontColor) {
        int closeimgwidth = Position == 0 ? 50 : 0;
        TextView text = new TextView(context);
        text.setId(Position + 1);
        text.setPadding(fieldLeft, fieldTop, fieldRight + closeimgwidth, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        text.setLayoutParams(lp);
        text.setBackground(gd);
        text.setGravity(fieldOrientation);
        text.setTypeface(null, fiedStyle);
        text.setText(textValue);
        text.setTextSize(fontSize);
        text.setTextColor(Color.parseColor(fontColor));

        actionData.put(Position + 1, Action + "^" + Redirect + "^" + Parameter + "^" + textValue + "^" + CloseDialog + "^" + Message);

        if (Action.length() > 0) {
            text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickAction(1, v.getId());
                }
            });
        }

        allFields.add(Position, text);
        return text;
    }

    public EditText getEditText(int Position, String textValue, Float fontSize, String fontColor) {

        EditText et = new EditText(context);
        et.setId(Position + 1);
        et.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        et.setLayoutParams(lp);

        et.setBackground(gd);
        et.setGravity(fieldOrientation);
        et.setTypeface(null, fiedStyle);

        if (textValue.length() > 0) {
            et.setHint(textValue);
        }

        if (fieldCategory.equals("Emailid")) {
            et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (fieldCategory.equals("Password")) {
            et.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        } else if (fieldCategory.equals("Phone")) {
            et.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (fieldCategory.equals("Number")) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (fieldCategory.equals("Multiline")) {
            et.setMinLines(2);
            et.setVerticalScrollBarEnabled(true);
        }


        et.setTextSize(fontSize);
        et.setTextColor(Color.parseColor(fontColor));

        allFields.add(Position, et);
        return et;
    }

    public CheckBox getCheckBox(int Position, String textValue, Float fontSize, String fontColor) {

        CheckBox cb = new CheckBox(context);
        cb.setId(Position + 1);
        cb.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, Position);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        cb.setLayoutParams(lp);
        cb.setBackground(gd);
        cb.setGravity(fieldOrientation);
        cb.setTypeface(null, fiedStyle);
        cb.setText(textValue);
        cb.setTextSize(fontSize);
        cb.setTextColor(Color.parseColor(fontColor));
        cb.setChecked(false);
        allFields.add(Position, cb);
        return cb;
    }

//    public CheckBox getCheckBox(int Position, String textValue, Float fontSize, String fontColor,String Orientation) {
//        int i;CheckBox[]cb=null;
//        String[]aText=textValue.split("\\,");
//        for( i=0;i<aText.length;i++){
//            cb=new CheckBox[aText.length];
//
//            cb[i] = new CheckBox(context);
//            cb[i].setId(Position + 1);
//            cb[i].setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
//            lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
//            if (!getAlign.equals("Top") && !getAlign.equals("Bottom"))
//            {
//                lp.addRule(RelativeLayout.BELOW, Position);
//            }
//            lp.addRule(fieldAlign, RelativeLayout.TRUE);
//            cb[i].setLayoutParams(lp);
//            cb[i].setBackground(gd);
//            cb[i].setGravity(fieldOrientation);
//            cb[i].setTypeface(null, fiedStyle);
//            cb[i].setText(aText[i].toString());
//            cb[i].setTextSize(fontSize);
//            cb[i].setTextColor(Color.parseColor(fontColor));
//            cb[i].setChecked(false);
//            allFields.add(Position, cb[i]);
//            return cb[i];
//        }
//        return cb[i];
//    }

    public RadioGroup getRadioGroup(int Position, String textValue, Float fontSize, String fontColor, String Orientation) {
        String[] aText = textValue.split("\\,");


        RadioGroup rg = new RadioGroup(context);
        rg.setId(Position + 1);
        RadioButton[] rb = new RadioButton[aText.length];
        if(aText.length<=2){
            if (Orientation.equals("Ver")) {
                rg.setOrientation(RadioGroup.VERTICAL);
            } else {
                rg.setOrientation(RadioGroup.HORIZONTAL);
            }}
        rg.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        rg.setLayoutParams(lp);
        rg.setBackground(gd);
        rg.setGravity(fieldOrientation);


        //or RadioGroup.VERTICAL
        for (int i = 0; i < aText.length; i++) {
            rb[i] = new RadioButton(context);
            rg.addView(rb[i]);
            rb[i].setText(aText[i].toString());
            rb[i].setTextSize(fontSize);
            rb[i].setTextColor(Color.parseColor(fontColor));
            rb[i].setTypeface(null, fiedStyle);
            if (i == 0) {
                rb[0].setChecked(true);
            }
        }
        allFields.add(Position, rg);

        return rg;
    }

    public Spinner getSpinner(int Position, String textValue, final Float fontSize, final String fontColor) {
        final String[] paths = textValue.split("\\,");
        final Spinner sp = new Spinner(context);
        sp.setId(Position + 1);
        final int fstyle = fiedStyle;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, paths) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(fontSize);
                textView.setTextColor(Color.parseColor(fontColor));
                textView.setTypeface(null, fstyle);
                return view;
            }
        };

        sp.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        sp.setLayoutParams(lp);
        sp.setBackground(gd);
        sp.setGravity(fieldOrientation);

        sp.setAdapter(adapter);

        allFields.add(Position, sp);
        return sp;
    }

    public ImageView getImage(ImageView imgn, int Position, String imgUrl, AlertDialog.Builder builder) {

        imgn.setId(Position + 1);
        imgn.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        imgn.setLayoutParams(lp);
        imgn.setBackground(gd);
        actionData.put(Position + 1, Action + "^" + Redirect + "^" + Parameter + "^" + imgUrl + "^" + CloseDialog + "^" + Message);

        if (Action.length() > 0) {
            imgn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickAction(1, v.getId());
                }
            });
        }
        allFields.add(Position, imgn);
        return imgn;

    }

    public Button getButton(int Position, final String textValue, Float fontSize, String fontColor) {

        Button btn = new Button(context);
        btn.setId(Position + 1);
        btn.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        btn.setLayoutParams(lp);
        btn.setBackground(gd);
        btn.setGravity(fieldOrientation);
        btn.setTypeface(null, fiedStyle);
        btn.setText(textValue);
        btn.setTransformationMethod(null);
        btn.setTextSize(fontSize);
        btn.setTextColor(Color.parseColor(fontColor));

        actionData.put(Position + 1, Action + "^" + Redirect + "^" + Parameter + "^" + textValue + "^" + CloseDialog + "^" + Message);

        if (Action.length() > 0) {
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    onClickAction(1, v.getId());
                }
            });
        }

        allFields.add(Position, btn);
        return btn;
    }

    public RatingBar getRatingBar(int Position, String textValue, Float fontSize, String fontColor) {
        RatingBar Rb = new RatingBar(context);
        Rb.setId(Position + 1);
        Rb.setStepSize((float) 0.1);
        Rb.setNumStars(Integer.parseInt(textValue));
        //Rb.setMax(3);
        Rb.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        Rb.setLayoutParams(lp);
        Rb.setBackground(gd);


        LayerDrawable stars = (LayerDrawable) Rb.getProgressDrawable();
        //stars.getDrawable(0).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor(fontColor), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.parseColor(fontColor), PorterDuff.Mode.SRC_ATOP);


        allFields.add(Position, Rb);
        return Rb;
    }


    public EditText getCalender(final int Position, String textValue, Float fontSize, String fontColor) {
        EditText calet = new EditText(context);
        calet.setFocusable(false);
        calet.setId(Position + 1);
        calet.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        calet.setLayoutParams(lp);

        calet.setBackground(gd);
        calet.setGravity(fieldOrientation);
        calet.setTypeface(null, fiedStyle);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        if (textValue.length() > 0) {
            calet.setHint(textValue);
        } else {
            calet.setHint(day + " - " + month + " - " + year);
        }
        calet.setTextSize(fontSize);
        calet.setTextColor(Color.parseColor(fontColor));

        calet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                CalPosition = Position;
                caldialog = new DatePickerDialog(v.getContext(), new PickDate(), year, month, day);
                caldialog.show();
            }
        });

        allFields.add(Position, calet);
        return calet;


    }


    public TextView getCountDown(int Position, String textValue, Float fontSize, String fontColor) {
        final TextView text = new TextView(context);
        text.setId(Position + 1);
        text.setPadding(fieldLeft, fieldTop, fieldRight, fieldBottom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(fieldWidth, LayoutParams.WRAP_CONTENT);//fieldRight
        lp.setMargins(fHorWidth + fieldLeftM, fieldTopM, fieldRightM, fieldBottomM);
        if (!getAlign.equals("Top") && !getAlign.equals("Bottom")) {
            lp.addRule(RelativeLayout.BELOW, finalPosition);
        }
        lp.addRule(fieldAlign, RelativeLayout.TRUE);
        text.setLayoutParams(lp);
        text.setBackground(gd);
        text.setGravity(fieldOrientation);
        text.setTypeface(null, fiedStyle);


        text.setTextSize(fontSize);
        text.setTextColor(Color.parseColor(fontColor));

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date finaldate = dateFormat.parse(textValue);

            long diffInMs = finaldate.getTime() - date.getTime();
            new CountDownTimer(diffInMs, 1000) {

                public void onTick(long millis) {

                    String gettimer = "";
                    if (millis > 86400000) {

                        String getday = "DAYS";
                        if (Long.toString(TimeUnit.MILLISECONDS.toDays(millis)) == "1") {
                            getday = "DAY";
                        }
                        gettimer = Long.toString(TimeUnit.MILLISECONDS.toDays(millis)) + " " + getday;
                    } else {
                        gettimer = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    }

                    text.setText(gettimer);
                }

                public void onFinish() {

                }
            }.start();
        } catch (Exception e) {
            Log.e(TAG, " DialogBox push details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());

        }
        allFields.add(Position, text);
        return text;
    }


    private class PickDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            EditText getcalet = (EditText) allFields.get(CalPosition);
            getcalet.setText(dayOfMonth + " - " + monthOfYear + " - " + year);
            caldialog.hide();
        }

    }


    public ImageView getCancelImage() {
        ImageView cimg = new ImageView(context);
        cimg.setImageResource(android.R.drawable.ic_notification_clear_all);
//        String closeimageurl = "https://s3-us-west-2.amazonaws.com/in-app-images/moe_close_1.png";
//        new P5ImageLoader(cimg, closeimageurl, 2, "",null).execute();


        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_notification_clear_all);
        int width = Math.round(bitmapOrg.getWidth() * 75 / 100);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


        cimg.setLayoutParams(lp);
        cimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                try {


                    json.put("AppKey", eng.p5GetAppKey());
                    json.put("MobileFormId", MobileFormId);
                    json.put("DeviceId", eng.getDeviceId(context));
                    json.put("SessionId", P5LifeCycle.getP5Session());
                    json.put("ScreenName", ScreenName);
                    json.put("FormResponses", getResponses);
                    json.put("BannerView", 0);
                    json.put("BannerClick", 0);
                    json.put("BannerClose", 1);
                    json.put("WorkFlowDataId",0);
                    json.put("SendReport", 0);
                    json.put("P5UniqueId", "");
                    json.put("ButtonName", "");
                    json.put("WidgetName", "");
                    json.put("SendReport", 0);
                    json.put("P5UniqueId", "");
                    json.put("ButtonName", "");
                    json.put("WidgetName", "");

                } catch (JSONException e) {
                    Log.e(TAG, " DialogBox push details failed");
                    e.printStackTrace();
                    Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                }
                String fresult = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                TextView p5textView = new TextView(context);
              //  new P5HttpRequest(context, ServiceUrl + context.getResources().getString(R.string.FORM_RESPONSES), fresult).execute();
                try {
                    new P5LifeCycle().callPushSend(context, new ObjectMapper().readValue(fresult, HashMap.class));
                } catch (IOException e) {
                    Log.e(TAG, " DialogBox push details failed");
                    e.printStackTrace();
                    Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                }
                P5dialogdismiss();
            }
        });

        return cimg;
    }

    public void onClickAction(int result, int getId) {

        try {
            String getAll = actionData.get(getId);
            String[] aText = getAll.split("\\^");
            String getAction = "", getRedirect = "", getParameter = "", getName = "", getCloseDialog = "", getMsg = "";
            if (aText.length > 0) {
                getAction = aText[0].toString();
                getRedirect = aText[1].toString();
                getParameter = aText[2].toString();
                getName = aText[3].toString();
                getCloseDialog = aText[4].toString();
                if (aText.length == 6) {
                    getMsg = aText[5].toString();
                } else {
                    getMsg = "";
                }

            }

            //  Log.i("HI", getAll+"/"+getCloseDialog);

            if (formSubmit == 1) {
                getName = "Form";
                result = btnFormSubmit();
            }


            if (result == 1 || formSubmit == 0) {


                int click = 1, close = 0;
                if (getAction.equals("Cancel")) {
                    click = 0;
                    close = 1;
                } else if (getAction.equals("Screen") || getAction.equals("Deeplink")) {
                    Intent intent = new Intent();

                    if (getParameter.indexOf(',') > 0 && getParameter.length() > 1) {
                        String[] paText = getParameter.split("\\,");
                        for (int i = 0; i < paText.length; i++) {
                            String[] paTextValue = paText[i].split("\\=");
                            intent.putExtra(paTextValue[0], paTextValue[1]);
                        }
                    } else if (getParameter.indexOf('=') > 0 && getParameter.length() > 1) {
                        String[] paTextValue = getParameter.split("\\=");
                        intent.putExtra(paTextValue[0], paTextValue[1]);
                    }

                    if (getAction.equals("Screen")) {
                        intent.setClassName(context, getRedirect);
                        if (new P5ConnectionDetector(context).isConnectingToInternet()) {
                            P5dialogdismiss();
                            eng.navigateScreen(Redirect, cordovaActivity, cordovaWebView);

                        } else {
                            Log.d("p5", "No internet");
                        }
                    } else {
                        int lene = getRedirect.lastIndexOf('.');
                        intent.setComponent(new ComponentName(getRedirect.substring(0, lene), getRedirect));
                        P5dialogdismiss();
                        eng.navigateScreen(Redirect, cordovaActivity, cordovaWebView);
                    }
                } else if (getAction.equals("Browser") && getRedirect.contains("http")) {
                    Uri uri = Uri.parse(getRedirect);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (getAction.equals("Copy") && getRedirect.length() > 0) {
                    Toast.makeText(context, "Copied successfully.", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text label", getRedirect);
                    clipboardManager.setPrimaryClip(clipData);
                } else if (getAction.equals("Call") && getRedirect.length() > 0) {
                    if (checkCallPermission(context)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + getRedirect));
                        context.startActivity(callIntent);
                    }
                } else if (getAction.equals("Share") && getRedirect.length() > 0) {
                    String app_name = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getRedirect.toString());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, getParameter.toString());
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via " + app_name));
                } else if (getAction.equals("Reminder") && getRedirect.length() > 0) {
                    Intent aintent = new Intent("plumb5.alarm");
                    aintent.putExtra("alarmtxt", getParameter.toString());
                    PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, aintent, 0);
                    AlarmManager alarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));

                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 * Integer.parseInt(getRedirect.toString()), pendingAlarmIntent);
                } else if (getAction.equals("Event") && getRedirect.length() > 0) {
                   // P5Engine.P5Eventdata(context, "Custom-InApp-Event", getRedirect.toString(), getParameter.toString());
                } else if (getAction.equals("Sms")) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setData(Uri.parse("smsto:" + getRedirect.toString()));
                    smsIntent.putExtra("sms_body", getParameter.toString());
                    context.startActivity(smsIntent);

                } else if (getAction.equals("Permission")) {

                    Plumb5.p5ChkPermission(context, getRedirect.toString());

                }


                if (formSubmit == 0) {
                    JSONObject json = new JSONObject();
                    try {

                        json.put("AppKey", eng.p5GetAppKey());
                        json.put("MobileFormId", MobileFormId);
                        json.put("DeviceId", eng.getDeviceId(context));
                        json.put("SessionId", P5LifeCycle.getP5Session());
                        json.put("ScreenName", ScreenName);
                        json.put("FormResponses", getResponses);
                        json.put("BannerView", 0);
                        json.put("BannerClick", click);
                        json.put("BannerClose", close);
                        json.put("ButtonName", getName);
                        json.put("SendReport", SendReport);
                        json.put("WidgetName", WidgetName);
                        json.put("WorkFlowDataId",0);
                        json.put("P5UniqueId","");

                    } catch (JSONException e) {
                        Log.e(TAG, " DialogBox push details failed");
                        e.printStackTrace();
                        Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                    }

                    String fresult = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                    //Log.d(TAG, "something goes wrong"+fresult);
                    TextView p5textView = new TextView(context);
                  //  new P5HttpRequest(context, ServiceUrl + context.getResources().getString(R.string.FORM_RESPONSES), fresult).execute();
                    new P5LifeCycle().callPushSend(context, new ObjectMapper().readValue(fresult, HashMap.class));
                }


                if (getMsg.length() != 0) {
                    Toast.makeText(context, getMsg, Toast.LENGTH_LONG).show();
                }

                if (getCloseDialog.equals("1")) {
                    P5dialogdismiss();
                }
            }


        } catch (Throwable e) {
            Log.d(TAG, "something goes wrong");
            Log.e(TAG, " DialogBox push details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
        }

    }


    private boolean checkCallPermission(Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ;
            if (activity.checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                Log.d(TAG, "location provider requires CALL_PHONE permission.");
                return false;
            }

        }
        return true;
    }


    public int btnFormSubmit() {
        int fresult = 0;
        String finalResponses = "";
        int loop = 0, gCount = 0, Group = 0;
        int ChkValidation = -1, EmailValid = 0;
        String sCheckValue = "";
        try {
            for (int i = 0; i < fieldsList.length(); ++i) {
                JSONObject field = fieldsList.getJSONObject(i);
                String fType = field.getString("Type");
                String fMandatory = field.getString("Mandatory");
                String mValue = "";

                //Log.d("p5", "wrong intent." + allFieldsName.get(i));


                if (fType.equals("Et") || fType.equals("Cal")) {
                    EditText chket = (EditText) allFields.get(i);
                    mValue = chket.getText().toString().replace("^", "").replace("^", "~");

                    if (fMandatory.equals("1") && mValue.length() == 0) {
                        ChkValidation = i;
                    } else {
                        loop = loop + 1;
                        finalData.put("field" + loop, mValue);
                        finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + mValue;
                    }
                    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                    if (mValue.matches(emailPattern) == false && allFieldsName.get(i).toString().indexOf("#1$") > 0) {
                        EmailValid = -1;
                    }
                    //Log.d("p5", EmailValid+"wrong intent." + allFieldsName.get(i).toString().indexOf("#1$")+mValue.matches(emailPattern));

                } else if (fType.equals("Cb")) {

                    gCount = gCount + 1;
                    Group = Integer.parseInt(field.getString("Group"));

                    CheckBox chkcb = (CheckBox) allFields.get(i);
                    if (chkcb.isChecked()) {
                        mValue = chkcb.getText().toString();
                        sCheckValue = sCheckValue + "," + mValue;
                    }

                    if (Group == gCount) {
                        if (fMandatory.equals("1") && sCheckValue.length() == 0) {
                            ChkValidation = i;
                        } else {
                            loop = loop + 1;
                            if (sCheckValue.length() != 0) {
                                finalData.put("field" + loop, sCheckValue.substring(1, sCheckValue.length()));
                                finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + sCheckValue.substring(1, sCheckValue.length());
                            } else {
                                finalData.put("field" + loop, sCheckValue.toString());
                                finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + sCheckValue.toString();
                            }
                        }

                        gCount = 0;
                    }
                } else if (fType.equals("Rg")) {
                    RadioGroup chkrg = (RadioGroup) allFields.get(i);
                    int id = chkrg.getCheckedRadioButtonId();
                    RadioButton chkrb = (RadioButton) chkrg.findViewById(id);
                    mValue = chkrb.getText().toString();
                    if (fMandatory.equals("1") && mValue.length() == 0) {
                        ChkValidation = i;
                    } else {
                        loop = loop + 1;
                        finalData.put("field" + loop, mValue);
                        finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + mValue;
                    }
                } else if (fType.equals("Sp")) {
                    Spinner chksp = (Spinner) allFields.get(i);
                    mValue = chksp.getSelectedItem().toString();
                    if (fMandatory.equals("1") && mValue.length() == 0) {
                        ChkValidation = i;
                    } else {
                        loop = loop + 1;
                        finalData.put("field" + loop, mValue);
                        finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + mValue;
                    }
                } else if (fType.equals("Rb")) {
                    RatingBar chkrb = (RatingBar) allFields.get(i);
                    mValue = String.valueOf(chkrb.getRating());
                    if (fMandatory.equals("1") && mValue.equals("0.0")) {
                        ChkValidation = i;
                    } else {
                        loop = loop + 1;
                        finalData.put("field" + loop, mValue);
                        finalResponses = finalResponses + " ^ " + allFieldsName.get(i) + "~" + mValue;
                    }
                }

            }

            if (EmailValid == -1) {
                Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();
            } else if (ChkValidation == -1) {
                //Log.d("p5data", finalData.toString());


                JSONObject json = new JSONObject();
                try {


                    json.put("AppKey", eng.p5GetAppKey());
                    json.put("MobileFormId", MobileFormId);
                    json.put("DeviceId", eng.getDeviceId(context));
                    json.put("SessionId", P5LifeCycle.getP5Session());
                    json.put("ScreenName", ScreenName);
                    json.put("FormResponses", finalResponses.substring(2, finalResponses.length()));
                    json.put("BannerView", 0);
                    json.put("BannerClick", 1);
                    json.put("BannerClose", 0);
                    json.put("SendReport", SendReport);
                    json.put("WidgetName", WidgetName);
                    json.put("WorkFlowDataId",0);
                    json.put("P5UniqueId","");
                } catch (JSONException e) {
                    Log.e(TAG, " DialogBox push details failed");
                    e.printStackTrace();
                    Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                }

                String result = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                //Log.d("p5data", result.toString());

                TextView p5textView = new TextView(context);
              //  new P5HttpRequest(context, ServiceUrl +context.getResources().getString(R.string.FORM_RESPONSES), result).execute();
                try {
                    new P5LifeCycle().callPushSend(context, new ObjectMapper().readValue(result, HashMap.class));
                } catch (IOException e) {
                    Log.e(TAG, " DialogBox push details failed");
                    e.printStackTrace();
                    Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
                }
                fresult = 1;

            } else
            {
                Toast.makeText(context, "Please fill the mandatory fields", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Something goes wrong", Toast.LENGTH_SHORT).show();
            Log.e(TAG, " DialogBox push details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
        }
        return fresult;
    }
}