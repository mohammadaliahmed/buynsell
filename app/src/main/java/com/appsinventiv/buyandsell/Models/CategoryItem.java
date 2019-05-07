package com.appsinventiv.buyandsell.Models;

/**
 * Created by AliAh on 04/01/2018.
 */

public class CategoryItem {
    String itemName;
    int icon;

    public CategoryItem(String itemName, int icon) {
        this.itemName = itemName;
        this.icon = icon;
    }

    public CategoryItem() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
