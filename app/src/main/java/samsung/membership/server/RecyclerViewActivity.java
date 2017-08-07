package samsung.membership.server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by yumin on 2017-08-05.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyData> dataSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dataSet = new ArrayList<>();
        adapter = new RecyclerViewAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        dataSet.add(new MyData("#InsideOut", R.mipmap.ic_launcher));
        dataSet.add(new MyData("#Mini", R.mipmap.ic_launcher));
        dataSet.add(new MyData("#ToyStroy", R.mipmap.ic_launcher));

    }
}
