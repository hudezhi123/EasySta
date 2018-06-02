package easyway.Mobile.Task;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.Data.GetTaskActorResult;
import easyway.Mobile.Data.GetTaskActorResult;
import easyway.Mobile.R;

public class SecureTipsInfoActivity extends Activity {
    private ListView listviewTips;
    private List<GetTaskActorResult.DataBean> tipsList;
    private SecureTipsAdapter adapter;
    private TextView tipsTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_tips_info);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.secure_tips);
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        List<GetTaskActorResult.DataBean> dataBeanList = null;
        if (bundle != null) {
            String resultJson = (String) bundle.getSerializable("GetTaskActorResult");
            GetTaskActorResult getTaskActorResult = new Gson().fromJson(resultJson, GetTaskActorResult.class);
            if (getTaskActorResult != null && getTaskActorResult.getData() != null && getTaskActorResult.getData().size() > 0) {
                dataBeanList = getTaskActorResult.getData();
                GetTaskActorResult.DataBean dataBean = null;
                for (int i = 0; i < dataBeanList.size(); i++) {
                    dataBean = dataBeanList.get(i);
                    tipsList.add(dataBean);
                }
            }
        }
        return;
    }

    private void initView() {
        tipsTips = (TextView) findViewById(R.id.text_tips_tips);
        listviewTips = (ListView) findViewById(R.id.listView_secure_tips);
        if (tipsList.size() > 0) {
            adapter = new SecureTipsAdapter(this, tipsList);
            listviewTips.setAdapter(adapter);
            tipsTips.setVisibility(View.GONE);
        } else {
            listviewTips.setVisibility(View.GONE);
            tipsTips.setVisibility(View.VISIBLE);
        }

    }

    private void initList() {
        tipsList = new ArrayList<GetTaskActorResult.DataBean>();
    }

    private void init() {
        initList();
        getData();
        initView();
    }
}
