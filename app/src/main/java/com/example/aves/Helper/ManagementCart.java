package com.example.aves.Helper;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.aves.Domain.FoodDomain;
import com.example.aves.Interface.ChangeNumberItemsListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertFood(FoodDomain item, View view) {
        ArrayList<FoodDomain> listFood = getListCard();
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listFood.size(); i++) {
            if (listFood.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }

        if (existAlready) {
            listFood.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            listFood.add(item);
        }

        tinyDB.putListObject("CardList", listFood);
        Snackbar.make(view, item.getTitle() + " added to cart.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();


    }

    public ArrayList<FoodDomain> getListCard() {
        return tinyDB.getListObject("CardList");
    }

    public void plusNumberFood(ArrayList<FoodDomain> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listfood.get(position).setNumberInCart(listfood.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CardList", listfood);
        changeNumberItemsListener.changed();
    }

    public void MinusNumerFood(ArrayList<FoodDomain> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listfood.get(position).getNumberInCart() == 1) {
            listfood.remove(position);
        } else {
            listfood.get(position).setNumberInCart(listfood.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CardList", listfood);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<FoodDomain> listFood2 = getListCard();
        double fee = 0;
        for (int i = 0; i < listFood2.size(); i++) {
            fee = fee + (listFood2.get(i).getFee() * listFood2.get(i).getNumberInCart());
        }
        return fee;
    }

    public int getItemsCount() {
        ArrayList<FoodDomain> listFood2 = getListCard();
        int count = 0;
        for (int i = 0; i < listFood2.size(); i++) {
            count += listFood2.get(i).getNumberInCart();
        }
        return count;
    }


    public void clearCart() {
        ArrayList<FoodDomain> listFood2 = new ArrayList<>();
        tinyDB.putListObject("CardList", listFood2);
    }
}
