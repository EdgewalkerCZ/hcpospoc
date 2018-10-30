package com.example.vaibhavchahal93788.myapplication.billdesk.model;

public class AllCategory {

    private long categoryId;
    private String category;
    private String[] subCategory;
    private String[] subCategoryId;
    private String image;

    private boolean selected;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String[] subCategory) {
        this.subCategory = subCategory;
    }

    public String[] getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String[] subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
