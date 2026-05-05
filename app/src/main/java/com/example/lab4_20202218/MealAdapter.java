package com.example.lab4_20202218;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> mealList;
    private Context context;

    public void setMealList(List<Meal> mealList) {
        this.mealList = mealList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);

        holder.tvMealName.setText(meal.getStrMeal());
        holder.tvMealId.setText("ID: " + meal.getIdMeal());

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .into(holder.ivMealThumb);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("idMeal", meal.getIdMeal());
            Navigation.findNavController(v).navigate(R.id.action_mealsFragment_to_recipeFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return mealList == null ? 0 : mealList.size();
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName, tvMealId;
        ImageView ivMealThumb;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealId = itemView.findViewById(R.id.tvMealId);
            ivMealThumb = itemView.findViewById(R.id.ivMealThumb);
        }
    }
}
