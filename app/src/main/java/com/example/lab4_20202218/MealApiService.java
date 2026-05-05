package com.example.lab4_20202218;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("api/json/v1/1/categories.php")
    Call<CategoriesResponse> getCategories();

    @GET("api/json/v1/1/filter.php")
    Call<MealsResponse> getMealsByCategory(@Query("c") String category);

    @GET("api/json/v1/1/lookup.php")
    Call<RecipeResponse> getRecipeById(@Query("i") String id);

    @GET("api/json/v1/1/random.php")
    Call<RecipeResponse> getRandomRecipe();
    @GET("api/json/v1/1/filter.php")
    Call<MealsResponse> getMealsByIngredient(@Query("i") String ingredient);
}