package easyway.Mobile.util;

import java.util.Calendar;

import easyway.Mobile.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateLine extends RelativeLayout implements View.OnClickListener {
    private Button btnDateNext;
    private Button btnDatePrevious;
    private TextView txtDate;
    private int weekday;
    private int year;    // 年
    private int month;    // 月
    private int day;        // 日
    private String date = "";
    private Context context;
    private LayoutInflater mInflater;
    private IDateLineListener listener;

    private final int FLAG_DATE_NORMAL = 0; // 当天
    private final int FLAG_DATE_NEXT = 1; // 后一天
    private final int FLAG_DATE_PREVIOUS = 2; // 前一天

    /**
     * 0 - 表示当天
     * -1 - 表示前一天
     * 1 - 表示后一天
     * ...
     * 并以此类推
     */
    public static int FLAG_DATE = 0;

    public String getWeekday() {
        String WeekDay = "";
        switch (weekday) {
            case 1:
                WeekDay += "星期日";
                break;
            case 2:
                WeekDay += "星期一";
                break;
            case 3:
                WeekDay += "星期二";
                break;
            case 4:
                WeekDay += "星期三";
                break;
            case 5:
                WeekDay += "星期四";
                break;
            case 6:
                WeekDay += "星期五";
                break;
            case 7:
                WeekDay += "星期六";
                break;
        }
        return WeekDay;
    }

    public boolean isCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = null;
        if (year == yy && month == mm && day == dd) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isYesterday() {
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = null;
        if (year == yy && month == mm && day == dd - 1) {
            return true;
        } else {
            return false;
        }
    }

    public DateLine(Context context) {
        super(context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public DateLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void setListener(IDateLineListener iDateLineListener) {
        listener = iDateLineListener;
    }

    private void init() {
        View Temp = mInflater.inflate(R.layout.dateline, this);

        txtDate = (TextView) Temp.findViewById(R.id.txtDate);
        txtDate.setOnClickListener(this);

        btnDateNext = (Button) Temp.findViewById(R.id.btnDateNext);
        btnDateNext.setOnClickListener(this);

        btnDatePrevious = (Button) Temp.findViewById(R.id.btnDatePrevious);
        btnDatePrevious.setOnClickListener(this);

        setDate(FLAG_DATE_NORMAL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtDate:
                new DatePickerDialog(context, DateSet, year, month, day).show();
                break;
            case R.id.btnDateNext:
                FLAG_DATE++;
                setDate(FLAG_DATE_NEXT);
                if (listener != null)
                    listener.DateChange();
                break;
            case R.id.btnDatePrevious:
                FLAG_DATE--;
                setDate(FLAG_DATE_PREVIOUS);
                if (listener != null)
                    listener.DateChange();
                break;
            default:
                break;
        }
    }


    // 设置日期控件
    DatePickerDialog.OnDateSetListener DateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearOfDate, int monthOfYear,
                              int dayOfMonth) {
            year = yearOfDate;
            month = monthOfYear;
            day = dayOfMonth;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            weekday = calendar.get(Calendar.DAY_OF_WEEK);
            calendar = null;
            date = new StringBuilder()
                    .append(year)
                    .append("-")
                    .append((month + 1) < 10 ? "0" + (month + 1)
                            : (month + 1)).append("-")
                    .append((day < 10) ? "0" + day : day).toString();
            txtDate.setText(date);

            if (listener != null)
                listener.DateChange();
        }
    };

    // 设置日期
    private void setDate(int flag) {
        switch (flag) {
            case FLAG_DATE_NORMAL:
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                weekday = c.get(Calendar.DAY_OF_WEEK);
                date = new StringBuilder()
                        .append(year)
                        .append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1)
                                : (month + 1)).append("-")
                        .append((day < 10) ? "0" + day : day).toString();
                txtDate.setText(date);
                break;
            case FLAG_DATE_NEXT:
                date = CommonUtils.GetNextDate(date);
                weekday = (weekday + 1) % 7;
                if (date != null) {
                    String[] strs = date.split("-");
                    if (strs != null && strs.length == 3) {
                        year = Integer.parseInt(strs[0]);
                        month = Integer.parseInt(strs[1]) - 1; // month starts form 0
                        day = Integer.parseInt(strs[2]);
                    }
                }
                txtDate.setText(date);
                break;
            case FLAG_DATE_PREVIOUS:
                date = CommonUtils.GetPreviousDate(date);
                weekday = (weekday - 1) % 7;
                if (date != null) {
                    String[] strs = date.split("-");
                    if (strs != null && strs.length == 3) {
                        year = Integer.parseInt(strs[0]);
                        month = Integer.parseInt(strs[1]) - 1; // month starts form 0
                        day = Integer.parseInt(strs[2]);
                    }
                }

                txtDate.setText(date);
                break;
        }
    }

    public String getDate() {
        if (date == null)
            setDate(FLAG_DATE_NORMAL);
        return date;
    }
}
