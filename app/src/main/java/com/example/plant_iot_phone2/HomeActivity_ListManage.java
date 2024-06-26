package com.example.plant_iot_phone2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeActivity_ListManage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeActivity_ListManage extends Fragment {
    // 모델 추가.
    LinearLayout button_add;
    // 모델 삭제.
    ImageView button_delete;
    // 사용자 정보.
    ImageView button_user;

    // ListView.
    ListView list;
    Model_ListAdapter listAdapter;
    ArrayList<Model_ListItem> listItems;
    GetList gList;
    String getListURL = "http://hosting.ajplants.com/Plant_modelListG_Android.php";

    String id = "";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeActivity_ListManage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeActivity_ListManage.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeActivity_ListManage newInstance(String param1, String param2) {
        HomeActivity_ListManage fragment = new HomeActivity_ListManage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_list_manage, container, false);

        id = ((HomeActivity)HomeActivity.mContext).id;

        // 모델 추가.
        button_add = (LinearLayout) v.findViewById(R.id.button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BluetoothActivity.class);
                intent.putExtra("purpose", "add");
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        // 모델 삭제.
        button_delete = (ImageView) v.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                HomeActivity_ListDelete listDelete = new HomeActivity_ListDelete();
                transaction.replace(R.id.listFrame, listDelete);
                transaction.commit();
            }
        });

        // 사용자 정보.
        button_user = (ImageView) v.findViewById(R.id.button_user);
        button_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity_UserMenu.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //ListView.
        list = (ListView) v.findViewById(R.id.plantList);
        listItems = new ArrayList<Model_ListItem>();
        listAdapter = new Model_ListAdapter(getContext(), listItems);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new listItemClickListener());

        gList = new GetList();
        gList.execute(getListURL);

        return v;
    }

    // ListView 아이템 클릭 시 이벤트.
    public class listItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("id", listItems.get(position).getId());
            intent.putExtra("name", listItems.get(position).getName());
            intent.putExtra("model", listItems.get(position).getModel());
            // get model test.
            //Toast.makeText(getContext(), listItems.get(position).getModel(), Toast.LENGTH_SHORT).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    // 리스트 얻어오기.
    class GetList extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "id=" + ((HomeActivity)HomeActivity.mContext).id;

            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) phpUrl.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            String TAG_JSON = "aj3dlab";
            String model = "", name = "";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    model = item.getString("model");
                    name = item.getString("name");

                    listItems.add(new Model_ListItem(name, model, id));
                    listAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}