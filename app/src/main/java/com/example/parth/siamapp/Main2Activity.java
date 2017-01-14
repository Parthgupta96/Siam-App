package com.example.parth.siamapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity {
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
    }

    private void init() {
    button = (Button)findViewById(R.id.demo_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseJson();
            }
        });
    }

    private void parseJson() {
        String response = "{\"data\":[{\"description\":\"Want to be a part of a reputed international society ? Have a natural inclination towards mathematics?  Society for Industrial and  Applied mathematics(SIAM), DTU is where you belong.  Know more about our agenda,  past events and upcoming events at our Orientation'16. Dont miss this opportunity to meet your seniors and faculty of MCE in this Interactive session.  Date: SEPTEMBER 14,2016 Time:5pm Venue:IW-FF-16\",\"name\":\"Orientation'16\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2016-09-14T17:00:00+0530\",\"id\":\"308596166167539\"},{\"end_time\":\"2016-09-14T03:00:00+0530\",\"name\":\"Bid To Win\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.74994722,\"longitude\":77.11702778,\"street\":\"New Delhi\"},\"id\":\"111943728824106\"},\"start_time\":\"2016-09-09T11:00:00+0530\",\"id\":\"1067983659905286\"},{\"description\":\"We are pleased to invite you to Evolute 2016, an annual symposium held by Society of Industrial and Applied Mathematics (SIAM), DTU Chapter. It is an event where renowned speakers such as doyens of the industry, distinguished professors  come together on a common platform to enlighten students with their ideas.\",\"name\":\"Evolute 2016\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.7454981,\"longitude\":77.11784106,\"street\":\"Shahbad Daulatpur, Main Bawana Road\",\"zip\":\"110042\"},\"id\":\"150938474997475\"},\"start_time\":\"2016-04-29T10:00:00+0530\",\"id\":\"1768830303351157\"},{\"description\":\"Stratazenith is a 2 day event consisting of 'War of Wits', an on-spot Strategy based quizzing cum bidding competition, and 'Mutiny', which is an interactive online multiplayer game. It is organised by The Indian Game Theory Society along with SIAM  DTU and The KGTS, IIT Kharagpur.\",\"end_time\":\"2016-01-23T19:00:00+0530\",\"name\":\"Stratazenith\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.7454981,\"longitude\":77.11784106,\"street\":\"Shahbad Daulatpur, Main Bawana Road\",\"zip\":\"110042\"},\"id\":\"150938474997475\"},\"start_time\":\"2016-01-22T17:00:00+0530\",\"id\":\"538204666345599\"},{\"name\":\"Evolute: Third Annual Symposium\",\"place\":{\"name\":\"Senate Hall, Admin Block, Delhi Technological University\"},\"start_time\":\"2015-04-11T10:00:00+0530\",\"id\":\"931381006892331\"},{\"description\":\"SIAM DTU invites innovative research and review papers in MATHEMATICS and STATISTICS and their applications\",\"name\":\"EVOLUTE '15:PAPER PRESENTATION\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2015-03-26T00:00:00+0530\",\"id\":\"1593974044173109\"},{\"description\":\"To enlighten the students with a deep insight into topic of their interests,SIAM DTU  takes a sneak peek into the depths of mathematics and its various applications\",\"name\":\"PROGETTO : CASE STUDY EVENT\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2015-03-26T00:00:00+0530\",\"id\":\"1562358640709400\"},{\"description\":\"SIAM DTU in association with PRINCETON REVIEW gives you a chance to test your verbal skills with free mock test\",\"name\":\"GRE MOCK TEST\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2015-03-25T17:00:00+0530\",\"id\":\"728545017243619\"},{\"description\":\"SIAM invites you to participate in free exciting fun and tech events. Treat awaits winners. Disclaimer for Freshers of 2014, it's not advisable to miss the golden opportunity\",\"end_time\":\"2014-09-09T16:30:00+0530\",\"name\":\"SIAM-DTU Events 2014\",\"place\":{\"name\":\"Raised platform of civil block, infront of Mech C\"},\"start_time\":\"2014-09-09T11:00:00+0530\",\"id\":\"614762165310501\"},{\"description\":\"Come to See Us, Meet Us and Join Us. Face-to-face interaction to know who we are, what we do and our vision. Don't miss the golden opportunity ;)\",\"name\":\"SIAM - DTU Orientation Session 2014\",\"place\":{\"name\":\"Exposition Hall, Ground floor, Admin Block, Delhi Technological University, Delhi\"},\"start_time\":\"2014-08-26T17:00:00+0530\",\"id\":\"1466076523672528\"},{\"description\":\"Society for Industrial and Applied Mathematics- DTU Chapter\",\"name\":\"SIAM-DTU Orientation\",\"place\":{\"name\":\"Smartclass L2\"},\"start_time\":\"2013-10-01T17:00:00+0530\",\"id\":\"456112637834718\"},{\"description\":\"Annual Symposium of SIAM-DTU Student Chapter which aims to promote, show-case and reward the technological developments taking place in this rapidly changing world, and inspire young minds to think, discover and create. SIAM invites you to be a part of this celebration for this special year, Mathematics of Planet Earth 2013.\",\"end_time\":\"2013-04-13T17:00:00+0530\",\"name\":\"EVOLUTE'13\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2013-04-12T10:00:00+0530\",\"id\":\"492809077441502\"},{\"description\":\"Society for Industrial and Applied Mathematics, DTU Chapter Date- 17th January 2013 Day- Thursday Venue- Exposition Hall (admin block) Time- 5 pm\",\"name\":\"ORIENTATION of SIAM-DTU\",\"place\":{\"name\":\"Delhi Technological University\",\"location\":{\"city\":\"New Delhi\",\"country\":\"India\",\"latitude\":28.749757293743,\"longitude\":77.116828490517,\"street\":\"DELHI TECHNOLOGICAL UNIVERSITY (Formerly delhi college of engineering)  Shahbad daulatpur, bawana road, delhi – 110042\",\"zip\":\"110042\"},\"id\":\"210191105662655\"},\"start_time\":\"2013-01-17T17:00:00+0530\",\"id\":\"521622657882096\"}],\"paging\":{\"cursors\":{\"before\":\"MzA4NTk2MTY2MTY3NTM5\",\"after\":\"NTIxNjIyNjU3ODgyMDk2\"}}}";
        try{
            StringBuilder answer = new StringBuilder();
            JSONObject root =new JSONObject(response);
            JSONArray data =  root.getJSONArray("data");
            for(int i=0;i<data.length();i++){
                JSONObject jsonObject = data.getJSONObject(i);
                answer.append("{");
                answer.append("\"event name\":\""+jsonObject.getString("name")+"\"");
                answer.append(",\"date\":\""+jsonObject.getString("start_time")+"\"");
                if(jsonObject.has("description")) {
                    answer.append(",\"description\":\"" + jsonObject.getString("description") + "\"");
                }answer.append("},");
            }
            System.out.print("");
        }catch (Exception e){
            System.out.print(e.toString());
        }
    }
}
