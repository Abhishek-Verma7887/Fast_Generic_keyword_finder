package com.example.Fast_Generic_keyword_finder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;


import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.content.ContentValues.TAG;
import static java.lang.Character.isLetter;
import static java.lang.Character.isLowerCase;



public class MainActivity extends AppCompatActivity{
    //Realm
    Realm realm;
    ArrayList<Keyword_schema> keywordList;
    //Realm
    private EditText url_n;
    private ProgressBar prepro;
    private Button go_butt,button_rebuild;
    private Button url_butt;
    private ImageView Porn_signal;
    private WebView webView;
    private TextView text_desc;
    private TextView expected_class;
    public static int SDK_INT = android.os.Build.VERSION.SDK_INT;
    private String porn_keywords[]={"abhishek","bbc","orgasm","nude","naked","dildo","threesome","bitch","gangbang","xxx","cumshot","cum","blowjob","bimbo",
            "squirt","ebony","tits","busty","whore","slut","cunt","anal", "gay","fuck","lesbian","porn","porno","sex","sexy","boobs","pussy","dick",
            "handjob","fingering","booty","creampie","butt","chick","milf","cougar","cuckold","deepthroat","hentai","doggystyle","milfy","bondage",
            "bbw","escort","erotic","incest","hotmom","orgy","puba","stepmom","stepsis","spanking","sissy","shemale","taboo","virgin","cfnm","cmnf"};
    HashMap<Integer,String> index_2_string=new HashMap<Integer,String>();
    Set<String> found_porn_words = new HashSet<String>();
    Set<Integer> found_porn_words_index=new HashSet<Integer>();
    Integer Total_keywords=62;
    Integer total_aggregate_percent=0;

    int K = 26;

    public class vertex{
        int[] next=new int[K];
        Boolean leaf=false;
        int p=-1;
        char pch;
        int green_link=-1;
        int percent=0;
        int link=-1;
        int[] go=new int[K];
        public vertex(int pp,char ch){
            p=pp; pch=ch;
            for(int i=0;i<K;i++){
                next[i]=-1; go[i]=-1;
            }
        }
    };

    ArrayList<vertex> tree=new ArrayList<>();

    void add_string(String s,Integer percent){
        int v=0;
        for(int i=0;i<s.length();i++){
            int c=s.charAt(i)-'a';
            if(c<0){
                Log.e(TAG, String.valueOf(s.charAt(i))+String.valueOf(c));  return;}
            if(tree.get(v).next[c]==-1){
                tree.get(v).next[c]=tree.size(); vertex tmp=new vertex(v,s.charAt(i));
                tree.add(tmp);
            }
            v=tree.get(v).next[c];
        }
        tree.get(v).percent=percent;
        index_2_string.put(v,s);
        tree.get(v).leaf=true;
    }

    int get_link(int v){
        if(tree.get(v).link==-1){
            if(v==0||tree.get(v).p==0){
                tree.get(v).link=0;
            }else{
                tree.get(v).link=go(get_link(tree.get(v).p),tree.get(v).pch);
            }
        }
        return tree.get(v).link;
    }

    int go(int v,int ch){
        int c=ch-'a';

        if(tree.get(v).go[c]==-1){
            if(tree.get(v).next[c]!=-1){
                tree.get(v).go[c]=tree.get(v).next[c];
            }else{
                if(v==0){ tree.get(v).go[c]=0; }else{
                    tree.get(v).go[c]=go(get_link(v),ch);
                }
            }
        }
        return tree.get(v).go[c];
    }

    void initialise_suffix(){
        Queue<Integer> q = new LinkedList<>(); q.add(0);
        while (!q.isEmpty()){
            int cur=q.remove(); get_link(cur);
            int ct=0;
            for(int i=0;i<26;i++){
                if(tree.get(cur).next[i]!=-1){ q.add(tree.get(cur).next[i]);  }
                go(cur,i+'a');
            }

            if(tree.get(cur).link>0){
                if(tree.get(tree.get(cur).link).leaf){ tree.get(cur).green_link=tree.get(cur).link; }
                else{
                    tree.get(cur).green_link=tree.get(tree.get(cur).link).green_link;
                }
            }

        }
    }
    int last_space_pos=-1;
    String porn_hai="";
    void print_green_links(int v,int cur_pos,Boolean agla){
        if(v>0){
            if(tree.get(v).leaf){
                String cur_match=index_2_string.get(v);
                 //porn_hai=porn_hai+" "+index_2_string.get(v);
                if(cur_match.length()==(cur_pos-last_space_pos)&&agla){ //LESS strict checking no substring check
                    found_porn_words.add(cur_match);
                    found_porn_words_index.add(v);
                }
            }
            print_green_links(tree.get(v).green_link,cur_pos,agla);
        }
    }

    void occurence_print(String str){
        int v=0;
        last_space_pos=-1;
        for(int c=0;c<str.length();c++){
            Boolean agla=false;
            int i=str.charAt(c)-'a';
            if(str.charAt(c)==' '){ last_space_pos=c;}
            if(c+1<str.length()){ if(str.charAt(c+1)==' '){agla=true;} }else{ agla=true;}

            //  Log.e("TAG3",String.valueOf("DEKH "+str.charAt(c))+" "+String.valueOf(i)+" "+String.valueOf(v));
            if(i<0||i>25||v<0){ Log.e("TAG2",String.valueOf(str.charAt(c))+" "+String.valueOf(i)+" "+String.valueOf(v)); v=0; continue;}
            print_green_links(tree.get(v).go[i],c,agla); v=tree.get(v).go[i];
        }
    }


    boolean error_thi;
    String filtered_FULL_page_doc="";
    String Total_errors="";

    final Boolean[] load_again_webview = {true};

    public MainActivity(){
        vertex top= new vertex(-1,'$');
        tree.add(top);
        for(String str:porn_keywords){
            add_string(str,0);
        }
        initialise_suffix();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Simple_browser);
        setContentView(R.layout.activity_main);
        url_n=(EditText) findViewById(R.id.textInputEditText);
        go_butt=(Button) findViewById(R.id.button);
        button_rebuild=(Button)findViewById(R.id.button_rebuild);
        text_desc=(TextView)findViewById(R.id.textView3);
        text_desc.setMovementMethod(new ScrollingMovementMethod());
        webView = (WebView) findViewById(R.id.webView);
        expected_class=(TextView) findViewById(R.id.textView);
        expected_class.setMovementMethod(new ScrollingMovementMethod());
        url_butt=(Button)findViewById(R.id.button2);
        prepro=(ProgressBar)findViewById(R.id.progressBar);
        prepro.setVisibility(View.INVISIBLE);
        Porn_signal=(ImageView)findViewById(R.id.imageView);

        //Realm2
        //SETUP REEALM
        Realm.init(this);
        RealmConfiguration config=new RealmConfiguration.Builder().schemaVersion(1).migration(new Migration()).build();
        realm=Realm.getInstance(config);
        RealmHelper helper=new RealmHelper(realm);
        //Realm2


        webView.setWebViewClient(new myWebViewClient());

        button_rebuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_rebuild.setClickable(false);
                url_butt.setClickable(false);
                go_butt.setClickable(false);
                try{
                    switch (view.getId()) {
                        case R.id.button_rebuild:
                            new RebuildTask().execute(10);
                            break;
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                button_rebuild.setClickable(true);
                url_butt.setClickable(true);
                go_butt.setClickable(true);
            }
        });

        Porn_signal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent automanton = new Intent(MainActivity.this, Automanton_building.class);
                startActivity(automanton);
            }
        });


        go_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Total_errors="";

                try {
                    switch (view.getId()) {
                        case R.id.button:
                            new MyTask().execute(10);
                            break;
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }

                String url=url_n.getText().toString();

                Boolean isUrl=false;
                for(int i=0;i<url.length();i++){
                    if(url.charAt(i)=='.'){ isUrl=true;}
                }

                if(!isUrl){
                    url = "www.google.com/search?q="+url.replace(" ", "%20");
                }





                if(load_again_webview[0]) {
                    webView.loadUrl(url);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                }
                //rebuilding button set clicability to true
                button_rebuild.setClickable(true);
                go_butt.setClickable(true);
                load_again_webview[0] =true;

            }
        });

        url_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    go_butt.setClickable(false);
                    button_rebuild.setClickable(false);
                    //rebuilding button set clicability to false
                    String cur_url = webView.getUrl().toString();
                    String[] arrStr = cur_url.split("//", 0);
                    Total_errors="";
                    url_n.setText(arrStr[1]);
                    load_again_webview[0]=false;
                    go_butt.performClick();
                }catch (Exception ex){
                    text_desc.setText(" First load the webpage correctly then  fetch current url\n"+ex.getMessage().toString());
                    //rebuilding button set clicability to true
                    go_butt.setClickable(true);
                    button_rebuild.setClickable(true);
                }
            }
        });

    }

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {


            String url=url_n.getText().toString();


            Document doc=null;
            String userAgent = System.getProperty("http.agent");
            String FULL_page_doc="";


            try {
                if (SDK_INT >= 10) {
                    StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
                    StrictMode.setThreadPolicy(tp);
                }
                String nrl="http://"+url;
                doc = Jsoup.connect(nrl).userAgent(userAgent).get();
            } catch (IOException e) {
                Total_errors=Total_errors+"\na: "+e.getMessage().toString();
                try{
                    if (SDK_INT >= 10) {
                        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
                        StrictMode.setThreadPolicy(tp);
                    }
                    String nrl="https://"+url;
                    doc = Jsoup.connect(nrl).userAgent(userAgent).get();
                }catch(Exception ex){
                    Total_errors=Total_errors+"\nb: "+e.getMessage().toString();
                }
                e.printStackTrace();
            }
            String str1="";
            error_thi=false;
            try{
                str1=str1+ doc.select("meta[name=description]").get(0)
                        .attr("content").toString();
                FULL_page_doc=doc.body().text();
            }
            catch(Exception e1) {
                try {
                    str1 = doc.body().text();
                    int len=str1.length();
                    str1= str1.substring(Math.min(len,50),Math.min(10050,len));
                    FULL_page_doc=doc.body().text();
                } catch (Exception ex) {
                    // Toast.makeText(getApplicationContext(), "1: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    Total_errors=Total_errors+"\nc: "+ex.getMessage().toString();
                    error_thi = true;

                }
            }


            FULL_page_doc=FULL_page_doc.toLowerCase();
            int len_FULL=FULL_page_doc.length();
            FULL_page_doc=FULL_page_doc.substring(Math.min(len_FULL,0),Math.min(100000,len_FULL));


            filtered_FULL_page_doc=FULL_page_doc;
            for(int i=0;i<FULL_page_doc.length();i++){
                if(isLowerCase(FULL_page_doc.charAt(i))||FULL_page_doc.charAt(i)==' ') filtered_FULL_page_doc=filtered_FULL_page_doc+FULL_page_doc.charAt(i);
            }

            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            occurence_print(filtered_FULL_page_doc);
            if(!found_porn_words.isEmpty()){
                Porn_signal.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.red_button_2));
                expected_class.setText("PORN CONTENT FOUND");
            }else{
                Porn_signal.setImageDrawable(getApplicationContext().getDrawable(R.mipmap.green_button));
                expected_class.setText("NO PORN CONTENT 0%");
            }
            for(String s:found_porn_words){
                porn_hai=porn_hai+" "+s;
            }

            Double Aggreagte_percent=0.0;
            Double total_percent=0.0;
            for(Integer v:found_porn_words_index){
                total_percent+=tree.get(v).percent;
            }
            DecimalFormat df = new DecimalFormat("##.###");
            Aggreagte_percent=(total_percent/(Double)(0.00001+found_porn_words.size()));
            Double word_match_percent=0.0;
            word_match_percent= (found_porn_words.size()*100/ new Double(0.000001+Total_keywords));
            text_desc.setText("Word Match Percent: "+String.valueOf(df.format(word_match_percent))+
                    "\nWeighted Percent: "+String.valueOf(df.format(Aggreagte_percent))+"\nPorn_keywords: "+porn_hai);

            found_porn_words.clear();
            found_porn_words_index.clear();

            //expected_class.setBackgroundDrawable(getApplicationContext().getDrawable(R.mipmap.green_button));
            if(error_thi){
                text_desc.setText("There is some error in loading the page or getting inference on text.\nErrors:"+Total_errors);
                Toast.makeText(getApplicationContext(),"There is some error in loading the page or getting inference on text",Toast.LENGTH_LONG).show();

            }
            prepro.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPreExecute() {
            prepro.setVisibility(View.VISIBLE);
            Total_errors="";
            porn_hai="";
            last_space_pos=-1;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            try{
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "progress error", Toast.LENGTH_LONG).show();
            }
        }
    }

    class RebuildTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {


            try {
                //RETRIEVE
                RealmConfiguration config=new RealmConfiguration.Builder().schemaVersion(1).migration(new Migration()).build();
                realm=Realm.getInstance(config);
                RealmHelper helper=new RealmHelper(realm);
                keywordList=helper.retrieve_only_SET();
                tree.clear();
                vertex top = new vertex(-1, '$');
                tree.add(top);
                for (Keyword_schema K : keywordList) {
                    add_string(K.getName(), Integer.valueOf(K.getPercent()));
                    Total_errors=Total_errors+K.getName();
                }
                Total_keywords=keywordList.size();
                initialise_suffix();
            }catch (Exception e){
                error_thi=true;
                Total_errors=Total_errors+e.getMessage();
            }


            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            if(error_thi)Toast.makeText(getApplicationContext(), "in reini"+Total_errors, Toast.LENGTH_LONG).show();
            prepro.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPreExecute() {
            prepro.setVisibility(View.VISIBLE);
            Total_errors="";
            error_thi=true;

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            try{
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "progress error", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private class myWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                view.loadUrl(url);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return true;
        }
    }

}
