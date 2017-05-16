package com.example.anais.ig2work;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ExpandableListView;

import android.widget.ListView;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.HomeworkExpandableAdapter;
import com.example.anais.ig2work.Model.Seance;
import com.example.anais.ig2work.Model.SeanceAdapter;
import com.example.anais.ig2work.Utils.ListUtils;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
La classe HomeActivity gère la page d'accueil de l'application, une fois l'utilisateur connecté.
Cette page comporte deux onglets : séances & devoirs (l'onglet devoirs n'est disponible que pour
les étudiants).
Dans chacun des onglets, on récupère et on affiche les données sous forme de liste.  L'utilisateur
peut choisir de voir les séances/devoirs du jour courant, des 7 prochains jours ou du mois courant.
Lorsque l'utilisateur clique sur une séance ou un devoir, il accède à la page détaillée.
 */
public class HomeActivity extends RestActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private SharedPreferences preferences;

    int idUser;
    Date dateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},  1);

        //addEvent();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        idUser = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getApplicationContext()).getInt(StringUtils.IDUSER.toString(), 0);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},  1);
    }

    /* private void addEvent() {
        Log.e("addEvent", "ok");
        ContentResolver cr = getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, new Date("05/02/2017 10:00:00").getTime());
        values.put(CalendarContract.Events.TITLE, "Devoir");
        values.put(CalendarContract.Events.DESCRIPTION, "Ceci est la description");
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        //values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=");
        values.put(CalendarContract.Events.DURATION, "+P1H");
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Check", "denied");
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_create_seance:
                Intent intent = new Intent(HomeActivity.this, AddSeanceActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickChangeActivity(String activity, Bundle data) {
        Intent intent = new Intent();

        switch (activity) {
            case "seance":
                intent = new Intent(HomeActivity.this, SeanceActivity.class);
                break;
            case "homework":
                intent = new Intent(HomeActivity.this, HomeworkActivity.class);
                break;
        }

        intent.putExtras(data);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private CalendarView calendarView;
        private ListView seanceListView;
        private ExpandableListView listView;
        private Date dateNow;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            calendarView = (CalendarView) rootView.findViewById(R.id.simpleCalendarView);
            seanceListView = (ListView) rootView.findViewById(R.id.section_scrollListView);
            listView = (ExpandableListView) rootView.findViewById(R.id.section_listView);

            int idUser = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(StringUtils.IDUSER.toString(), 0);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1: // Onglet Séances
                    calendarView.setVisibility(View.VISIBLE);
                    seanceListView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    getSeances(idUser);
                    break;
                case 2: // Onglet Devoirs
                    calendarView.setVisibility(View.GONE);
                    seanceListView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    getAllHomeworks(idUser);
                    break;
            }

            return rootView;
        }

        public void getSeances(final int idUser) {
            getAllSeances(idUser, Calendar.getInstance().getTime());

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, final int dayOfMonth) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    dateNow = cal.getTime();

                    getAllSeances(idUser, dateNow);
                }
            });
        }

        public void getAllSeances(final int idUser, final Date dateNow) {
            new RequestActivity() {
                @Override
                public void traiteReponse(JSONObject o, String action) {

                    if(!o.isNull("feedback")) {
                        Toast.makeText(getBaseContext(), "Utilisateur non reconnu...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                        List<Seance> listSeances = new ArrayList<Seance>();

                        // Le rôle de l'utilisateur est utilisé pour instancier l'objet Seance
                        // On s'en sert dans la gestion de l'affichage (affichage du nom de l'enseignant ou de la promo)
                        String target = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(StringUtils.ROLE.toString(), "");

                        // Liste des séances
                        JSONArray seances = o.getJSONArray("seances");

                        for (int i = 0; i < seances.length(); i++) {

                            JSONObject seance = seances.getJSONObject(i);

                            int id = seance.getInt("id");
                            String moduleName = seance.getString("moduleName");
                            String teacherFName = seance.getString("teacherFirstName");
                            String teacherLName = seance.getString("teacherLastName");
                            String promoName = seance.getString("promoName");
                            String dayTime = seance.getString("dayTime");
                            String room = seance.getString("room");

                            Seance s = new Seance(id, moduleName, teacherFName + " " + teacherLName, promoName, formatter.parse(dayTime), room, target);

                            Calendar cal = Calendar.getInstance();

                            Date dateSeance = formatter.parse(dayTime);

                            if (dateNow.getYear() == dateSeance.getYear()
                                    && dateNow.getMonth() == dateSeance.getMonth()
                                    && dateNow.getDate() == dateSeance.getDate()) {
                                listSeances.add(s);
                            }
                        }

                        SeanceAdapter adapter = new SeanceAdapter(getActivity(), listSeances);
                        seanceListView.setAdapter(adapter);

                        seanceListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Seance seanceChoice = (Seance) seanceListView.getAdapter().getItem(i);

                                Bundle data = new Bundle();
                                data.putInt("idSeance", seanceChoice.getId());

                                HomeActivity activity = (HomeActivity) getActivity();
                                activity.onClickChangeActivity("seance", data);
                            }
                        });

                        ListUtils.setDynamicHeight(seanceListView);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }.envoiRequete("getAllSeance", "action=getAllSeance&idUser=" + idUser);
        }

        public void getAllHomeworks(final int idUser) {
            new RequestActivity() {
                @Override
                public void traiteReponse(JSONObject o, String action) {

                    if(!o.isNull("feedback")) {
                        Toast.makeText(getBaseContext(), "Utilisateur non reconnu...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                        List<Homework> listHomeworksDay = new ArrayList<Homework>();
                        List<Homework> listHomeworksWeek = new ArrayList<Homework>();
                        List<Homework> listHomeworksMonth = new ArrayList<Homework>();

                        // On crée 3 filtres sur la liste des séances
                        // Ainsi l'utilisateur pourra choisir d'afficher les séances du jour, de la semaine ou du mois à venir
                        List<String> listFilter = new ArrayList<String>();
                        listFilter.add("Aujourd'hui");
                        listFilter.add("7 prochains jours");
                        listFilter.add("Ce mois-ci");

                        // Liste des séances
                        JSONArray homeworks = o.getJSONArray("retour");

                        for (int i = 0; i < homeworks.length(); i++) {

                            JSONObject homework = homeworks.getJSONObject(i);

                            int id = homework.getInt("id");
                            String moduleName = homework.getString("moduleName");
                            String title = homework.getString("titre");
                            String description = homework.getString("description");
                            String dueDate = homework.getString("dueDate");

                            Boolean isVisible = false;
                            if (!homework.isNull("isVisible")) {
                                isVisible = homework.getString("isVisible").equals("1") ? true : false;
                            }

                            Boolean realized = false;
                            if (!homework.isNull("realized")) {
                                realized = homework.getString("realized").equals("1") ? true : false;
                            }

                            Homework h = new Homework(id, moduleName, title, description, formatter.parse(dueDate), realized, isVisible);

                            Date dateHomework = formatter.parse(dueDate);
                            Date dateNow = new Date();
                            long diffDate = (dateHomework.getTime() - dateNow.getTime()) / (24 * 60 * 60 * 1000);

                            // En fonction de la date d'échéance du devoir, on l'ajoute dans les listes qui correspondent
                            if (diffDate == 0) {
                                listHomeworksDay.add(h);
                                listHomeworksWeek.add(h);
                                listHomeworksMonth.add(h);
                            } else if (diffDate > 0 && diffDate < 7) {
                                listHomeworksWeek.add(h);
                                listHomeworksMonth.add(h);
                            } else if (diffDate >= 7) {
                                listHomeworksMonth.add(h);
                            }
                        }

                        HashMap<String, List<Homework>> mapHomeworks = new HashMap<String, List<Homework>>();
                        mapHomeworks.put(listFilter.get(0), listHomeworksDay);
                        mapHomeworks.put(listFilter.get(1), listHomeworksWeek);
                        mapHomeworks.put(listFilter.get(2), listHomeworksMonth);

                        HomeworkExpandableAdapter adapter = new HomeworkExpandableAdapter(getContext(), listView, listFilter, mapHomeworks);
                        listView.setAdapter(adapter);

                        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                                Homework homeworkChoice = (Homework) listView.getExpandableListAdapter().getChild(i, i1);

                                Bundle data = new Bundle();
                                data.putInt("idHomework", homeworkChoice.getId());

                                HomeActivity activity = (HomeActivity) getActivity();
                                activity.onClickChangeActivity("homework", data);

                                return true;
                            }
                        });

                    } catch (ParseException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.envoiRequete("getHomeWorkByUser", "action=getHomeWorkByUser&idUser=" + idUser);//"action=getAllHomeworks&idUser=" + idUser);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

            // Les étudiants voient 2 onglets (séances & devoirs)
            // Les enseignants n'ont que l'onglet des séances
            switch (preferences.getString(StringUtils.ROLE.toString(), "")) {
                case "student" :
                    return 2;
                case "teacher" :
                    return 1;
            }

            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.seances);
                case 1:
                    return getString(R.string.homeworks);
            }

            return null;
        }
    }
}
