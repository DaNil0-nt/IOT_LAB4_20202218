package com.example.lab4_20202218;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeFragment extends Fragment {

    private EditText etMealId;
    private Button btnSearchRecipe;
    private TextView tvRecipeName, tvRecipeCategoryArea, tvIngredients, tvInstructions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etMealId = view.findViewById(R.id.etMealId);
        btnSearchRecipe = view.findViewById(R.id.btnSearchRecipe);
        tvRecipeName = view.findViewById(R.id.tvRecipeName);
        tvRecipeCategoryArea = view.findViewById(R.id.tvRecipeCategoryArea);
        tvIngredients = view.findViewById(R.id.tvIngredients);
        tvInstructions = view.findViewById(R.id.tvInstructions);

        btnSearchRecipe.setOnClickListener(v -> {
            String id = etMealId.getText().toString().trim();
            if (!id.isEmpty()) {
                buscarReceta(id);
            }
        });

        if (getArguments() != null) {
            String idMeal = getArguments().getString("idMeal", "");
            if (!idMeal.isEmpty()) {
                etMealId.setText(idMeal);
                buscarReceta(idMeal);
            }
        }
    }

    private void buscarReceta(String id) {
        MealApiService apiService = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealApiService.class);

        apiService.getRecipeById(id).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    Recipe recipe = response.body().getMeals().get(0);
                    mostrarDetalles(recipe);
                } else {
                    Toast.makeText(getContext(), "NO ENCONTRADA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("Lab4", "FALLO", t);
            }
        });
    }

    private void mostrarDetalles(Recipe recipe) {
        tvRecipeName.setText(recipe.getStrMeal());
        tvRecipeCategoryArea.setText("Categoría: " + recipe.getStrCategory() + " | Origen: " + recipe.getStrArea());

        String ingredientes = "1. " + recipe.getStrIngredient1() + "\n" +
                "2. " + recipe.getStrIngredient2() + "\n" +
                "3. " + recipe.getStrIngredient3();
        tvIngredients.setText(ingredientes);

        tvInstructions.setText(recipe.getStrInstructions());
    }
}