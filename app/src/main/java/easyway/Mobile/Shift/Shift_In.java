package easyway.Mobile.Shift;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;

public class Shift_In extends ActivityEx
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_in);

        TextView labTitle = (TextView) findViewById(R.id.title);
        labTitle.setText(R.string.Shift_In);

        Button btnShiftIn = (Button) findViewById(R.id.btnShiftIn);
        btnShiftIn.setOnClickListener(shiftInLis());

        Button btnShiftInSign = (Button) findViewById(R.id.btnShiftInSign);
        btnShiftInSign.setOnClickListener(ShiftInSignLis());
    }

    private OnClickListener shiftInLis()
    {
        return new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(Shift_In.this, Shift_In_Pool.class);
                startActivityForResult(intent, 1);
            }
        };
    }

    private OnClickListener ShiftInSignLis()
    {
        return new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(Shift_In.this, Shift_In_SignIn.class);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == 2)
        {
            /*
             * Bundle bundle = null;
             * if(data!=null&&(bundle=data.getExtras())!=null){
             * //this.Shift_Id=bundle.getLong("Shift_Id"); }
             */
        }
    }
}
