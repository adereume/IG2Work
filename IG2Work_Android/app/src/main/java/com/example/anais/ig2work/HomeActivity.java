package com.example.anais.ig2work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.HomeworkAdapter;
import com.example.anais.ig2work.Model.Seance;
import com.example.anais.ig2work.Model.SeanceAdapter;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends RestActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
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
        private ExpandableListView listView;

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
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            listView = (ExpandableListView) rootView.findViewById(R.id.section_listView);

            int idUser = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(StringUtils.IDUSER.toString(), 0);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

                // Onglet Séances
                case 1:
                    getAllSeances(idUser);
                    break;

                // Onglet Devoirs
                case 2:
                    getAllHomeworks(idUser);
                    break;
            }

            return rootView;
        }

        public void getAllSeances(final int idUser) {
            new RequestActivity() {
                @Override
                public void traiteReponse(JSONObject o, String action) {

                    if(!o.isNull("feedback")) {
                        Toast.makeText(getBaseContext(), "Utilisateur non reconnu...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                        List<Seance> listSeancesDay = new ArrayList<Seance>();
                        List<Seance> listSeancesWeek = new ArrayList<Seance>();
                        List<Seance> listSeancesMonth = new ArrayList<Seance>();

                        // Le rôle de l'utilisateur est utilisé pour instancier l'objet Seance
                        // On s'en sert dans la gestion de l'affichage (affichage du nom de l'enseignant ou de la promo)
                        String target = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(StringUtils.ROLE.toString(), "");

                        // On crée 3 filtres sur la liste des séances
                        // Ainsi l'utilisateur pourra choisir d'afficher les séances du jour, de la semaine ou du mois à venir
                        List<String> listFilter = new ArrayList<String>();
                        listFilter.add("Aujourd'hui");
                        listFilter.add("7 prochains jours");
                        listFilter.add("Ce mois-ci");

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

                            Date dateSeance = formatter.parse(dayTime);
                            Date dateNow = formatter.parse("2017-01-16 00:00:00");//new Date();
                            long diffDate = (dateSeance.getTime() - dateNow.getTime()) / (24 * 60 * 60 * 1000);

                            if (diffDate == 0) {
                                listSeancesDay.add(s);
                                listSeancesWeek.add(s);
                                listSeancesMonth.add(s);
                            } else if (diffDate < 7) {
                                listSeancesWeek.add(s);
                                listSeancesMonth.add(s);
                            } else {
                                listSeancesMonth.add(s);
                            }
                        }

                        HashMap<String, List<Seance>> mapSeances = new HashMap<String, List<Seance>>();
                        mapSeances.put(listFilter.get(0), listSeancesDay);
                        mapSeances.put(listFilter.get(1), listSeancesWeek);
                        mapSeances.put(listFilter.get(2), listSeancesMonth);

                        SeanceAdapter adapter = new SeanceAdapter(getActivity(), listView, listFilter, mapSeances);
                        listView.setAdapter(adapter);

                        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                                Seance seanceChoice = (Seance) listView.getExpandableListAdapter().getChild(i, i1);

                                Bundle data = new Bundle();
                                data.putInt("idSeance", seanceChoice.getId());

                                HomeActivity activity = (HomeActivity) getActivity();
                                activity.onClickChangeActivity("seance", data);

                                return true;
                            }
                        });

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
                        /*JSONArray homeworks = o.getJSONArray("homeworks");

                        for (int i = 0; i < homeworks.length(); i++) {

                            JSONObject homework = homeworks.getJSONObject(i);

                            int id = homework.getInt("id");
                            String moduleName = homework.getString("moduleName");
                            String title = homework.getString("title");
                            String description = homework.getString("description");
                            String dueDate = homework.getString("dueDate");
                            Boolean realized = homework.getBoolean("realized");

                            Homework h = new Homework(id, moduleName, title, description, formatter.parse(dueDate), realized);

                            Date dateHomework = formatter.parse(dueDate);
                            Date dateNow = formatter.parse("2017-01-16 00:00:00");//new Date();
                            long diffDate = (dateHomework.getTime() - dateNow.getTime()) / (24 * 60 * 60 * 1000);

                            if (diffDate == 0) {
                                listHomeworksDay.add(h);
                                listHomeworksWeek.add(h);
                                listHomeworksMonth.add(h);
                            } else if (diffDate < 7) {
                                listHomeworksWeek.add(h);
                                listHomeworksMonth.add(h);
                            } else {
                                listHomeworksMonth.add(h);
                            }
                        }*/

                        Homework h1 = new Homework(1, "Projet Mobile", "Rédiger la doc fonctionnelle", "", formatter.parse("2017-01-18 08:00:00"), false);
                        Homework h2 = new Homework(2, "Projet Mobile", "Commencer les dévs", "", formatter.parse("2017-01-22 13:30:00"), true);
                        Homework h3 = new Homework(3, "Anglais", "Préparer oral technique", "", formatter.parse("2017-01-31 15:45:00"), false);

                        listHomeworksWeek.add(h1);
                        listHomeworksWeek.add(h2);
                        listHomeworksMonth.add(h1);
                        listHomeworksMonth.add(h2);
                        listHomeworksMonth.add(h3);

                        HashMap<String, List<Homework>> mapHomeworks = new HashMap<String, List<Homework>>();
                        mapHomeworks.put(listFilter.get(0), listHomeworksDay);
                        mapHomeworks.put(listFilter.get(1), listHomeworksWeek);
                        mapHomeworks.put(listFilter.get(2), listHomeworksMonth);

                        HomeworkAdapter adapter = new HomeworkAdapter(getContext(), listView, listFilter, mapHomeworks);
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

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }.envoiRequete("getAllHomeworks", "action=getAllSeances&idUser=" + idUser);//"action=getAllHomeworks&idUser=" + idUser);
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
                    return "Séances";
                case 1:
                    return "Devoirs";
            }

            return null;
        }
    }
}
