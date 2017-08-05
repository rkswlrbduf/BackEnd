package samsung.membership.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//중요한부분이다 이부분으로 사용하는데 아래와 같이 다른 클래스를 써주어도 무방하다.
import org.apache.commons.net.ftp.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
//아직 많은기능을안써서 그런지 아직은 두개의 차이가없다;;나는 oroinc으로 써야지 안되면 다른걸로 바꾸고,,,,


import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    private String server = "172.24.1.1";
    private int port = 21;
    private String id = "kyuyeol";
    private String password = "password";
    private ImageView image;
    private GridView gv;

    FTPUtility ftpUtility;
    FTPClient ftpClient;
    FTPFile ftpFile[] = null;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    int posX1 = 0, posX2 = 0, posY1 = 0, posY2 = 0;
    float oldDist = 1f;
    float newDist = 1f;

    private int count;

    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private File outputFile; //파일명까지 포함한 경로
    private File path;//디렉토리경로

    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }


    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        gv = (GridView)findViewById(R.id.gridView1);
        gv.setOnTouchListener(this);

        try {
            ftpUtility = new FTPUtility("172.24.1.1", 21, "kyuyeol", "password");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "ERROR");
        }



        /*try {
            ftpFile = ftpUtility.ftpClient.listFiles();
        } catch (Exception e) {

        }
        Log.d("TAG", ftpFile.length + "");
        for(int i=0;i<ftpFile.length;i++) {
            Log.d("TAG", ftpFile[i].toString());
        }*/


        new DownloadFilesTask(MainActivity.this).execute();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                posX1 = (int) event.getX();
                posY1 = (int) event.getY();
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    posX2 = (int) event.getX();
                    posY2 = (int) event.getY();

                    if (Math.abs(posX2 - posX1) > 20 || Math.abs(posY2 - posY1) > 20) {
                        posX1 = posX2;
                        posY1 = posY2;
                    }
                } else if (mode == ZOOM) {
                    newDist = spacing(event);
                    if (newDist - oldDist > 100) {
                        oldDist = newDist;
                        MainActivity.this.gv.setNumColumns(MainActivity.this.gv.getNumColumns()-1);
                    }
                    else if (oldDist - newDist > 100) {
                        oldDist = newDist;
                        MainActivity.this.gv.setNumColumns(MainActivity.this.gv.getNumColumns()+1);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                newDist = spacing(event);
                oldDist = spacing(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return false;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!readAccepted || !writeAccepted) {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    private class DownloadFilesTask extends AsyncTask<Void, String, Void> {

        private ProgressDialog progressDialog;
        private Context context;
        private MyAdapter adapter;

        protected DownloadFilesTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Start");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ftpUtility.connect();
            ftpUtility.login();
            ftpUtility.cd("/home/pi/raspicam_example_with_opencv/capture");
            publishProgress("max", Integer.toString(ftpUtility.list().length));
            OutputStream output = null;
            adapter = new MyAdapter(getApplicationContext(),R.layout.row,ftpUtility.list().length);
            File f;
            /*try {
                FTPFile ftpfile[] = ftpUtility.list();
                Log.d("TAG",ftpfile.length + "");
                for(int i=0;i<ftpfile.length;i++) {
                    Log.d("NAME" , ftpfile[i].getName());
                    f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),ftpfile[i].getName());
                    output = new FileOutputStream(f);
                    Log.d("TAG_NAME", ftpfile[i].getName());
                    ftpUtility.pwd();
                    ftpUtility.ftpClient.retrieveFile(ftpfile[i].getName(),output);
                    output.close();
                    publishProgress("progress", Integer.toString(i), "작업 번호 " + Integer.toString(i) +  "번 수행중");
                }
                //File local = new File(source);
                //output = new FileOutputStream(local);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            Log.d("TAG", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            ftpUtility.logout();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0].equals("progress")) {
                progressDialog.setProgress(Integer.parseInt(values[1]));
                progressDialog.setMessage(values[2]);
            } else if(values[0].equals("max")) {
                progressDialog.setMax(Integer.parseInt(values[1]));
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //image.setImageURI(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/2017-07-30 22: 38: 45.jpg"));
            gv.setAdapter(adapter);
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

}

class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    int img[];
    int count;
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, int count) {
        this.context = context;
        this.layout = layout;
        this.count = count;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null)
            imageView = new ImageView(context);
        else
            imageView = (ImageView) convertView;

        imageView.setAdjustViewBounds(true);

        Log.d("TAG", "http://172.24.1.1/" + Integer.toString(position + 1) + ".jpg");

        Picasso.with(context).load("http://172.24.1.1/" + Integer.toString(position + 1) + ".jpg")
                .transform(PicassoTransformations.resizeTransformation)
                .into(imageView);
        return imageView;
    }
}