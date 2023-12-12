package com.example.pokemonwatchlist_lesh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    Button search;
    Button clear;
    Button clearList;
    EditText input;
    ListView listView;
    LinkedList<PokeEntry> pokeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search_button);
        search.setOnClickListener(searchListener);
        clear = findViewById(R.id.clear_button);
        clear.setOnClickListener(clearListener);
        clearList = findViewById(R.id.clearlist_button);
        clearList.setOnClickListener(clearListListener);
        listView = findViewById(R.id.listV);
        input = findViewById(R.id.input_ET);

        search.setBackgroundColor(getResources().getColor(R.color.red));
        clear.setBackgroundColor(getResources().getColor(R.color.red));
        clearList.setBackgroundColor(getResources().getColor(R.color.red));


        pokeList = new LinkedList<>();
        listView.setOnItemClickListener(listListener);
    }

    View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pokemon = input.getText().toString();
            makeRequest(pokemon);
            if(check(pokemon)){
               addPokemon(pokemon);
            }
            else{
                Toast.makeText(getApplicationContext(), "The pokemon entered is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void addPokemon(String pokemon){
        ANRequest req = AndroidNetworking.get("https://pokeapi.co/api/v2/pokemon/{ditto}")
                .addPathParameter("ditto", pokemon)
                .setPriority(Priority.LOW)
                .build();
        req.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    Integer id = response.getInt("id");

                    pokeList.add(new PokeEntry(name, id));
                    if(listView.getAdapter() != null){
                        ((ArrayAdapter<PokeEntry>) listView.getAdapter()).notifyDataSetChanged();
                    }
                    else{
                        ArrayAdapter<PokeEntry> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, pokeList);
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
//        ArrayAdapter<PokeEntry> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pokeList);
//        listView.setAdapter(adapter);
    }

    private void makeRequest(String pokemon){
        ANRequest req = AndroidNetworking.get("https://pokeapi.co/api/v2/pokemon/{ditto}")
                .addPathParameter("ditto", pokemon)
                .setPriority(Priority.LOW)
                .build();
        req.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ability = response.getJSONArray("abilities").getJSONObject(0).getJSONObject("ability").getString("name");
                    Integer base_XP = response.getInt("base_experience");
                    Integer height = response.getInt("height");
                    Integer id = response.getInt("id");
                    String move = response.getJSONArray("moves").getJSONObject(0).getJSONObject("move").getString("name");
                    String name = response.getString("name");
                    Integer weight = response.getInt("weight");
                    String image = response.getJSONObject("sprites").getString("front_default");
                    ImageView iv = findViewById(R.id.imageView);
                    Picasso.get().load(image).into(iv);
                    //set text views
                    ((TextView) findViewById(R.id.name_TV)).setText(name);
                    ((TextView) findViewById(R.id.number_TV)).setText(id.toString());
                    ((TextView) findViewById(R.id.weight_TV)).setText(weight.toString());
                    ((TextView) findViewById(R.id.height_TV)).setText(height.toString());
                    ((TextView) findViewById(R.id.move_TV)).setText(move);
                    ((TextView) findViewById(R.id.ability_TV)).setText(ability);
                    ((TextView) findViewById(R.id.base_TV)).setText(base_XP.toString());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getApplicationContext(), "Please enter a valid pokemon name or id", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean check(String pokemon){
        if(pokemon.contains("%") || pokemon.contains("&") || pokemon.contains("*") || pokemon.contains("(") || pokemon.contains("@")
                || pokemon.contains(")") || pokemon.contains("!") || pokemon.contains(";") || pokemon.contains(":")
                || pokemon.contains("<") || pokemon.contains(">") || pokemon.isEmpty()){
            //toast
            String message = "The input contains invalid characters/is empty";
            return false;
        }
        if(isNumber(pokemon)){
            if(Integer.valueOf(pokemon) < 0 || Integer.valueOf(pokemon) > 1010){
                String message = "The id is not valid";
                //make toast
                return false;
            }
            for(int i = 0; i < pokeList.size(); i++) {
                if (pokeList.get(i).getId() == Integer.valueOf(pokemon)) {
                    String message = "this pokemon is already in the list";
                    //toast
                    return false;
                }
            }
        }
        for(int i = 0; i < pokeList.size(); i++){
            if(pokeList.get(i).getName() == pokemon){
                String message = "this pokemon is already in the list";
                //toast
                return false;
            }
        }
        return true;
    }

    private boolean isNumber(String s){
        for(int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isWord(String s){
        for(int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PokeEntry selected = (PokeEntry) parent.getItemAtPosition(position);
            makeRequest(selected.getName());  //.toString()
        }
    };

    View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((TextView) findViewById(R.id.name_TV)).setText("");
            ((TextView) findViewById(R.id.number_TV)).setText("");
            ((TextView) findViewById(R.id.weight_TV)).setText("");
            ((TextView) findViewById(R.id.height_TV)).setText("");
            ((TextView) findViewById(R.id.move_TV)).setText("");
            ((TextView) findViewById(R.id.ability_TV)).setText("");
            ((TextView) findViewById(R.id.base_TV)).setText("");
            ImageView iv = findViewById(R.id.imageView);
            iv.setImageDrawable(null);
            input.setText("");
        }
    };

    View.OnClickListener clearListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pokeList.removeAll(pokeList);
            ((ArrayAdapter<PokeEntry>) listView.getAdapter()).notifyDataSetChanged();
        }
    };

}