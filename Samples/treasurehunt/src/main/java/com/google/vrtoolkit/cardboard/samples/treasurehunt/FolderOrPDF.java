package com.google.vrtoolkit.cardboard.samples.treasurehunt;

/**
 * Created by Quentin on 10/05/2016.
 */
public class FolderOrPDF implements Comparable<FolderOrPDF>  {
    private String path;
    private String name;
    private String type;

    FolderOrPDF(String path){
        this.path = path;
        String[] separated = this.path.split("\\/");
        this.name = separated[separated.length-1];
        this.type = null;
    }

    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public int compareTo(FolderOrPDF another) {
        return this.getName().compareTo(another.getName());
    }

}
