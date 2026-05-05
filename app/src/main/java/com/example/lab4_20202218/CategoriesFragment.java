package com.example.lab4_20202218;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriesFragment extends Fragment implements SensorEventListener {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isFetchingRandom = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter();
        adapter.setContext(getContext());
        rvCategories.setAdapter(adapter);

        obtenerCategorias();

        if (getActivity() != null) {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && !isFetchingRandom) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.STANDARD_GRAVITY;

            if (acceleration > 4.0) {
                isFetchingRandom = true;
                Toast.makeText(getContext(), "RECETA ALEATORIO", Toast.LENGTH_SHORT).show();
                obtenerRecetaAleatoria(getView());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void obtenerCategorias() {
        MealApiService apiService = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealApiService.class);

        apiService.getCategories().enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setCategoryList(response.body().getCategories());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.e("Lab4", "Fallo", t);
            }
        });
    }

    private void obtenerRecetaAleatoria(View view) {
        MealApiService apiService = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MealApiService.class);

        apiService.getRandomRecipe().enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                isFetchingRandom = false;
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    Recipe randomRecipe = response.body().getMeals().get(0);
                    String randomMealId = randomRecipe.getIdMeal();

                    Bundle bundle = new Bundle();
                    bundle.putString("idMeal", randomMealId);

                    if (view != null) {
                        Navigation.findNavController(view).navigate(R.id.action_categoriesFragment_to_recipeFragment, bundle);
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                isFetchingRandom = false;
                Log.e("Lab4", "Fallo", t);
            }
        });
    }
}