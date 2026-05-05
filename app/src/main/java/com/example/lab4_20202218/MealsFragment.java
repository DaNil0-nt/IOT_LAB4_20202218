package com.example.lab4_20202218;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MealsFragment extends Fragment {

    private RecyclerView rvMeals;
    private MealAdapter adapter;
    private String categoryName = "";
    private EditText etIngredient;
    private Button btnSearchIngredient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etIngredient = view.findViewById(R.id.etIngredient);
        btnSearchIngredient = view.findViewById(R.id.btnSearchIngredient);
        rvMeals = view.findViewById(R.id.rvMeals);

        rvMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealAdapter();
        adapter.setContext(getContext());
        rvMeals.setAdapter(adapter);

        btnSearchIngredient.setOnClickListener(v -> {
            String ingredient = etIngredient.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                buscarPorIngrediente(ingredient);
            } else {
                Toast.makeText(getContext(), "Ingrese un ingrediente", Toast.LENGTH_SHORT).show();
            }
        });

        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName", "");
            if (!categoryName.isEmpty()) {
                buscarPorCategoria(categoryName);
            }
        }
    }

    private void buscarPorCategoria(String category) {
        getRetrofit().getMealsByCategory(category).enqueue(new Callback<MealsResponse>() {
            @Override
            public void onResponse(Call<MealsResponse> call, Response<MealsResponse> response) {
                actualizarLista(response);
            }
            @Override
            public void onFailure(Call<MealsResponse> call, Throwable t) {
                Log.e("Lab4", "Error", t);
            }
        });
    }

    private void buscarPorIngrediente(String ingredient) {
        getRetrofit().getMealsByIngredient(ingredient).enqueue(new Callback<MealsResponse>() {
            @Override
            public void onResponse(Call<MealsResponse> call, Response<MealsResponse> response) {
                actualizarLista(response);
            }
            @Override
            public void onFailure(Call<MealsResponse> call, Throwable t) {
                Log.e("Lab4", "Error", t);
            }
        });
    }

    private void actualizarLista(Response<MealsResponse> response) {
        if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
            adapter.setMealList(response.body().getMeals());
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "NO HAY PLATOS", Toast.LENGTH_SHORT).show();
        }
    }

    private MealApiService getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealApiService.class);
    }
}